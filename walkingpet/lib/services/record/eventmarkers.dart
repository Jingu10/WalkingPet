import 'dart:convert';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr/record/event/all";

// 이벤트 마커 정보 불러오기
Future<Map<String, dynamic>> getEventMarkers() async {
  final client = AuthInterceptor();
  final url = Uri.parse(baseUrl);
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("이벤트 마커 정보 불러오기 오류 : ${response.statusCode}");
    throw Error();
  }
}
