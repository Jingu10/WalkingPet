import 'package:http/http.dart' as http;
import 'dart:convert';
import '../Interceptor.dart'; // JSON 데이터를 다루고, 문자열 인코딩 및 디코딩을 위함

Future<List<Map<String, dynamic>>> getMyGroup() async {
  final client = AuthInterceptor();
  final response =
      await client.get(Uri.parse('https://walkingpet.co.kr/team/belong'));
  if (response.statusCode == 200) {
    //  응답 바이트를 UTF-8로 디코딩 (디코팅 안하면 한글이 깨져서 온다)
    var data = utf8.decode(response.bodyBytes);
    // 디코딩된 문자열을  Json으로 파싱
    var jsonData = jsonDecode(data);
    print('내가 가입한 그룹: ${jsonData['data']}');
    return (jsonData['data'] as List).map((item) {
      return Map<String, dynamic>.from(item);
    }).toList();
  } else {
    //상태 코드와 함께 에러 메세지 출력력
    print("내가 가입한 그룹 데이터 받아오기 오류 : ${response.statusCode}");
    throw Error();
  }
}

//모든 그룹 정보 가져오기
Future<List<Map<String, dynamic>>> getAllGroup() async {
  final client = AuthInterceptor();
  final response =
      await client.get(Uri.parse('https://walkingpet.co.kr/team/all'));
  if (response.statusCode == 200) {
    //  응답 바이트를 UTF-8로 디코딩 (디코팅 안하면 한글이 깨져서 온다)
    var data = utf8.decode(response.bodyBytes);
    // 디코딩된 문자열을  Json으로 파싱
    var jsonData = jsonDecode(data);
    print('전체 그룹: ${jsonData['data']}');
    return (jsonData['data'] as List).map((item) {
      return Map<String, dynamic>.from(item);
    }).toList();
  } else {
    //상태 코드와 함께 에러 메세지 출력력
    print("내가 가입한 그룹 데이터 받아오기 오류 : ${response.statusCode}");
    throw Error();
  }
}
