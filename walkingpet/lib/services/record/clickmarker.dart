import 'dart:convert';
import '../Interceptor.dart';
import 'package:http/http.dart' as http;

const String baseUrl = "https://walkingpet.co.kr";
const String checkRecordUrl = "record/check-record";

// 기록 확인
Future<Map<String, dynamic>> getClickMarker(
    double latitude, double longitude, int recordId) async {
  final client = AuthInterceptor();
  final url = Uri.parse(
      '$baseUrl/$checkRecordUrl?latitude=$latitude&longitude=$longitude&recordId=$recordId');
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
      print("해당 기록을 찾을 수 없습니다.");
      break;
    case 403:
      print("기록에 접근 권한이 없습니다.");
      break;
    default:
      print("기록 확인 오류: ${response.statusCode}");
  }
  throw Exception("서버 응답 에러: ${response.statusCode}");
}
