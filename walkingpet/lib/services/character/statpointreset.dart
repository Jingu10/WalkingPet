import 'dart:convert';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr";
const String statreset = "character/stat/reset";

// 캐릭터 변경 모달
Future<Map<String, dynamic>> getStatReset() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$statreset');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    return jsonData;
  } else {
    throw Error();
  }
}
