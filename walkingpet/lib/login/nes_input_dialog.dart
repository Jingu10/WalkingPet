// ignore_for_file: use_build_context_synchronously, avoid_print

import 'package:flutter/material.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:nes_ui/nes_ui.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class CustomNesInputDialog extends StatefulWidget {
  final String inputLabel;
  final String message;

  const CustomNesInputDialog({
    super.key,
    required this.inputLabel,
    required this.message,
  });

  static Future<String?> show({
    required BuildContext context,
    required String message,
    String inputLabel = 'Ok',
  }) {
    return showDialog<String?>(
      context: context,
      builder: (context) => CustomNesInputDialog(
        inputLabel: inputLabel,
        message: message,
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

  void setErrorMessage() {
    setState(() {
      _errorMessage = '중복된 닉네임 입니다.';
    });
  }

  bool _validateInput(String value) {
    if (value.isEmpty) {
      setState(() {
        _errorMessage = '닉네임을 입력해주세요.';
      });
      return false;
    }
    RegExp regex = RegExp(r'^[가-힣A-Za-z0-9]{2,6}$');
    if (!regex.hasMatch(value)) {
      setState(() {
        _errorMessage = '한글, 영어, 숫자만 가능합니다.';
      });
      return false;
    }
    setState(() {
      _errorMessage = null;
    });
    return true;
  }

  // 신규 유저 회원가입
  Future<void> _signUp(BuildContext context, String nickname) async {
    final User user = await UserApi.instance.me();
    const baseUrl = 'https://walkingpet.co.kr';
    const endpoint = '/auth/social-login';

    if (await _checkNickname(nickname)) {
      try {
        final url = Uri.parse('$baseUrl$endpoint');
        final body = {
          'socialEmail': '${user.kakaoAccount?.email}',
          'nickname': nickname
        };
        final response = await http.post(
          url,
          headers: {'Content-Type': 'application/json'},
          body: json.encode(body),
        );

        if (response.statusCode == 200) {
          await _saveTokens(response.body);
          Navigator.pushReplacementNamed(context, '/home');
        } else {
          print("서버로부터 응답이 없습니다");
        }
      } catch (error) {
        print("네트워크 문제: $error");
      }
    } else {
      setErrorMessage();
    }
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
            if (_errorMessage == "중복된 닉네임 입니다.")
              SizedBox(
                height: screenHeight * 0.03,
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
            onPressed: () {
              if (_formKey.currentState!.validate()) {
                _signUp(context, _controller.text);
                //Navigator.of(context).pop(_controller.text);
              }
            },
          ),
        ],
      ),
    );
  }
}

//백으로부터 받은 토큰을 FlutterSecureStorage에 저장
Future<void> _saveTokens(String responseBody) async {
  final jsonResponse = json.decode(responseBody);
  final accessToken = jsonResponse['data']['accessToken'];
  final refreshToken = jsonResponse['data']['refreshToken'];

  const storage = FlutterSecureStorage();
  await storage.write(key: 'ACCESS_TOKEN', value: accessToken);
  await storage.write(key: 'REFRESH_TOKEN', value: refreshToken);
}

// 닉네임 중복 체크 (중복일 경우 true 반환)
Future<bool> _checkNickname(String nickname) async {
  const baseUrl = 'https://walkingpet.co.kr';
  const endpoint = '/user/nicknameCheck';

  try {
    final url = Uri.parse('$baseUrl$endpoint?nickname=$nickname');
    final response = await http.get(url);

    if (response.statusCode == 200) {
      final jsonResponse = json.decode(response.body);
      final data = jsonResponse['data'];

      if (data) {
        // data가 true인 경우는 중복된 닉네임이라는 뜻
        return false;
      } else {
        return true;
      }
    } else {
      print('서버로부터 오류 응답을 받았습니다. 상태 코드: ${response.statusCode}');
      return false;
    }
  } catch (error) {
    print("네트워크 문제: $error");
    return false;
  }
}
