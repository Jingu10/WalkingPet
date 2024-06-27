import 'package:flutter/material.dart';

class MainFontStyle extends StatelessWidget {
  final double size;
  final String text;
  final Color color;
  final bool whiteOffset;

  const MainFontStyle({
    super.key,
    required this.size,
    required this.text,
    this.color = Colors.white,
    this.whiteOffset = false,
  });

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: TextStyle(
        fontSize: size,
        color: color,
        shadows: [
          Shadow(
            // 외곽선 색상 및 오프셋 설정
            offset: const Offset(-1.5, -1.5),
            color: whiteOffset ? Colors.white : Colors.black,
            blurRadius: 1,
          ),
          Shadow(
            offset: const Offset(1.5, -1.5),
            color: whiteOffset ? Colors.white : Colors.black,
            blurRadius: 1,
          ),
          Shadow(
            offset: const Offset(1.5, 1.5),
            color: whiteOffset ? Colors.white : Colors.black,
            blurRadius: 1,
          ),
          Shadow(
            offset: const Offset(-1.5, 1.5),
            color: whiteOffset ? Colors.white : Colors.black,
            blurRadius: 1,
          ),
        ],
      ),
    );
  }
}
