import 'dart:convert'; // JSON 데이터를 다루고, 문자열 인코딩 및 디코딩을 위함
import '../Interceptor.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

// Future<Void>는 반환 값이 없고, 완료까지 일정 시간이 소요될 수 있는 비동기 작업을 수행
Future<int> checkStep() async {
  /*
  Uri.parse는 문자열 URL을 Uri 객체로 변환합니다. 
  await 키워드는 비동기 요청의 완료를 기다리며, 그 동안 다른 코드의 실행을 중지하지 않습니다.
  */
  final client = AuthInterceptor();
  const FlutterSecureStorage storage = FlutterSecureStorage();
  String userId = await storage.read(key: 'USER_ID') ?? "0";
  int userIdInt = int.parse(userId);

  final response = await client.get(
      Uri.parse('https://walkingpet.co.kr/auth/checkstep?userId=$userIdInt'));

  if (response.statusCode == 200) {
    // 응답 바이트를 UTF-8로 디코딩 (UTF8 디코딩 안하면 한글이 깨져서 온다)
    var data = utf8.decode(response.bodyBytes);
    // 디코딩된 문자열을 JSON으로 파싱
    var jsonData = jsonDecode(data);
    return jsonData['data']['step'];
  } else {
    return -1;
  }
}
