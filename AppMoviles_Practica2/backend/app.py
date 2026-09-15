from flask import Flask, jsonify, request, g
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_cors import CORS
from functools import wraps
from datetime import datetime, timedelta, timezone
import os
import jwt

app = Flask(__name__)
CORS(app)  # permite que la web (otro origen) llame a esta API

# 1. Configuración de la Base de Datos (SQLite)
# El archivo se guardará en la carpeta del contenedor como 'site.db'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)
bcrypt = Bcrypt(app)

# 2. Modelo de Usuario (La tabla en la BD)

# La llave para firmar los JWT NUNCA se escribe fija en el código. Si no viene por
# variable de entorno, usamos una de desarrollo y avisamos en consola (regla del
# profesor: el proyecto tiene que arrancar sí o sí, pero debe avisar qué falta).
SECRET_KEY = os.environ.get("SECRET_KEY")
if not SECRET_KEY:
    SECRET_KEY = "clave-de-desarrollo-cambiame"
    print(
        "ADVERTENCIA: SECRET_KEY no configurada por variable de entorno. "
        "Usando una clave de desarrollo NO segura. Copia backend/.env.example a "
        "backend/.env y define tu propia SECRET_KEY antes de usar esto en produccion."
    )


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(20), unique=True, nullable=False)
    password = db.Column(db.String(60), nullable=False)  # hash, nunca texto plano
    role = db.Column(db.String(10), nullable=False, default='user')

    def __repr__(self):
        return f"User('{self.username}', '{self.role}')"
    
def generar_token(user):
    payload = {
        "user_id": user.id,
        "role": user.role,
        "exp": datetime.now(timezone.utc) + timedelta(hours=2),
    }
    return jwt.encode(payload, SECRET_KEY, algorithm="HS256")


def requiere_token(f):
    """Decorador: exige un JWT válido en el header Authorization: Bearer <token>.
    Deja user_id y role disponibles en `g` para la vista que decore."""
    @wraps(f)
    def wrapper(*args, **kwargs):
        auth_header = request.headers.get('Authorization', '')
        if not auth_header.startswith('Bearer '):
            return jsonify({"message": "Falta el token de autenticación"}), 401

        token = auth_header.split(' ', 1)[1]
        try:
            payload = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
        except jwt.ExpiredSignatureError:
            return jsonify({"message": "El token expiró, inicia sesión de nuevo"}), 401
        except jwt.InvalidTokenError:
            return jsonify({"message": "Token inválido"}), 401

        g.user_id = payload['user_id']
        g.role = payload['role']
        return f(*args, **kwargs)
    return wrapper

class Tarea(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    titulo = db.Column(db.String(100), nullable=False)
    descripcion = db.Column(db.String(300), nullable=True)
    completada = db.Column(db.Boolean, default=False)
    owner_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)

    def to_dict(self):
        return {
            "id": self.id,
            "titulo": self.titulo,
            "descripcion": self.descripcion,
            "completada": self.completada,
            "owner_id": self.owner_id,
        }

# 3. Rutas

@app.route('/')
def hello():
    return jsonify({"message": "API Funcionando"})

# Endpoint de REGISTRO
@app.route('/register', methods=['POST'])
def register():
    data = request.get_json(silent=True) or {}
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({"message": "Usuario y contraseña son obligatorios"}), 400

    if User.query.filter_by(username=username).first():
        return jsonify({"message": "El usuario ya existe"}), 400

    hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
    new_user = User(username=username, password=hashed_password, role='user')
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "Usuario creado exitosamente"}), 201

# Endpoint de LOGIN
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json(silent=True) or {}
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({"status": "error", "message": "Usuario y contraseña son obligatorios"}), 400

    user = User.query.filter_by(username=username).first()

    if user and bcrypt.check_password_hash(user.password, password):
        token = generar_token(user)
        return jsonify({
            "status": "success",
            "message": "Login exitoso",
            "token": token,
            "user_id": user.id,
            "username": user.username,
            "role": user.role,
        }), 200
    else:
        return jsonify({"status": "error", "message": "Credenciales inválidas"}), 401

def _obtener_tarea_o_error(tarea_id):
    """Busca la tarea y valida permisos: dueño o admin. Regresa (tarea, None) o
    (None, (respuesta, codigo)) listo para hacer `return` directo."""
    tarea = Tarea.query.get(tarea_id)
    if not tarea:
        return None, (jsonify({"message": "Tarea no encontrada"}), 404)
    if tarea.owner_id != g.user_id and g.role != 'admin':
        return None, (jsonify({"message": "No tienes permiso sobre esta tarea"}), 403)
    return tarea, None


@app.route('/tareas', methods=['POST'])
@requiere_token
def crear_tarea():
    data = request.get_json(silent=True) or {}
    titulo = data.get('titulo')
    if not titulo:
        return jsonify({"message": "El título es obligatorio"}), 400

    nueva = Tarea(
        titulo=titulo,
        descripcion=data.get('descripcion', ''),
        owner_id=g.user_id,
    )
    db.session.add(nueva)
    db.session.commit()
    return jsonify(nueva.to_dict()), 201


@app.route('/tareas', methods=['GET'])
@requiere_token
def listar_tareas():
    # Regla de roles: el admin ve todo, el usuario normal solo lo suyo.
    if g.role == 'admin':
        tareas = Tarea.query.all()
    else:
        tareas = Tarea.query.filter_by(owner_id=g.user_id).all()
    return jsonify([t.to_dict() for t in tareas]), 200


@app.route('/tareas/<int:tarea_id>', methods=['GET'])
@requiere_token
def obtener_tarea(tarea_id):
    tarea, error = _obtener_tarea_o_error(tarea_id)
    if error:
        return error
    return jsonify(tarea.to_dict()), 200


@app.route('/tareas/<int:tarea_id>', methods=['PUT'])
@requiere_token
def actualizar_tarea(tarea_id):
    tarea, error = _obtener_tarea_o_error(tarea_id)
    if error:
        return error

    data = request.get_json(silent=True) or {}
    if 'titulo' in data:
        tarea.titulo = data['titulo']
    if 'descripcion' in data:
        tarea.descripcion = data['descripcion']
    if 'completada' in data:
        tarea.completada = bool(data['completada'])
    db.session.commit()
    return jsonify(tarea.to_dict()), 200


@app.route('/tareas/<int:tarea_id>', methods=['DELETE'])
@requiere_token
def borrar_tarea(tarea_id):
    tarea, error = _obtener_tarea_o_error(tarea_id)
    if error:
        return error

    db.session.delete(tarea)
    db.session.commit()
    return jsonify({"message": "Tarea eliminada"}), 200

if __name__ == '__main__':
    # Esto crea las tablas automáticamente si no existen al iniciar
    with app.app_context():
        db.create_all()
    
    app.run(host='0.0.0.0', port=5000, debug=True)