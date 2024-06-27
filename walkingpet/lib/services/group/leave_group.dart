import 'dart:convert';
import 'package:flutter/material.dart';

import '../Interceptor.dart';

Future<void> leaveGroup(int teamId) async {
  final client = AuthInterceptor();
  try {
    final response =
        await client.post(Uri.parse('https://walkingpet.co.kr/team/exit'),
            headers: {"Content-Type": "application/json"}, // 요청 본문이 json 형태임
            body: jsonEncode({"teamId": teamId}));
    if (response.statusCode == 200) {
      print("그룹 탈퇴 성공");
    } else {
      (print("그룹 탈퇴 실패"));
    }
  } catch (e) {
    print("그룹 탈퇴 요청 중 오류 발생 : $e");
  }
}

