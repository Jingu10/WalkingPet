import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';

class GachaBox extends StatelessWidget {
  final String boxName;
  final String boxImage; //박스 이미지 경로
  // final int totalBoxQuantity; //내가 가진 박스
  final String buttonImage; //버튼 이미지 경로
  final String inactivedButtonImage; //비활성화된 버튼 이미지 경로
  final int remainCount; // 남은 상자 개수
  final VoidCallback onTap;

  const GachaBox({
    super.key,
    required this.boxName,
    required this.boxImage,
    // required this.totalBoxQuantity,
    required this.buttonImage,
    required this.inactivedButtonImage,
    required this.remainCount,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    return Column(
      children: [
        Text(boxName, style: TextStyle(fontSize: screenHeight * 0.03)),
        Text('x $remainCount',
            style: TextStyle(fontSize: screenHeight * 0.04, color: Colors.red)),
        Stack(
          alignment: Alignment.center,
          children: [
            Image.asset(
              boxImage,
              width: screenWidth * 0.3,
            ),
          ],
        ),
        GestureDetector(
          onTap: remainCount > 0
              ? onTap
              : null, //onTap 속성에 null 을 할당하면 터치 이벤트를 받지 않음
          child: SvgPicture.asset(
            remainCount > 0 ? buttonImage : inactivedButtonImage,
            height: screenHeight * 0.06,
          ),
        ),
      ],
    );
  }
}
