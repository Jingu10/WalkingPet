import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:kakaomap_webview/kakaomap_webview.dart';
import 'package:provider/provider.dart';
import 'package:walkingpet/providers/character_info.dart';
import 'package:walkingpet/services/Interceptor.dart';
import 'package:walkingpet/services/record/camera.dart';
import 'package:walkingpet/services/record/clickmarker.dart';
import 'package:walkingpet/services/record/eventmarkers.dart';
import 'package:walkingpet/services/record/usermarkers.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';
import 'dart:io';
//모달창
import 'package:walkingpet/record/show_record.dart';

class Record extends StatefulWidget {
  const Record({super.key});

  @override
  State<Record> createState() => _RecordState();
}

class _RecordState extends State<Record> {
  // 지도 업로드에 필요한 변수
  // 현 위치
  late Future<Position> _currentPositionFuture;
  late Stream<Position> positionStream; // 실시간 위치 파악 위함
  double currentLat = 36.355387454337716;
  double currentLng = 127.29839622974396;
  // 마커
  List<dynamic> eventmarkers = []; // 이벤트 마커
  List<dynamic> usermarkers = []; // 사용자 마커
  bool isLoading = true;

  // 마커 클릭 후를 위해 필요한 변수
  int recordId = 0;

  @override
  void initState() {
    super.initState();
    initInfo();
    // 실시간 위치 파악 위함
  }

  // 현재 위치 가져오기
  Future<Position> _getCurrentLocation() async {
    // 현재 위치 가져오기
    Position position = await Geolocator.getCurrentPosition();
    setState(() {
      currentLat = position.latitude;
      currentLng = position.longitude;
    });
    return position;
  }

  // 실시간 위치 업데이트 설정
  void _startLocationUpdates() {
    // 추가된 부분
    const locationSettings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: 10,
    );

    positionStream =
        Geolocator.getPositionStream(locationSettings: locationSettings);

    positionStream.listen((Position position) {
      setState(() {
        currentLat = position.latitude;
        currentLng = position.longitude;
      });
    });
  }

  // API 요청으로 데이터 불러오기
  Future<void> initInfo() async {
    try {
      var responseEvents = await getEventMarkers();
      var responseUsers = await getUserMarkers();
      setState(() {
        eventmarkers = responseEvents['data']['eventRecordList'];
        usermarkers = responseUsers['data']['normalRecordList'];
        // isLoading = false;
      });
      _currentPositionFuture = _getCurrentLocation();
      _startLocationUpdates();
    } catch (e) {
      // isLoading = false;
    }
  }

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    final String? kakaoMapKey = dotenv.env['MAP_APP_KEY'];

    return Scaffold(
      resizeToAvoidBottomInset: false,
      body: Stack(
        children: [
          // 1. 배경 이미지
          Positioned.fill(
            child: Image.asset(
              'assets/backgrounds/map_road.png',
              fit: BoxFit.cover,
            ),
          ),

          // 2. 투명 레이어 (전체 영역)
          Positioned(
            child: Container(
              width: screenWidth,
              height: screenHeight,
              color: const Color.fromARGB(255, 255, 255, 255).withOpacity(0.4),
            ),
          ),

          // 2. 투명 레이어 (특정 영역만)
          // Positioned(
          //   left: screenWidth * 0.05,
          //   top: screenHeight * 0.15,
          //   child: Container(
          //     width: screenWidth * 0.9,
          //     height: screenHeight * 0.8,
          //     decoration: BoxDecoration(
          //       color:
          //           const Color.fromARGB(255, 255, 243, 212).withOpacity(0.75),
          //       borderRadius: BorderRadius.circular(10),
          //     ),
          //   ),
          // ),

          // 3. 내용
          // 3-1. 기록 & 'X' 버튼
          Padding(
            padding: const EdgeInsets.only(top: 40),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('       '),
                const MainFontStyle(size: 40, text: '기록'),
                TextButton(
                  onPressed: () async {
                    Navigator.pop(context);
                    // Navigator.pushReplacementNamed(
                    //     context, '/home'); // 현재 경로를 '/home'으로 교체
                  },
                  style: ButtonStyle(
                    padding: MaterialStateProperty.all(
                        EdgeInsets.zero), // 버튼의 내부 패딩 제거
                  ),
                  child: const Text(
                    'X',
                    style: TextStyle(fontSize: 30, color: Colors.black),
                  ),
                )
              ],
            ),
          ),

          // 앱 업로드를 위한 임시 코드
          // Positioned(
          //   left: screenWidth * 0.05,
          //   top: screenHeight * 0.15,
          //   child: Container(
          //     width: screenWidth * 0.9,
          //     height: screenHeight * 0.8,
          //     decoration: BoxDecoration(
          //       color:
          //           const Color.fromARGB(255, 255, 243, 212).withOpacity(0.75),
          //       borderRadius: BorderRadius.circular(10),
          //     ),
          //   ),
          // ),

          // const Center(
          //   child: Text(
          //     '캐릭터와 함께 걸은\n나만의 장소를 기록하세요\n\n(준비중)',
          //     style: TextStyle(fontSize: 20, color: Colors.black),
          //     textAlign: TextAlign.center,
          //   ),
          // ),

          // 3-2. 지도 KakaoMapView
          FutureBuilder<Position>(
            future: _currentPositionFuture,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const MainFontStyle(
                        size: 20,
                        text: '... 로딩중 ...',
                      ),
                      SizedBox(
                        height: screenHeight * 0.05,
                      ),
                      const CircularProgressIndicator(),
                    ],
                  ),
                );
              } else if (snapshot.hasError) {
                return Center(
                  child: Text('위치를 불러오는 중 오류가 발생했습니다: ${snapshot.error}'),
                );
              } else {
                WidgetsBinding.instance.addPostFrameCallback((_) {
                  setState(() {
                    isLoading = false;
                  });
                });

                return Positioned(
                  left: screenWidth * 0.05,
                  top: screenHeight * 0.15,
                  child: KakaoMapView(
                    width: screenWidth * 0.9,
                    height: screenHeight * 0.7,
                    kakaoMapKey: kakaoMapKey!,
                    lat: currentLat, // 현재 위도
                    lng: currentLng, // 현재 경도
                    zoomLevel: 3, // 초기 줌 레벨

                    customScript: '''
                      // 현재 위치 마커
                      var currentMarkerImageSrc = 'https://ifh.cc/g/hFVWjs.png',
                          currentMarkerImageSize = new kakao.maps.Size(31, 42),
                          currentMarkerImageOption = {offset: new kakao.maps.Point(0, 0)};

                      var currentMarkerImage = new kakao.maps.MarkerImage(currentMarkerImageSrc, currentMarkerImageSize, currentMarkerImageOption);

                      var currentMarkerPosition = new kakao.maps.LatLng($currentLat, $currentLng);
                      var currentMarker = new kakao.maps.Marker({position: currentMarkerPosition, image: currentMarkerImage});
                      currentMarker.setMap(map);  

                      // 이벤트 마커 담을 변수
                      var eventMarkers = ${jsonEncode(eventmarkers)};

                      // 마커 이미지 => 이벤트 팻말
                      var eventImageSrc = 'https://ifh.cc/g/0kW3Od.png', // 마커이미지의 주소입니다
                          eventImageSize = new kakao.maps.Size(40, 40), // 마커이미지의 크기입니다
                          eventImageOption = {offset: new kakao.maps.Point(0, 0)}; // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.

                      var eventMarkerImage = new kakao.maps.MarkerImage(eventImageSrc, eventImageSize, eventImageOption);
                      
                      // 이벤트 마커 추가할 함수
                      function addEventMarker(position, recordId) {
                        var marker = new kakao.maps.Marker({position: position, image: eventMarkerImage});
                        marker.setMap(map);
                        return marker;
                      }
                      
                      // 이벤트 마커 표시하기 (반복문 활용)
                      for(var i = 0 ; i < eventMarkers.length ; i++){
                        var marker = addEventMarker(new kakao.maps.LatLng(eventMarkers[i].latitude, eventMarkers[i].longitude), eventMarkers[i].recordId);
                        kakao.maps.event.addListener(marker, 'click', (function(i) {
                          return function(){
                            onTapMarker.postMessage(JSON.stringify({type: '이벤트', recordId: eventMarkers[i].recordId}));
                          };
                        })(i));
                      }
                      
                      // 사용자 마커 담을 변수
                      var userMarkers = ${jsonEncode(usermarkers)};

                      // 마커 이미지 => 사용자 팻말
                      var userImageSrc = 'https://ifh.cc/g/CqdgWa.png', // 마커이미지의 주소입니다
                          userImageSize = new kakao.maps.Size(40, 40), // 마커이미지의 크기입니다
                          userImageOption = {offset: new kakao.maps.Point(0, 0)}; // 마커이미지의 옵션입니다. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정합니다.

                      var userMarkerImage = new kakao.maps.MarkerImage(userImageSrc, userImageSize, userImageOption);
                      
                      // 사용자 마커 추가할 함수
                      function addUserMarker(position, recordId) {
                        var marker = new kakao.maps.Marker({position: position, image: userMarkerImage});
                        marker.setMap(map);
                        return marker;
                      }

                      // 사용자 마커 표시하기 (반복문 활용)
                      for(var i = 0 ; i < userMarkers.length ; i++){
                        var marker = addUserMarker(new kakao.maps.LatLng(userMarkers[i].latitude, userMarkers[i].longitude), userMarkers[i].recordId);
                        kakao.maps.event.addListener(marker, 'click', (function(i) {
                          return function(){
                            onTapMarker.postMessage(JSON.stringify({type: '다른 유저의 기록', recordId: userMarkers[i].recordId}));
                          };
                        })(i));
                      }
                    ''',

                    onTapMarker: (message) async {
                      var data = jsonDecode(message.message);
                      var recordId = data['recordId'];

                      var res = await getClickMarker(
                          currentLat, currentLng, recordId);
                      //얘 오류나서 잠깐 주석함
                      print(res);

                      var record = res['data']['selectUserRecord'];
                      if (record != null) {
                        String nickname = record['nickname'];
                        int characterId = record['characterId'];
                        String imageUrl = record['imageUrl'];
                        String regDate = record['regDate'];

                        showRecord(
                            context, nickname, characterId, imageUrl, regDate);
                      }
                    },

                    // onTapMarker: (message) {
                    //   getClickMarker(currentLat, currentLng, recordId);
                    //   // ScaffoldMessenger.of(context).showSnackBar(
                    //   //     SnackBar(content: Text(message.message)));
                    // },
                  ),
                );
              }
            },
          ),

          // 3-3. 내 기록 / 기록 생성
          if (!isLoading)
            Positioned(
              left: screenWidth * 0.1,
              right: screenWidth * 0.1,
              bottom: screenHeight * 0.05,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // GestureDetector(
                  //   onTap: () {
                  //     print('내 기록 보기 버튼 클릭');
                  //   },
                  //   child: Stack(
                  //     alignment: Alignment.center,
                  //     children: [
                  //       Image.asset(
                  //         'assets/buttons/green_short_button.png',
                  //         height: screenHeight * 0.06,
                  //         fit: BoxFit.cover,
                  //       ),
                  //       const Text(
                  //         '내 기록',
                  //         style: TextStyle(fontSize: 20),
                  //       )
                  //     ],
                  //   ),
                  // ),
                  GestureDetector(
                    onTap: () async {
                      File result = await pickImage();
                      final res = await uploadImage(
                          imageFile: result,
                          characterId: Provider.of<CharacterProvider>(context,
                                  listen: false)
                              .characterId,
                          latitude: currentLat,
                          longitude: currentLng);
                      Navigator.pushReplacementNamed(context, '/record');
                    },
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        Image.asset(
                          'assets/buttons/green_short_button.png',
                          height: screenHeight * 0.06,
                          fit: BoxFit.cover,
                        ),
                        const Text(
                          '기록하기',
                          style: TextStyle(fontSize: 20),
                        )
                      ],
                    ),
                  ),
                ],
              ),
            ),
        ],
      ),
    );
  }
}
