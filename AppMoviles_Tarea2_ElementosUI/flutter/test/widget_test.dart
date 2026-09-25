import 'package:catalogo_ui/comun/textos.dart';
import 'package:catalogo_ui/main.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('La pantalla de inicio muestra el título y las seis secciones', (tester) async {
    await tester.pumpWidget(const CatalogoApp());
    await tester.pumpAndSettle();

    expect(find.text(T.inicioTitulo), findsOneWidget);
    expect(find.textContaining('Sección 1'), findsOneWidget);
  });

  test('f() rellena las plantillas con el formato de Android', () {
    expect(f(T.s1SimpleEco, ['Mango']), 'Estás escribiendo: Mango');
    expect(f(T.s5ProgresoValor, [40]), 'Determinado: 40 %');
  });
}
