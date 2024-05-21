import 'dart:convert';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr/character";

// 캐릭터 정보 조회
Future<Map<String, dynamic>> getCharacterInfo() async {
  final client = AuthInterceptor();
  final url = Uri.parse(baseUrl);
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    return jsonData;
  } else {
    throw Error();
  }
}
