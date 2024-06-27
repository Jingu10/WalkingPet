import 'package:flutter/material.dart';
import 'package:walkingpet/battle/battle.dart';
import 'package:walkingpet/battle/widgets/character_change.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/common/exit_alert_modal.dart';
// import 'package:walkingpet/common/star.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';
import 'package:walkingpet/main.dart';
import 'package:walkingpet/services/audio/audio_manager.dart';
import 'package:walkingpet/services/battle/getmyinfo.dart';

class BattleReady extends StatefulWidget {
  const BattleReady({super.key});

  @override
  State<BattleReady> createState() => _BattleReadyState();
}

class _BattleReadyState extends State<BattleReady> {
  Map<String, dynamic> characterData = {};
  String animal = "";
  bool isLoading = true;
  int upgrade = 0;

  @override
  void initState() {
    super.initState();
    initMyInfo(); // 위젯이 로드될 때 fetchData 호출
    AudioManager().play('audio/battleReady.mp3');
  }

  @override
  void dispose() {
    AudioManager().stop();
    super.dispose();
  }

  Future<void> initMyInfo() async {
    try {
      var response = await getMyInfo();

      setState(() {
        characterData = response['data']; // API 응답을 상태에 저장
        int characterId = characterData['characterId']
            as int; // API에서 characterId가 int 타입이라고 가정
        animal = CharacterMap.idToAnimal[characterId] ??
            "unKnown"; // characterId에 해당하는 동물이 없을 경우 "unKnown"을 사용
        upgrade = characterData['upgrade'];
        isLoading = false;
      });
    } catch (e) {
      isLoading = false;
    }
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return PopScope(
      canPop: false,
      onPopInvoked: (didPop) {
        handleExit(context);
      },
      child: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage(
                "assets/backgrounds/battleReady.jpg"), // 이미지 파일 경로 지정
            fit: BoxFit.cover, // 배경 이미지가 전체 화면을 채우도록 설정
          ),
        ),
        child: Scaffold(
          backgroundColor: Colors.transparent, // Scaffold의 배경을 투명하게 설정
          body: Stack(
            children: [
              if (!isLoading)
                Container(
                  margin: EdgeInsets.fromLTRB(
                    screenWidth * 0.055,
                    screenHeight * 0.05,
                    screenWidth * 0.055,
                    screenHeight * 0.12,
                  ), // 좌, 상, 우, 하 각각 다른 마진 적용

                  padding: EdgeInsets.fromLTRB(
                    screenWidth * 0.05,
                    screenHeight * 0.02,
                    screenWidth * 0.05,
                    screenHeight * 0.02,
                  ), // 모든 방향에 16.0의 패딩을 적용
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.6), // 투명한 흰색 배경 추가
                    borderRadius: BorderRadius.circular(10.0), // 모서리를 둥글게 처리
                  ),
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        MainFontStyle(
                            size: screenWidth * 0.1,
                            text: characterData['nickname']), // 동적 데이터 사용
                        MainFontStyle(
                            size: screenWidth * 0.08,
                            text: "점수: ${characterData['rating']}"),
                        SizedBox(
                          height: screenHeight * 0.02,
                        ),

                        // 캐릭터 등급(별)
                        Row(
                          mainAxisSize: MainAxisSize.min,
                          children: List.generate(
                              characterData['characterGrade'], (index) {
                            return Image.asset(
                              'assets/items/one_star.png',
                              width: screenWidth * 0.08,
                            );
                          }),
                        ),

                        SizedBox(
                          height: screenHeight * 0.01,
                        ),
                        Center(
                          child: MainFontStyle(
                            size: screenWidth * 0.07,
                            text: "+$upgrade",
                            color: Colors.redAccent,
                            whiteOffset: true,
                          ),
                        ),

                        Image.asset(
                          'assets/animals/$animal/${animal}_idle.gif',
                        ),

                        Transform.translate(
                          offset: Offset(0, screenHeight * -0.02),
                          child: MainFontStyle(
                              size: screenWidth * 0.08,
                              text: "Lv.${characterData['level']}"),
                        ),

                        TextButton(
                          onPressed: () {
                            showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return Dialog(
                                  shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(5)),
                                  child: Container(
                                    width: screenWidth,
                                    height: screenHeight * 0.6,
                                    decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(5),
                                    ),
                                    child: const BattleCharacterChange(),
                                  ),
                                );
                              },
                            );
                          },
                          child: Image.asset(
                            'assets/buttons/character_change_button.png',
                            scale: 0.85,
                          ),
                        ),

                        SizedBox(
                          height: screenHeight * 0.05,
                        ),

                        InkWell(
                            onTap: () {
                              if (characterData['battleCount'] > 0) {
                                Navigator.pushReplacement(
                                  context,
                                  MaterialPageRoute(
                                    builder: (context) =>
                                        Battle(myCharacterData: characterData),
                                  ),
                                );
                              }
                            },
                            child: Stack(
                              alignment: Alignment.center,
                              children: [
                                ClipRRect(
                                  borderRadius: BorderRadius.circular(20),
                                  child: ColorFiltered(
                                    colorFilter:
                                        characterData['battleCount'] > 0
                                            ? const ColorFilter.mode(
                                                Colors.transparent,
                                                BlendMode.multiply,
                                              )
                                            : const ColorFilter.mode(
                                                Colors.grey,
                                                BlendMode.saturation,
                                              ),
                                    child: Image.asset(
                                        'assets/buttons/battle_start_button.png'),
                                  ),
                                ),
                                Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      "Battle Start!",
                                      style: TextStyle(
                                          fontSize: screenWidth * 0.05),
                                    ),
                                    Text(
                                      "일일 배틀 횟수: ${characterData['battleCount']}/10",
                                      style: TextStyle(
                                          fontSize: screenWidth * 0.035),
                                    ),
                                  ],
                                ),
                              ],
                            )),
                      ],
                    ),
                  ),
                ),
              const Positioned(
                bottom: 0,
                left: 0,
                right: 0,
                child: BottomNavBar(
                  selectedIndex: 3,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
