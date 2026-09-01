import 'package:flutter/material.dart';

void main() => runApp(const HolaMundoApp());

class HolaMundoApp extends StatelessWidget {
  const HolaMundoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Hola Mundo Flutter',
      home: const Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Hola Mundo',
                style: TextStyle(fontSize: 34, fontWeight: FontWeight.bold),
              ),
              Text('Miguel Angel Rodriguez Candelario', style: TextStyle(fontSize: 18)),
              Text('Boleta: 2024630606', style: TextStyle(fontSize: 18)),
              Text('Grupo: 6CV3', style: TextStyle(fontSize: 18)),
            ],
          ),
        ),
      ),
    );
  }
}