import 'dart:convert';
import '../Interceptor.dart';

Future<bool> enterGroup(int teamId, String password) async {
  final client = AuthInterceptor();
  try {
    final response =
        await client.post(Uri.parse('https://walkingpet.co.kr/team/enter'),
            headers: {"Content-Type": "application/json"}, //요청 본문이 Json 형식임
            body: jsonEncode({
              "teamId": teamId,
              "password": password,
            }));
    if (response.statusCode == 200) {
      print('그룹 입장 성공');
      return true;
    } else {
      print('그룹 입장 실패');
      return false;
    }
  } catch (e) {
    print("그룹 입장 요청 중 오류 발생 : $e");
    return false;
  }
}
