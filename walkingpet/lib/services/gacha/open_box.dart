import 'package:http/http.dart' as http;
import 'dart:convert';
import '../Interceptor.dart'; // JSON 데이터를 다루고, 문자열 인코딩 및 디코딩을 위함

Future<Map<String, dynamic>> openLuxuryBox() async {
  final client = AuthInterceptor();
  final response = await client
      .get(Uri.parse('https://walkingpet.co.kr/gacha/result?box=luxury'));
  if (response.statusCode == 200) {
    // 응답 바이트를 UTF-8로 디코딩 (UTF8 디코딩 안하면 한글이 깨져서 온다)
    var data = utf8.decode(response.bodyBytes);
    // 디코딩된 문자열을 JSON으로 파싱
    var jsonData = jsonDecode(data);
    print(jsonData['data']);
    return jsonData['data'];
  } else if (response.statusCode == 403) {
    // 열 수 있는 상자가 없으면
    throw Exception("열 수 있는 상자가 없습니다.");
  } else {
    //상태 코드와 함께 에러 메세지 출력
    //상자 없어도 에러남
    print("에러남 : ${response.statusCode}");
    throw Error();
  }
}

//일반 박스
Future<Map<String, dynamic>> openNormalBox() async {
  final client = AuthInterceptor();
  final response = await client
      .get(Uri.parse('https://walkingpet.co.kr/gacha/result?box=normal'));
  if (response.statusCode == 200) {
    // 응답 바이트를 UTF-8로 디코딩 (UTF8 디코딩 안하면 한글이 깨져서 온다)
    var data = utf8.decode(response.bodyBytes);
    // 디코딩된 문자열을 JSON으로 파싱
    var jsonData = jsonDecode(data);
    print(jsonData['data']);
    return jsonData['data'];
  } else if (response.statusCode == 403) {
    // 열 수 있는 상자가 없으면
    throw Exception("열 수 있는 상자가 없습니다.");
  } else {
    //상태 코드와 함께 에러 메세지 출력
    //상자 없어도 에러남
    print("에러남 : ${response.statusCode}");
    throw Error();
  }
}
