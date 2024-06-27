// ignore_for_file: use_build_context_synchronously, avoid_print

import 'package:flutter/material.dart';
import 'package:walkingpet/common/exit_alert_modal.dart';
import 'package:walkingpet/login/nes_input_dialog.dart';
import 'package:nes_ui/nes_ui.dart';
import 'package:flutter/services.dart';
import 'dart:convert';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:walkingpet/providers/step_counter.dart';

class Login extends StatefulWidget {
  const Login({super.key});

  @override
  State<Login> createState() => _LoginState();
}

class _LoginState extends State<Login> {
  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return PopScope(
      canPop: false,
      onPopInvoked: (bool dippop) {
        handleExit(context);
      },
      child: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage("assets/backgrounds/day.png"), // 배경 이미지 파일 경로 지정
            fit: BoxFit.cover, // 배경 이미지가 전체 화면을 채우도록 설정
          ),
        ),
        child: Scaffold(
          backgroundColor: Colors.transparent, // Scaffold 배경을 투명하게 설정
          body: Center(
            child: Column(
              children: [
                SizedBox(
                  height: screenHeight * 0.14,
                ),
                Text(
                  "Walking Pet",
                  style: TextStyle(
                    fontSize: screenWidth * 0.13,
                    color: const Color.fromRGBO(141, 198, 63, 1),
                    shadows: const [
                      Shadow(
                        offset: Offset(-1.5, -1.5),
                        color: Color.fromRGBO(54, 91, 18, 1),
                        blurRadius: 1,
                      ),
                      Shadow(
                        offset: Offset(1.5, -1.5),
                        color: Color.fromRGBO(54, 91, 18, 1),
                        blurRadius: 1,
                      ),
                      Shadow(
                        offset: Offset(1.5, 1.5),
                        color: Color.fromRGBO(54, 91, 18, 1),
                        blurRadius: 1,
                      ),
                      Shadow(
                        offset: Offset(-1.5, 1.5),
                        color: Color.fromRGBO(54, 91, 18, 1),
                        blurRadius: 1,
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  height: screenHeight * 0.26,
                ),
                Image.asset('assets/animals/cow/cow_walk.gif'),
                SizedBox(
                  height: screenHeight * 0.06,
                ),
                InkWell(
                  onTap: () => _handleKakaoLogin(context),
                  child:
                      Image.asset('assets/icons/kakao_login_medium_wide.png'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

//백으로부터 받은 토큰을 FlutterSecureStorage에 저장
Future<void> _saveTokens(String responseBody) async {
  final jsonResponse = json.decode(responseBody);
  final accessToken = jsonResponse['data']['accessToken'];
  final refreshToken = jsonResponse['data']['refreshToken'];
  final userId = jsonResponse['data']['userId'].toString();

  const storage = FlutterSecureStorage();
  await storage.write(key: 'ACCESS_TOKEN', value: accessToken);
  await storage.write(key: 'REFRESH_TOKEN', value: refreshToken);
  await storage.write(key: 'USER_ID', value: userId);
}

Future<String?> show({
  required BuildContext context,
  NesDialogFrame frame = const NesBasicDialogFrame(),
}) {
  return NesDialog.show<String?>(
    context: context,
    builder: (_) => const CustomNesInputDialog(
      inputLabel: "  확인  ",
      message: "닉네임을 정해주세요 (2~6자)",
    ),
    frame: frame,
  );
}

//  카카오 로그인 시작
Future<void> _handleKakaoLogin(BuildContext context) async {
  if (await isKakaoTalkInstalled()) {
    try {
      await UserApi.instance.loginWithKakaoTalk();
      _checkIfUserIsRegistered(context);
    } catch (error) {
      if (error is PlatformException && error.code == 'CANCELED') {
        return;
      }

      // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
      try {
        await UserApi.instance.loginWithKakaoAccount();
        _checkIfUserIsRegistered(context);
      } catch (error) {
        print(error);
      }
    }
  } else {
    try {
      await UserApi.instance.loginWithKakaoAccount();
      _checkIfUserIsRegistered(context);
    } catch (error) {
      print(error);
    }
  }
}

/*
    이미 가입한 유저인지 확인하는 메소드
    가입한 유저라면 바로 로그인 코드로 이동
    새로운 유저라면 회원가입 코드로 이동
   */
Future<void> _checkIfUserIsRegistered(BuildContext context) async {
  final User user = await UserApi.instance.me();
  final email = user.kakaoAccount?.email;
  const baseUrl = 'https://walkingpet.co.kr';
  const endpoint = '/user/emailCheck';

  try {
    final url = Uri.parse('$baseUrl$endpoint?userEmail=$email');
    final response = await http.get(url);

    if (response.statusCode == 200) {
      final jsonResponse = json.decode(response.body);
      final data = jsonResponse['data'];

      if (data != null && data is bool) {
        if (data) {
          _login(context);
        } else {
          // show 메서드는 닉네임을 정하는 모달을 보여줌 (= 회원가입)
          // 회원가입을 하기 전 걸음 수를 초기화 한다
          await show(context: context);
        }
      } else {}
    } else {
      print('서버로부터 오류 응답을 받았습니다. 상태 코드: ${response.statusCode}');
    }
  } catch (error) {
    print("네트워크 문제: $error");
  }
}

// 기존 회원 로그인 (토큰이 존재하지 않을 때 실행)
Future<void> _login(BuildContext context) async {
  final User user = await UserApi.instance.me();
  const baseUrl = 'https://walkingpet.co.kr';
  const endpoint = '/auth/social-login';

  try {
    final url = Uri.parse('$baseUrl$endpoint');
    final body = {'socialEmail': '${user.kakaoAccount?.email}', 'nickname': ""};
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
}
