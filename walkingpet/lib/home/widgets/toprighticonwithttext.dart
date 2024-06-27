import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:walkingpet/main.dart';

class TopRightIconWithText extends StatelessWidget {
  final String icon;
  final String text;

  const TopRightIconWithText({
    super.key,
    required this.icon,
    required this.text,
  });

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    double svgWidth = screenWidth * 0.17;
    double svgHeight = screenHeight * 0.05;

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        IconButton(
          onPressed: () {
            Navigator.pushNamed(context, '/$icon');
          },
          icon: Image.asset(
            'assets/icons/$icon.png',
            width: svgWidth,
            height: svgHeight,
          ),
          // icon: SvgPicture.asset(
          //   'assets/icons/$icon.svg',
          //   width: svgWidth,
          // ),
        ),
        Transform.translate(
          offset: Offset(0, screenHeight * -0.005),
          child: Text(
            text,
            style: TextStyle(
              fontSize: screenWidth * 0.05,
              fontWeight: FontWeight.bold,
              color: Colors.white,
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
          ),
        )
      ],
    );
  }
}
