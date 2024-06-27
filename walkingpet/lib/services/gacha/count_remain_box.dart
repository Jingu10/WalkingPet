import 'package:http/http.dart' as http;
import 'dart:convert';
import '../Interceptor.dart';

Future<Map<String, dynamic>> countRemainBox() async {
  final client = AuthInterceptor();
  final response =
      await client.get(Uri.parse('https://walkingpet.co.kr/gacha/count'));
  if (response.statusCode == 200) {
    print(response.body);
    //문자열을 Json으로 파싱
    var jsonData = jsonDecode(response.body);
    print(jsonData["data"]);

    return jsonData["data"];
  } else {
    print("박스 수 정보 받아오기 에러 : ${response.statusCode}");
    throw Error();
  }
}
