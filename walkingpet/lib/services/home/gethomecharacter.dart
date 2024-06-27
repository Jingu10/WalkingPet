import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

Future<Map<String, dynamic>> getHomeCharacter() async {
  const FlutterSecureStorage storage = FlutterSecureStorage();
  String userId = await storage.read(key: 'USER_ID') ?? "0";
  int userIdInt = int.parse(userId);

  final url =
      Uri.parse('https://walkingpet.co.kr/auth/info?userId=$userIdInt ');

  final response = await http.get(
    url,
    headers: {'Content-Type': 'application/json'},
  );

  try {
    if (response.statusCode == 200) {
      var data = utf8.decode(response.bodyBytes);
      return jsonDecode(data);
    } else {
      throw Exception("API 요청 실패");
    }
  } catch (e) {
    print("API 요청 중 에러 발생: $e");
    throw Exception("API 요청 실패");
  }
}
