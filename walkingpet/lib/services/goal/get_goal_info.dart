import 'dart:convert';
import '../Interceptor.dart';

Future<Map<String, dynamic>> getGoalInfo() async {
  final client = AuthInterceptor();
  final response =
      await client.get(Uri.parse('https://walkingpet.co.kr/goal/info'));
  if (response.statusCode == 200) {
    //문자열 데이터 (response)를 JSON으로 파싱
    var jsonResponse = jsonDecode(response.body);
    print(jsonResponse['data']);
    return jsonResponse['data'];
  } else {
    print("목표 받아오기 에러남 : ${response.statusCode}");
    throw Error();
  }
}
