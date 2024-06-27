// 일일 목표 달성시 서버에 보상 요청하는 코드
import '../Interceptor.dart';
import 'dart:convert';

Future<Map<String, dynamic>> sendRewardRequest(int goalSteps) async {
  final client = AuthInterceptor();
  var response = await client.get(
      Uri.parse('https://walkingpet.co.kr/goal/reward?goalStep=$goalSteps'));
  if (response.statusCode == 200) {
    // print(response.body);
    // 응답 데이터를 JSON으로 파싱
    var responseData = jsonDecode(response.body);
    print('보상 아이템: ${responseData['data']['goalReward']}');
    var reward = responseData['data']['goalReward'];
    return reward;
  } else {
    print("보상 받아오기 에러 : ${response.statusCode}");
    throw Error();
  }
}
