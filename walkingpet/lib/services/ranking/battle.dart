import 'dart:convert';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr";
const String battleUrl = "ranking/battle";

// 랭킹 Top10 조회
Future<Map<String, dynamic>> getBattleTop10() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$battleUrl/top10');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("배틀랭킹 Top10 오류: ${response.statusCode}");
    throw Error();
  }
}

// 랭킹 본인 순위 조회
Future<Map<String, dynamic>> getBattleMyRank() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$battleUrl/myrank');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("배틀랭킹 유저순위(my rank) 오류: ${response.statusCode}");
    throw Error();
  }
}

// 랭킹 1~3위 조회
Future<Map<String, dynamic>> getBattleTop3() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$battleUrl/top3');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("배틀랭킹 Top3 오류: ${response.statusCode}");
    throw Error();
  }
}
