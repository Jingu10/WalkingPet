import 'dart:convert';
import '../Interceptor.dart';

Future<void> joinGroup(int teamId) async {
  final client = AuthInterceptor();
  try {
    final response =
        await client.post(Uri.parse('https://walkingpet.co.kr/team/join'),
            headers: {"Content-Type": "application/json"},
            body: jsonEncode({
              "teamId": teamId,
            }));
    if (response.statusCode == 200) {
      print("그룹 가입 성공");
    } else {
      print("그룹 가입 실패");
    }
  } catch (e) {
    print("그룹 가입 요청 도중 유류 발생 : $e");
  }
}
