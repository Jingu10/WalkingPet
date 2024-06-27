import 'package:http/http.dart' as http;
import 'dart:convert'; // JSON 데이터를 다루고, 문자열 인코딩 및 디코딩을 위함

// Future<Void>는 반환 값이 없고, 완료까지 일정 시간이 소요될 수 있는 비동기 작업을 수행
Future<Map<String, dynamic>> fetchData() async {
  /*
  Uri.parse는 문자열 URL을 Uri 객체로 변환합니다. 
  await 키워드는 비동기 요청의 완료를 기다리며, 그 동안 다른 코드의 실행을 중지하지 않습니다.
  */

  final response =
      await http.get(Uri.parse('https://walkingpet.co.kr/character/test'));

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
