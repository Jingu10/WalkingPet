import 'dart:convert';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr/record/normal";

// 유저가 작성한 마커 정보 불러오기
Future<Map<String, dynamic>> getUserMarkers() async {
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
