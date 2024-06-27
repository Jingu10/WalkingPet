import 'dart:convert';
import '../Interceptor.dart';

Future<List<dynamic>> getGroupMember(groupId) async {
  final client = AuthInterceptor();
  final response = await client
      .get(Uri.parse('https://walkingpet.co.kr/team/members/$groupId'));
  if (response.statusCode == 200) {
    var data = utf8.decode(response.bodyBytes);
    var jsonData = jsonDecode(data);
    print("불러온 멤버 정보 : ${jsonData['data']}");
    return jsonData['data'];
  } else {
    print("멤버 정보 불러오는 도중 오류 발생 : ${response.statusCode}");
    throw Error();
  }
}
