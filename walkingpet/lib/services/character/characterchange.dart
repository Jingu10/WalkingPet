import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:walkingpet/providers/character_info.dart';
import '../Interceptor.dart';

const String baseUrl = "https://walkingpet.co.kr";
const String characterInfo = "character/list";
const String characterChange = "character/change";

// 캐릭터 변경 모달

// 1. 캐릭터 정보 받아오기 (get)
Future<Map<String, dynamic>> getCharacterChange() async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$characterInfo');
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

// 2. 유저의 현재 장착 캐릭터 변경하기 (post)
Future<void> postCharacterChange(
    BuildContext context, selectCharacterId) async {
  final client = AuthInterceptor();
  final url = Uri.parse('$baseUrl/$characterChange');

  try {
    final response = await client.post(
      url,
      headers: {"Content-Type": "application/json"}, // 콘텐트 타입 헤더 추가
      body: jsonEncode({"selectCharacterId": selectCharacterId}),
    );
    print(selectCharacterId);
    // print('Response Body: ${response.body}');

    if (response.statusCode == 200) {
      var data = utf8.decode(response.bodyBytes);
      var jsonData = jsonDecode(data);
      final characterProvider =
          Provider.of<CharacterProvider>(context, listen: false);
      characterProvider.characterId = jsonData['data']['changeCharacterId'];

      print('캐릭터 변경 성공: ${response.body}');
    } else {
      print('캐릭터 변경 실패: ${response.statusCode}');
    }
  } catch (e) {
    print('캐릭터 변경 요청 중 오류 발생: $e');

    // 클라이언트 사용 후 자원 해제
  } finally {
    client.close();
  }
}
