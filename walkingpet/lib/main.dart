import 'dart:async';

import 'package:flutter/material.dart';
import 'package:nes_ui/nes_ui.dart';
import 'package:walkingpet/battle/battleready.dart';
import 'package:walkingpet/character/characterinfo.dart';
import 'package:walkingpet/providers/character_info.dart';
import 'package:walkingpet/providers/step_counter.dart';
import 'package:walkingpet/gacha/gacha.dart';
import 'package:walkingpet/goal/goal.dart';
import 'package:walkingpet/group/group.dart';
import 'package:walkingpet/home/home.dart';
import 'package:walkingpet/login/login.dart';
import 'package:walkingpet/ranking/ranking.dart';
import 'package:flutter/services.dart'; // SystemChrome을 사용하기 위해 필요
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';
import 'package:walkingpet/providers/gachabox_count_provider.dart';
import 'package:kakao_flutter_sdk_common/kakao_flutter_sdk_common.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import 'package:walkingpet/record/record.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart'; // env 관련 코드

import 'package:firebase_core/firebase_core.dart';
import 'package:walkingpet/services/audio/audio_manager.dart';
import 'package:walkingpet/services/audio/music_navigator_observer.dart';
import 'package:walkingpet/services/fcm/fcm.dart';
import 'firebase_options.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:geolocator/geolocator.dart';

Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  // 백그라운드에서 FCM을 수신했을 때의 동작 (아마 사용 안할듯)
  //print('A background message just showed up :  ${message.messageId}');
  //print("백그라운드 FCM 수신 완료!");
}

Future<void> setFCM3() async {
  String token = await FirebaseMessaging.instance.getToken() ?? '';
  debugPrint("fcmToken : $token");
  await sendTokenToServer(token);

  // 해당 토큰을 서버에 저장하는 api를 만들어서 요청 보내자
  //print("fcmToken : $token");
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await _requestPermissions();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);
  await dotenv.load(fileName: ".env"); // .env 추가
  _scheduleDailyTask();

  //_initSSE();

  /* 상단바, 하단바 모두 표시 & 상단바 투명하게 */
  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent, // 상단바 배경을 투명하게
    statusBarIconBrightness: Brightness.dark, // 상단바 아이콘을 어둡게 (라이트 모드)
  ));

  /* 세로 방향 고정 */
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp, // 세로 방향으로 위를 가리킴
    DeviceOrientation.portraitDown // 세로 방향으로 아래를 가리킴
  ]);

  KakaoSdk.init(
    nativeAppKey: dotenv.env['NATIVE_APP_KEY'],
    javaScriptAppKey: dotenv.env['JS_APP_KEY'],
  );

  // REFRESH_TOKEN 확인
  const storage = FlutterSecureStorage();
  String? refreshToken = await storage.read(key: 'REFRESH_TOKEN');

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (context) => BoxCounterProvider()),
        ChangeNotifierProvider<StepCounter>(create: (context) => StepCounter()),
        ChangeNotifierProvider<CharacterProvider>(
          create: (context) => CharacterProvider(),
        ),
      ],
      child: MyApp(
        startRoute: refreshToken != null ? '/home' : '/login',
      ),
    ),
  );
}

class MyApp extends StatefulWidget {
  final String startRoute;

  const MyApp({super.key, required this.startRoute});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused) {
      AudioManager().stop();
    } else if (state == AppLifecycleState.resumed) {
      // 현재 라우트에 따라 음악 재생
      handleRouteChange(ModalRoute.of(context)?.settings.name);
    }
    super.didChangeAppLifecycleState(state);
  }

  @override
  Widget build(BuildContext context) {
    // 1. 기본 테마 가져오기
    final ThemeData baseTheme = flutterNesTheme();

    // 2. TextTheme 생성하고, 모든 텍스트 스타일에 DungGeunMo 글꼴 적용!
    final TextTheme textTheme = baseTheme.textTheme.copyWith(
      titleLarge:
          baseTheme.textTheme.titleLarge?.copyWith(fontFamily: 'DungGeunMo'),
      titleMedium:
          baseTheme.textTheme.titleMedium?.copyWith(fontFamily: 'DungGeunMo'),
      titleSmall:
          baseTheme.textTheme.titleSmall?.copyWith(fontFamily: 'DungGeunMo'),
      headlineLarge:
          baseTheme.textTheme.headlineLarge?.copyWith(fontFamily: 'DungGeunMo'),
      headlineMedium: baseTheme.textTheme.headlineMedium
          ?.copyWith(fontFamily: 'DungGeunMo'),
      headlineSmall:
          baseTheme.textTheme.headlineSmall?.copyWith(fontFamily: 'DungGeunMo'),
      bodyLarge:
          baseTheme.textTheme.bodyLarge?.copyWith(fontFamily: 'DungGeunMo'),
      bodyMedium:
          baseTheme.textTheme.bodyMedium?.copyWith(fontFamily: 'DungGeunMo'),
      bodySmall:
          baseTheme.textTheme.bodySmall?.copyWith(fontFamily: 'DungGeunMo'),
      labelLarge:
          baseTheme.textTheme.labelLarge?.copyWith(fontFamily: 'DungGeunMo'),
      labelMedium:
          baseTheme.textTheme.labelMedium?.copyWith(fontFamily: 'DungGeunMo'),
      labelSmall:
          baseTheme.textTheme.labelSmall?.copyWith(fontFamily: 'DungGeunMo'),
      displayLarge:
          baseTheme.textTheme.displayLarge?.copyWith(fontFamily: 'DungGeunMo'),
    );

    // 3. 수정된 TextTheme + 기존 테마 (결합) => 새 테마 생성
    final ThemeData newTheme = baseTheme.copyWith(
      textTheme: textTheme,
    );

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Waking Pet',
      theme: newTheme,
      initialRoute: widget.startRoute,
      navigatorObservers: [MusicNavigatorObserver()],
      routes: {
        '/login': (context) => const Login(),
        '/home': (context) => const Home(),
        '/goal': (context) => const Goal(),
        '/ranking': (context) => const Ranking(),
        '/characterinfo': (context) => const CharacterInfo(),
        '/gacha': (context) => const Gacha(),
        '/battleready': (context) => const BattleReady(),
        '/group': (context) => const Group(),
        '/record': (context) => const Record(),
      },
    );
  }
}

// 권한 요청 메소드 정의
Future<void> _requestPermissions() async {
  // ACTIVITY_RECOGNITION 권한 요청
  var status = await Permission.activityRecognition.request();
  var notificationStatus = await Permission.notification.request();

  // 위치 서비스가 활성화되어 있는지 확인
  bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
  if (!serviceEnabled) {
    // 위치 서비스가 비활성화되어 있으면, 사용자에게 위치 서비스를 활성화하도록 요청
    await Geolocator.openLocationSettings();
    // 위치 권한 상태 확인
    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        // 권한이 거부된 경우, 오류 반환
        SystemNavigator.pop();
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // 권한이 영구적으로 거부된 경우, 오류 반환
      SystemNavigator.pop();
    }
  }

  // 위치 권한 상태 확인
  LocationPermission permission = await Geolocator.checkPermission();
  if (permission == LocationPermission.denied) {
    permission = await Geolocator.requestPermission();
    if (permission == LocationPermission.denied) {
      // 권한이 거부된 경우, 오류 반환
      SystemNavigator.pop();
    }
  }

  if (permission == LocationPermission.deniedForever) {
    // 권한이 영구적으로 거부된 경우, 오류 반환
    SystemNavigator.pop();
  }

  if (status.isGranted && notificationStatus.isGranted) {
    return;
  } else {
    // 사용자가 권한을 거부한 경우 처리할 로직 추가 가능
    SystemNavigator.pop();
  }
}

// 11시 59분부터 12시 까지 1초마다 StepCounter().resetStep() 실행
void _scheduleDailyTask() async {
  DateTime now = DateTime.now();
  DateTime firstRun = DateTime(now.year, now.month, now.day, 23, 59, 00);

  // 현재 시간이 23:59:00를 넘었으면 즉시 타이머 실행
  if (now.isAfter(firstRun)) {
    firstRun = now; // 타이머를 즉시 실행하기 위해 firstRun을 현재 시간으로 설정
  } else {
    // 현재 시간이 23:59:00를 넘지 않았다면, 오늘 23:59:00로 설정
    firstRun = DateTime(now.year, now.month, now.day, 23, 59, 00);
  }

  Duration initialDelay = firstRun.difference(now);
  Timer(initialDelay, () {
    // 23:59:00부터 자정까지 1초마다 resetStep 실행
    DateTime endRun = DateTime(now.year, now.month, now.day + 1, 0, 0, 0);
    Timer.periodic(const Duration(seconds: 1), (timer) async {
      DateTime currentTime = DateTime.now();
      if (currentTime.isAfter(endRun)) {
        timer.cancel();
        // 다음 날 23:59:00에 다시 실행되도록 타이머 설정
        _scheduleDailyTask();
      } else {
        await StepCounter().resetStep();
      }
    });
  });
}

void handleRouteChange(String? routeName) {
  if (routeName == '/home') {
    AudioManager().play('audio/home.mp3');
  } else if (routeName == '/goal') {
    AudioManager().play('audio/goal.mp3');
  } else if (routeName == '/ranking') {
    AudioManager().play('audio/ranking.mp3');
  } else if (routeName == '/characterinfo') {
    AudioManager().play('audio/character.mp3');
  } else if (routeName == '/gacha') {
    AudioManager().play('audio/gacha.mp3');
  } else if (routeName == '/battleready') {
    AudioManager().play('audio/battleready.mp3');
  } else if (routeName == '/group') {
    AudioManager().play('audio/group.mp3');
  } else if (routeName == '/record') {
    AudioManager().play('audio/record.mp3');
  } else {
    AudioManager().stop();
  }
}
