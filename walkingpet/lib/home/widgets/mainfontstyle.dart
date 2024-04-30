import 'package:flutter/material.dart';

class MainFontStyle extends StatelessWidget {
  final double size;
  final String text;
  final Color color;

  const MainFontStyle({
    super.key,
    required this.size,
    required this.text,
    this.color = Colors.white,
  });

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: TextStyle(
        fontSize: size,
        color: color,
        shadows: const [
          Shadow(
            // 외곽선 색상 및 오프셋 설정
            offset: Offset(-1.5, -1.5),
            color: Colors.black,
            blurRadius: 1,
          ),
          Shadow(
            offset: Offset(1.5, -1.5),
            color: Colors.black,
            blurRadius: 1,
          ),
          Shadow(
            offset: Offset(1.5, 1.5),
            color: Colors.black,
            blurRadius: 1,
          ),
          Shadow(
            offset: Offset(-1.5, 1.5),
            color: Colors.black,
            blurRadius: 1,
          ),
        ],
      ),
    );
  }
}
