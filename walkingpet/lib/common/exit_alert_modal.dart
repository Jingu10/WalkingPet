import 'package:flutter/material.dart';
import 'dart:io';
import 'package:nes_ui/nes_ui.dart';
import 'package:flutter/services.dart';

class ExitModal extends StatelessWidget {
  const ExitModal({super.key});

  static Future<bool?> showExitModal({
    required BuildContext context,
    NesDialogFrame frame = const NesBasicDialogFrame(),
    bool isLogout = false,
  }) {
    return NesDialog.show<bool?>(
      context: context,
      builder: (_) => const ExitModal(),
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
        children: [
          const Text(
            "종료하시겠습니까?",
          ),
          SizedBox(
            height: screenHeight * 0.04,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              NesButton(
                type: NesButtonType.primary,
                child: const Text("취소", style: TextStyle(color: Colors.white)),
                onPressed: () {
                  Navigator.of(context).pop(false); // 취소
                },
              ),
              SizedBox(
                width: screenWidth * 0.03,
              ),
              NesButton(
                type: NesButtonType.error,
                child: const Text("종료", style: TextStyle(color: Colors.white)),
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

Future<void> handleExit(BuildContext context) async {
  bool? result = await ExitModal.showExitModal(context: context);

  if (result == true) {
    // 종료를 눌렀을 때 앱을 종료
    if (Platform.isAndroid) {
      SystemNavigator.pop();
    } else if (Platform.isIOS) {
      exit(0);
    }
  }
}
