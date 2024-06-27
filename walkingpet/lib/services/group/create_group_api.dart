import 'dart:convert';
import '../Interceptor.dart';

Future<void> createGroupService(
    String teamName, String explanation, String password) async {
  final client = AuthInterceptor();
  try {
    final response =
        await client.post(Uri.parse('https://walkingpet.co.kr/team/create'),
            headers: {"Content-Type": "application/json"}, //요청 본문이 Json 형식임
            body: jsonEncode({
              "teamName": teamName,
              "explanation": explanation,
              "password": password,
            }));
    if (response.statusCode == 200) {
      print('그룹 생성 성공 : $teamName 그룹');
    } else {
      print('그룹 생성 실패: $teamName 그룹');
    }
  } catch (e) {
    print("그룹 생성 요청 중 오류 발생 : $e");
  }
}
