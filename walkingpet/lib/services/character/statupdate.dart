import 'dart:convert';
import '../Interceptor.dart';
import 'package:http/http.dart' as http;

const String baseUrl = "https://walkingpet.co.kr";
const String statUrl = "character/stat";

// 캐릭터 스탯 업데이트
Future<Map<String, dynamic>> updateStat(
    int userCharacterId, String value) async {
  final client = AuthInterceptor();
  final url = Uri.parse(
      '$baseUrl/$statUrl?userCharacterId=$userCharacterId&value=$value');
  final response = await client.get(url);

  try {
    if (response.statusCode == 200) {
      var data = utf8.decode(response.bodyBytes);
      return jsonDecode(data);
    } else {
      return _handleError(response);
    }
  } catch (e) {
    print("API 요청 중 에러 발생: $e");
    throw Exception("API 요청 실패");
  }
}

Map<String, dynamic> _handleError(http.Response response) {
  switch (response.statusCode) {
    case 404:
      print("유저의 해당 캐릭터를 찾을 수 없습니다.");
      break;
    case 403:
      print("유저의 해당 캐릭터 스탯 포인트가 부족합니다.");
      break;
    default:
      print("캐릭터 능력치 업데이트 받아오기 오류 : ${response.statusCode}");
  }
  throw Exception("서버 응답 에러: ${response.statusCode}");
}
