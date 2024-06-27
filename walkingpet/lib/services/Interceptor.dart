import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:walkingpet/providers/step_counter.dart';

class AuthInterceptor extends http.BaseClient {
  final http.Client _inner = http.Client();
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  final StepCounter _stepCounter; // StepCounter 인스턴스 추가

  static final AuthInterceptor _instance = AuthInterceptor._internal();

  factory AuthInterceptor() {
    return _instance;
  }

  AuthInterceptor._internal() : _stepCounter = StepCounter();
  // 싱글톤 인스턴스

  @override
  Future<http.StreamedResponse> send(http.BaseRequest request) async {
    final accessToken = await _storage.read(key: 'ACCESS_TOKEN');
    final steps = _stepCounter.steps ?? 0; // StepCounter의 steps 값 사용
    final headers = request.headers;

    headers['Authorization'] = 'Bearer $accessToken';
    headers['step'] = '$steps';

    request.headers.addAll(headers);

    return _inner.send(request);
  }
}
