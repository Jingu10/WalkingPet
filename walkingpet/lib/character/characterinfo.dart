import 'package:flutter/material.dart';
import 'package:walkingpet/character/character_change.dart';
import 'package:walkingpet/character/character_exp.dart';
import 'package:walkingpet/character/widgets/character_stat.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/character_map.dart';
// import 'package:walkingpet/levelup/levelup.dart';
import 'package:walkingpet/services/character/characterinfo.dart';
import 'package:walkingpet/services/character/statpointreset.dart';

// 캐릭터 정보
class CharacterInfo extends StatefulWidget {
  const CharacterInfo({super.key});

  @override
  State<CharacterInfo> createState() => _CharacterInfoState();
}

class _CharacterInfoState extends State<CharacterInfo> {
  // 필요한 변수 만들기
  Map<String, dynamic> characterInfoData = {};
  String animal = "";
  int sp = 0;
  int statPoint = 0;
  bool isLoading = true;

  // statPoint 값을 업데이트하는 메서드
  void updateStatPoint(
      String statnameEn, int point, int addpoint, int newStatPoint) {
    setState(() {
      characterInfoData[statnameEn] = point;
      characterInfoData[
              'add${statnameEn[0].toUpperCase()}${statnameEn.substring(1)}'] =
          addpoint;
      statPoint = newStatPoint;
    });
  }

  @override
  void initState() {
    super.initState();
    initInfo();
  }

  // API 요청으로 데이터 불러오기
  Future<void> initInfo() async {
    try {
      var responseInfo = await getCharacterInfo();

      setState(() {
        characterInfoData = responseInfo['data'];
        int characterId = characterInfoData['characterId'] as int;
        animal = CharacterMap.idToAnimal[characterId] ?? "bunny";
        statPoint = characterInfoData['statPoint'] as int;

        isLoading = false;
      });
    } catch (e) {
      isLoading = false;
    }
  }

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      body: Stack(
        children: [
          // 1. 배경 이미지
          Positioned.fill(
            child: Image.asset(
              'assets/backgrounds/characterinfo.png',
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
          Positioned(
            left: screenWidth * 0.05,
            top: screenHeight * 0.1,
            child: Container(
              width: screenWidth * 0.9,
              height: screenHeight * 0.75,
              decoration: BoxDecoration(
                color:
                    const Color.fromARGB(255, 255, 243, 212).withOpacity(0.75),
                borderRadius: BorderRadius.circular(10),
              ),
            ),
          ),

          // 3. 내용
          if (isLoading)
            const Center(
                child: Text(
              '캐릭터 정보 로딩중..',
              style: TextStyle(
                color: Colors.black,
              ),
            ))
          else
            Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // 1. 유저 닉네임
                  Text(
                    characterInfoData['nickname'] ?? '닉네임로딩중',
                    style: const TextStyle(
                      // fontWeight: FontWeight.bold,
                      fontSize: 35,
                    ),
                  ),
                  const SizedBox(height: 5),

                  // 2. 캐릭터 이미지
                  Image.asset(
                    'assets/animals/$animal/${animal}_idle.gif',
                    height: 200,
                    // scale: 0.3,
                  ),

                  // 3. 캐릭터 변경 버튼
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
                              child: const CharacterChange(),
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

                  // 4. 레벨 & 경험치 바 & 경험치 아이템 사용 버튼 (+버튼)
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 15),
                    child: SizedBox(
                      width: screenWidth * 0.9,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          // 4-1. 레벨
                          Text(
                            'Lv.${characterInfoData['characterLevel']}',
                            style: const TextStyle(
                              fontSize: 23,
                            ),
                          ),

                          // 4-2. 경험치 바
                          SizedBox(
                            width: 200,
                            child: Stack(
                              alignment: Alignment.center,
                              children: [
                                // 4-2-1. Linear Progress Bar
                                SizedBox(
                                  width: 183,
                                  height: 25,
                                  child: ClipRRect(
                                    borderRadius: const BorderRadius.all(
                                        Radius.circular(10)),
                                    child: LinearProgressIndicator(
                                      value: (characterInfoData['experience'] ??
                                                  0)
                                              .toDouble() /
                                          (characterInfoData['maxExperience'] ??
                                                  1)
                                              .toDouble(),
                                      backgroundColor: const Color(0xFF727272),
                                      valueColor:
                                          const AlwaysStoppedAnimation<Color>(
                                              Color(0xFFF3A52F)),
                                    ),
                                  ),
                                ),

                                // 4-2-2. 도트 이미지
                                Image.asset(
                                  'assets/images/character_bar_gray.png',
                                  scale: 0.7,
                                ),

                                // 4-2-3. 경험치 값
                                Text(
                                  '${characterInfoData['experience'] ?? 0}/${characterInfoData['maxExperience'] ?? 0}',
                                  style: const TextStyle(color: Colors.white),
                                )
                              ],
                            ),
                          ),

                          // 4-3. 경험치 아이템 사용 버튼 (페이지 이동)
                          SizedBox(
                            width: 30,
                            child: TextButton(
                              onPressed: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) =>
                                          const CharacterExp()),
                                );
                              },
                              style: TextButton.styleFrom(
                                  padding: EdgeInsets.zero),
                              child: Image.asset(
                                'assets/buttons/yellow_plus_button.png',
                                scale: 0.75,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),

                  // 5. 능력치 & 남은 능력치 포인트 & 초기화 버튼
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    child: SizedBox(
                      width: screenWidth * 0.9,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          // 5-1. 능력치: 체력, 공격력, 방어력
                          Column(
                            children: [
                              CharacterInfoStat(
                                statname: '체력',
                                statnameEn: 'health',
                                point: characterInfoData['health'] ?? 0,
                                addpoint: characterInfoData['addHealth'] ?? 0,
                                characterId:
                                    characterInfoData['characterId'] ?? 0,
                                updateStatPoint: updateStatPoint,
                                statPoint: statPoint,
                              ),
                              CharacterInfoStat(
                                statname: '공격력',
                                statnameEn: 'power',
                                point: characterInfoData['power'] ?? 0,
                                addpoint: characterInfoData['addPower'] ?? 0,
                                characterId:
                                    characterInfoData['characterId'] ?? 0,
                                updateStatPoint: updateStatPoint,
                                statPoint: statPoint,
                              ),
                              CharacterInfoStat(
                                statname: '방어력',
                                statnameEn: 'defense',
                                point: characterInfoData['defense'] ?? 0,
                                addpoint: characterInfoData['addDefense'] ?? 0,
                                characterId:
                                    characterInfoData['characterId'] ?? 0,
                                updateStatPoint: updateStatPoint,
                                statPoint: statPoint,
                              ),
                            ],
                          ),

                          Column(
                            children: [
                              // 5-2. 남은 능력치 포인트
                              const Text(
                                '남은 포인트',
                                style: TextStyle(
                                  fontSize: 13,
                                ),
                              ),
                              Text(
                                statPoint.toString(),
                                style: const TextStyle(
                                  fontSize: 35,
                                ),
                              ),

                              // 5-3. 초기화 버튼
                              Center(
                                child: characterInfoData['initStatus'] == 0
                                    ? TextButton(
                                        onPressed: () async {
                                          await getStatReset();
                                          Navigator.pushNamed(
                                              context, '/characterinfo');
                                        },
                                        child: Image.asset(
                                          'assets/buttons/character_reset_button.png',
                                          scale: 0.8,
                                        ),
                                      )
                                    : TextButton(
                                        onPressed: () {
                                          showDialog(
                                            context: context,
                                            builder: (BuildContext context) {
                                              return AlertDialog(
                                                title: const Center(
                                                    child: Text('안내')),
                                                content: const Text(
                                                  '능력치 초기화는\n하루에 한번만 가능합니다.',
                                                  textAlign: TextAlign.center,
                                                  style:
                                                      TextStyle(fontSize: 16),
                                                ),
                                                actions: <Widget>[
                                                  TextButton(
                                                    child: const Text('확인'),
                                                    onPressed: () {
                                                      Navigator.of(context)
                                                          .pop();
                                                    },
                                                  ),
                                                ],
                                              );
                                            },
                                          );
                                        },
                                        child: Image.asset(
                                          'assets/buttons/character_reset_button_pushed.png',
                                          scale: 0.8,
                                        ),
                                      ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),

          // 4. (임시 코드) levelup 모달
          // Positioned(
          //   bottom: 0,
          //   child: ElevatedButton(
          //     onPressed: () {
          //       showDialog(
          //         context: context,
          //         builder: (BuildContext context) {
          //           return Dialog(
          //             shape: RoundedRectangleBorder(
          //                 borderRadius: BorderRadius.circular(20.0)),
          //             child: Container(
          //               width: 300,
          //               height: 500,
          //               decoration: BoxDecoration(
          //                 borderRadius: BorderRadius.circular(20.0),
          //               ),
          //               child: const LevelUp(),
          //             ),
          //           );
          //         },
          //       );
          //     },
          //     child: const Text('Level Up Modal'),
          //   ),
          // ),
        ],
      ),
      bottomNavigationBar: const BottomNavBar(
        selectedIndex: 0,
      ),
    );
  }
}
