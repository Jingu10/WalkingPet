import 'dart:convert';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr";
const String groupUrl = "ranking";

// 가입한 그룹 수 조회 (count)
Future<Map<String, dynamic>> getMyGroupCount() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$groupUrl/group/count');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("그룹랭킹 가입한 그룹 수 오류: ${response.statusCode}");
    throw Error();
  }
}

// 가입한 그룹 랭킹 조회
Future<Map<String, dynamic>> getMyGroup() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$groupUrl/myGroup');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("그룹랭킹 가입한 그룹들 순위 조회 오류: ${response.statusCode}");
    throw Error();
  }
}

// 모든 그룹 랭킹 조회
Future<Map<String, dynamic>> getGroup() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$groupUrl/group');
  final response = await client.get(url);

  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    // print(jsonData);
    return jsonData;
  } else {
    print("모든 그룹 랭킹 조회 오류: ${response.statusCode}");
    throw Error();
  }
}
