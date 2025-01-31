import 'package:flutter/material.dart';
import 'package:nes_ui/nes_ui.dart';

class StatResetModal extends StatelessWidget {
  const StatResetModal({super.key});

  static Future<bool?> showStatResetModal({
    required BuildContext context,
    NesDialogFrame frame = const NesBasicDialogFrame(),
  }) {
    return NesDialog.show<bool?>(
      context: context,
      builder: (_) => const StatResetModal(),
      frame: frame,
    );
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          const Text(
            "스탯을 초기화 하시겠습니까?",
          ),
          SizedBox(
            height: screenHeight * 0.02,
          ),
          const Text(
            "하루에 한 번만 초기화 가능합니다.",
          ),
          SizedBox(
            height: screenHeight * 0.04,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              NesButton(
                type: NesButtonType.error,
                child: const Text("취소", style: TextStyle(color: Colors.white)),
                onPressed: () {
                  Navigator.of(context).pop(false); // 취소
                },
              ),
              SizedBox(
                width: screenWidth * 0.03,
              ),
              NesButton(
                type: NesButtonType.warning,
                child: const Text("확인", style: TextStyle(color: Colors.white)),
                onPressed: () {
                  Navigator.of(context).pop(true); // 종료
                },
              ),
            ],
          ),
        ],
      ),
    );
  }
}
