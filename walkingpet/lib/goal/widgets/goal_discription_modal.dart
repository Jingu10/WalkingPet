import 'package:flutter/material.dart';
import 'package:nes_ui/nes_ui.dart';

class GoalDiscriptionModal extends StatelessWidget {
  const GoalDiscriptionModal({super.key});

  static Future<bool?> showDiscription({
    required BuildContext context,
    NesDialogFrame frame = const NesBasicDialogFrame(),
  }) {
    return NesDialog.show<bool?>(
      context: context,
      builder: (_) => const GoalDiscriptionModal(),
      frame: frame,
    );
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Center(
      child: Padding(
        padding:
            EdgeInsets.all(screenWidth * 0.02), // 여백을 추가하여 텍스트가 화면에 잘 맞도록 함
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              "일일 목표 및 보상",
              style: TextStyle(
                fontSize: screenWidth * 0.06,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(
              height: screenHeight * 0.02,
            ),
            Text(
              "• 출석보상: 경험치 아이템 1개\n"
              "• 3,000걸음: 경험치 아이템 2개\n"
              "• 5,000걸음: 경험치 아이템 2개\n"
              "• 7,000걸음: 일반 상자 1개\n"
              "• 10,000걸음: 고급 상자 1개\n",
              style: TextStyle(
                fontSize: screenWidth * 0.042,
              ),
              textAlign: TextAlign.left,
            ),
            SizedBox(
              height: screenHeight * 0.03,
            ),
            Text(
              "주간 목표 및 보상",
              style: TextStyle(
                fontSize: screenWidth * 0.06,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(
              height: screenHeight * 0.02,
            ),
            Text(
              "하루에 5,000걸음을 걸으면\n주간 목표가 1회 달성됩니다.\n",
              style: TextStyle(
                fontSize: screenWidth * 0.042,
              ),
              textAlign: TextAlign.center,
            ),
            Text(
              "• 1~3회: 고급 상자 1개\n"
              "• 4~6회: 고급 상자 3개\n"
              "• 7회: 고급 상자 5개\n",
              style: TextStyle(
                fontSize: screenWidth * 0.042,
              ),
              textAlign: TextAlign.left,
            ),
            SizedBox(
              height: screenHeight * 0.04,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                NesButton(
                  type: NesButtonType.primary,
                  child:
                      const Text("확인", style: TextStyle(color: Colors.white)),
                  onPressed: () {
                    Navigator.of(context).pop(true); // 종료
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
