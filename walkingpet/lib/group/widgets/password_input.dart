// ignore_for_file: use_build_context_synchronously, avoid_print

import 'package:flutter/material.dart';
import 'package:nes_ui/nes_ui.dart';
import 'package:walkingpet/group/widgets/group_detail.dart';
import 'package:walkingpet/services/group/enter_group.dart';

Future<String?> passwordInputShow({
  required BuildContext context,
  NesDialogFrame frame = const NesBasicDialogFrame(),
  required int groupId,
}) {
  return NesDialog.show<String?>(
    context: context,
    builder: (_) => CustomNesInputDialog(
      inputLabel: "  확인  ",
      message: "비밀번호 입력 (최대 6자)",
      groupId: groupId,
    ),
    frame: frame,
  );
}

class CustomNesInputDialog extends StatefulWidget {
  final String inputLabel;
  final String message;
  final int groupId;

  const CustomNesInputDialog({
    super.key,
    required this.inputLabel,
    required this.message,
    required this.groupId,
  });

  static Future<String?> show({
    required BuildContext context,
    required String message,
    String inputLabel = 'Ok',
    required int groupId,
  }) {
    return showDialog<String?>(
      context: context,
      builder: (context) => CustomNesInputDialog(
        inputLabel: inputLabel,
        message: message,
        groupId: groupId,
      ),
    );
  }

  @override
  CustomNesInputDialogState createState() => CustomNesInputDialogState();
}

class CustomNesInputDialogState extends State<CustomNesInputDialog> {
  final TextEditingController _controller = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  String? _errorMessage;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  bool _validateInput(String value) {
    if (value.isEmpty) {
      setState(() {
        _errorMessage = '비밀번호를 입력해주세요.';
      });
      return false;
    }

    setState(() {
      _errorMessage = null;
    });
    return true;
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Form(
      key: _formKey,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(widget.message),
          SizedBox(height: screenHeight * 0.03),
          TextFormField(
            controller: _controller,
            textAlign: TextAlign.center,
            autofocus: true,
            maxLength: 6,
            decoration: const InputDecoration(
              counterText: "", // 카운터 텍스트를 숨김
            ),
            validator: (value) {
              if (value == null || !_validateInput(value)) {
                return ' '; // This space ' ' is used to activate the error message space.
              }
              return null;
            },
          ),
          if (_errorMessage != null)
            Text(
              _errorMessage!,
              style: TextStyle(color: Colors.red, fontSize: screenWidth * 0.03),
            ),
          SizedBox(height: screenHeight * 0.03),
          NesButton(
            type: NesButtonType.primary,
            child: Text(widget.inputLabel,
                style: const TextStyle(color: Colors.white)),
            onPressed: () async {
              if (_formKey.currentState!.validate()) {
                bool passwordMatch =
                    await enterGroup(widget.groupId, _controller.text);
                // 비밀번호가 일치하면
                if (passwordMatch) {
                  navigateToGroupDetail(context, widget.groupId);
                } else {
                  setState(() {
                    _errorMessage = "일치하지 않습니다.";
                  });
                }
              }
            },
          ),
        ],
      ),
    );
  }
}

void navigateToGroupDetail(BuildContext context, int groupId) {
  // 그룹 디테일 페이지 이동 함수화
  Navigator.push(
    context,
    MaterialPageRoute(
      builder: (context) => GroupDetail(groupId: groupId),
    ),
  );
}
