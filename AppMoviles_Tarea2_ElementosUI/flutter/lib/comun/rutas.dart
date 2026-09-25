import 'package:flutter/material.dart';

import '../inicio.dart';
import '../secciones/s1_entrada_texto.dart';
import '../secciones/s2_botones.dart';
import '../secciones/s3_seleccion.dart';
import '../secciones/s4_listas.dart';
import '../secciones/s5_informacion.dart';
import '../secciones/s6_contenedores.dart';
import 'textos.dart';

/// Rutas (pantallas) de la app: Inicio y las seis secciones.
enum Ruta {
  inicio('/', T.tituloInicio, T.inicioDescripcion, Icons.home),
  entrada('/entrada-texto', T.tituloS1, T.inicioDescS1, Icons.edit),
  botones('/botones', T.tituloS2, T.inicioDescS2, Icons.touch_app),
  seleccion('/seleccion', T.tituloS3, T.inicioDescS3, Icons.check_box),
  listas('/listas', T.tituloS4, T.inicioDescS4, Icons.list),
  informacion('/informacion', T.tituloS5, T.inicioDescS5, Icons.info),
  contenedores('/contenedores', T.tituloS6, T.inicioDescS6, Icons.dashboard);

  const Ruta(this.nombre, this.titulo, this.descripcion, this.icono);

  final String nombre;
  final String titulo;
  final String descripcion;
  final IconData icono;

  Widget pantalla() => switch (this) {
        Ruta.inicio => const PantallaInicio(),
        Ruta.entrada => const SeccionEntradaTexto(),
        Ruta.botones => const SeccionBotones(),
        Ruta.seleccion => const SeccionSeleccion(),
        Ruta.listas => const SeccionListas(),
        Ruta.informacion => const SeccionInformacion(),
        Ruta.contenedores => const SeccionContenedores(),
      };
}

/// Va a una sección dejando solo Inicio debajo; así Atrás siempre regresa a Inicio.
void irA(BuildContext context, Ruta ruta) {
  if (ruta == Ruta.inicio) {
    Navigator.popUntil(context, ModalRoute.withName(Ruta.inicio.nombre));
  } else {
    Navigator.pushNamedAndRemoveUntil(context, ruta.nombre, ModalRoute.withName(Ruta.inicio.nombre));
  }
}
