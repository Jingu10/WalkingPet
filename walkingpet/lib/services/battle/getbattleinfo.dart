import 'dart:convert';
import '../Interceptor.dart';

// Future<Void>는 반환 값이 없고, 완료까지 일정 시간이 소요될 수 있는 비동기 작업을 수행
Future<Map<String, dynamic>> getBattleInfo() async {
  final client = AuthInterceptor();
  final response =
      await client.get(Uri.parse('https://walkingpet.co.kr/battle/start'));

  if (response.statusCode == 200) {
    // 응답 바이트를 UTF-8로 디코딩 (UTF8 디코딩 안하면 한글이 깨져서 온다)
    var data = utf8.decode(response.bodyBytes);
    // 디코딩된 문자열을 JSON으로 파싱
    var jsonData = jsonDecode(data);
    return jsonData;
  } else {
    throw Exception('Failed to load data');
  }
}
