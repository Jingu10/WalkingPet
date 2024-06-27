import 'dart:convert';

import 'package:walkingpet/services/Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr";
const String expiteminfoUrl = "character/exp";
const String expitemUrl = "character/item/use/expitem";

// 캐릭터별 경험치 & 경험치 아이템 정보 조회
Future<Map<String, dynamic>> getExpitemInfo() async {
  final client = AuthInterceptor();
  // final url = Uri.parse(baseUrl);
  final url = Uri.parse('$baseUrl/$expiteminfoUrl');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("캐릭터 정보 받아오기 오류 : ${response.statusCode}");
    throw Error();
  }
}

// 캐릭터별 경험치 아이템 사용
Future<Map<String, dynamic>> getExpitem(int quantity) async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$expitemUrl?quantity=$quantity');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("캐릭터별 경험치 아이템 사용 오류 : ${response.statusCode}");
    throw Error();
  }
}
