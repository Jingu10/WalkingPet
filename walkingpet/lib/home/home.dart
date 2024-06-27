import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/common/exit_alert_modal.dart';
import 'package:walkingpet/home/widgets/logout_modal.dart';
import 'package:walkingpet/providers/character_info.dart';
import 'package:walkingpet/providers/step_counter.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';
import 'package:walkingpet/home/widgets/toprighticonwithttext.dart';
import 'package:provider/provider.dart';
import 'package:walkingpet/services/audio/audio_manager.dart';
import 'package:walkingpet/services/home/gethomecharacter.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    final characterProvider =
        Provider.of<CharacterProvider>(context, listen: false);
    if (characterProvider.nickname.isEmpty) {
      initInfo();
    } else {
      isLoading = false;
    }
  }

// 캐릭터 초기화
  Future<void> initInfo() async {
    final characterProvider =
        Provider.of<CharacterProvider>(context, listen: false);

    try {
      var responseInfo = await getHomeCharacter();
      var characterInfoData = responseInfo['data'];
      int characterId = characterInfoData['characterId'] as int;
      String nickname = characterInfoData['nickname'] as String;
      characterProvider.updateCharacter(characterId, nickname);

      setState(() {
        isLoading = false;
      });
    } catch (e) {}
  }

  // 로그아웃
  Future<void> deleteTokens() async {
    const storage = FlutterSecureStorage();
    await storage.delete(key: 'ACCESS_TOKEN');
    await storage.delete(key: 'REFRESH_TOKEN');
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
            image: AssetImage("assets/backgrounds/day.png"), // 이미지 파일 경로 지정
            fit: BoxFit.cover, // 배경 이미지가 전체 화면을 채우도록 설정
          ),
        ),
        child: Scaffold(
          resizeToAvoidBottomInset: false,
          backgroundColor: Colors.transparent, // Scaffold 배경을 투명하게 설정
          body: Stack(
            children: [
              Positioned(
                bottom: screenHeight * 0.2, // 화면 아래에서 20% 위치
                left: 0,
                right: 0,
                child: Center(
                  child: MainFontStyle(
                    size: screenWidth * 0.033,
                    text: "걸음수는 자정에 초기화됩니다",
                    color: Colors.yellow,
                  ),
                ),
              ),
              Center(
                child: Column(
                  children: [
                    SizedBox(
                      height: screenHeight * 0.04,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // 로그아웃 아이콘
                        Transform.translate(
                          offset: Offset(screenWidth * 0.01, 0),
                          child: IconButton(
                            onPressed: () async {
                              // 로그아웃 여부 확인 모달
                              bool? confirmLogout =
                                  await LogoutModal.showLogoutModal(
                                      context: context);

                              // 사용자가 로그아웃을 확인했을 때만 로그아웃을 진행
                              if (confirmLogout == true) {
                                // 로그아웃 함수
                                try {
                                  await UserApi.instance.logout();
                                  await deleteTokens(); // 로그아웃 시 토큰 삭제
                                  Navigator.pushNamed(
                                      context, '/login'); // 로그인 페이지로 이동
                                } catch (error) {
                                  AccessTokenInfo tokenInfo =
                                      await UserApi.instance.accessTokenInfo();
                                }
                              }
                            },
                            icon: Image.asset(
                              'assets/icons/logout.png',
                              // width: svgWidth,
                              height: screenHeight * 0.07,
                            ),
                          ),
                        ),

                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            Transform.translate(
                              offset: Offset(screenWidth * 0.10, 0),
                              child: const TopRightIconWithText(
                                  icon: "record", text: "기록"),
                            ),
                            Transform.translate(
                              offset: Offset(screenWidth * 0.05, 0),
                              child: const TopRightIconWithText(
                                  icon: "goal", text: "목표"),
                            ),
                            const TopRightIconWithText(
                                icon: "ranking", text: "랭킹"),
                            SizedBox(
                              width: screenWidth * 0.01,
                            ),
                          ],
                        ),
                      ],
                    ),
                    SizedBox(
                      height: screenHeight * 0.05,
                    ),
                    MainFontStyle(size: screenWidth * 0.1, text: "걸음수"),
                    Transform.translate(
                      offset: Offset(0, screenHeight * -0.03),
                      child: Consumer<StepCounter>(
                          builder: (context, provider, child) {
                        return MainFontStyle(
                          size: screenWidth * 0.3,
                          text: "${provider.steps}",
                        );
                      }),
                    ),
                    SizedBox(
                      height: screenHeight * 0.06,
                    ),
                    if (!isLoading)
                      Consumer<CharacterProvider>(
                        builder: (context, characterProvider, child) {
                          String animal = CharacterMap
                                  .idToAnimal[characterProvider.characterId] ??
                              "bunny";
                          return SizedBox(
                            height: screenHeight * 0.22,
                            child: Image.asset(
                              'assets/animals/$animal/${animal}_walk.gif',
                              fit: BoxFit.fitHeight,
                            ),
                          );
                        },
                      ),
                  ],
                ),
              ),
              const Positioned(
                bottom: 0,
                left: 0,
                right: 0,
                child: BottomNavBar(
                  selectedIndex: 2,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
