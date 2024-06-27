import 'package:flutter/material.dart';
import 'package:walkingpet/character/widgets/levelup.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/services/character/characterexpitem.dart';

// 캐릭터 경험치 아이템 사용 페이지
class CharacterExp extends StatefulWidget {
  const CharacterExp({super.key});

  @override
  State<CharacterExp> createState() => _CharacterExpState();
}

class _CharacterExpState extends State<CharacterExp> {
  // 필요한 변수 만들기
  Map<String, dynamic> characterInfoData = {};
  String animal = "";
  int? characterLevel;

  //현재 레벨
  int fixLevel = 0;
  int fixExperience = 0;
  int fixMaxExperience = 0;

  //현재 레벨과 현재 경험치
  int experience = 1;
  int level = 0;
  int maxExperience = 1;
  double? expValue;

  bool isLoading = true;

  int quantity = 0; // API 요청으로 받아오는 '경험치 아이템' 값
  int expitemCount = 0; // 알고리즘 구현을 위한 값

  @override
  void initState() {
    super.initState();
    initInfo();
  }

  // API 요청으로 데이터 불러오기
  Future<void> initInfo() async {
    try {
      var responseInfo = await getExpitemInfo();

      setState(() {
        characterInfoData = responseInfo['data'];

        int characterId = characterInfoData['characterId'] as int;
        animal = CharacterMap.idToAnimal[characterId] ?? "bunny";

        characterLevel = characterInfoData['characterLevel'];
        experience = characterInfoData['experience'];
        maxExperience = characterInfoData['maxExperience'];
        level = characterInfoData['characterLevel'];

        fixLevel = characterInfoData['characterLevel'];
        fixExperience = characterInfoData['experience'];

        expValue = experience / maxExperience;

        quantity = characterInfoData['quantity'];

        // print(characterInfoData);
        isLoading = false;
      });
    } catch (e) {
      isLoading = false;
    }
  }

  // Java에서 가져온 함수를 Dart로 변환
  int getMaxExperience(int level) {
    if (level == 1) {
      return 10;
    } else {
      return level * (level - 1) * 5 ~/ 2 + 10;
    }
  }

  List<int> getLevel(int fixExprience) {
    int tempLevel = fixLevel;
    int tempExperience = fixExprience;
    int tempMaxExperience = getMaxExperience(tempLevel);
    while (tempExperience >= tempMaxExperience) {
      tempExperience -= tempMaxExperience;
      tempLevel++;
      tempMaxExperience = getMaxExperience(tempLevel);
    }

    return [tempLevel, tempExperience];
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
            top: screenHeight * 0.085,
            child: Container(
              width: screenWidth * 0.9,
              height: screenHeight * 0.77,
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
              '캐릭터 경험치 UP 로딩중..',
              style: TextStyle(
                color: Colors.black,
              ),
            ))
          else
            Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  // 1. 유저 닉네임
                  Padding(
                    padding: const EdgeInsets.only(top: 15, bottom: 10),
                    child: Text(
                      characterInfoData['nickname'] ?? '닉네임로딩중',
                      style: const TextStyle(
                        fontSize: 32,
                      ),
                    ),
                  ),

                  // 2. 캐릭터 이미지
                  Stack(
                    children: [
                      Center(
                        child: Image.asset(
                          'assets/animals/$animal/${animal}_idle.gif',
                          height: 200,
                          // scale: 0.3,
                        ),
                      ),

                      // 캐릭터 등급(별)
                      Row(
                        // mainAxisSize: MainAxisSize.min,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: List.generate(
                            characterInfoData['characterGrade'], (index) {
                          return Image.asset(
                            'assets/items/one_star.png',
                            width: screenWidth * 0.08,
                          );
                        }),
                      ),
                    ],
                  ),

                  // 3. 레벨 & 경험치 바
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 15),
                    child: SizedBox(
                      width: screenWidth * 0.9,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          // 3-1. 레벨
                          Text(
                            'Lv.$level',
                            style: level == fixLevel
                                ? const TextStyle(
                                    fontSize: 23, color: Colors.black)
                                : // 기본 스타일
                                const TextStyle(
                                    fontSize: 23,
                                    color: Color.fromARGB(255, 88, 169, 38),
                                    fontWeight: FontWeight.bold), // 변경된 스타일
                          ),

                          // 3-2. 경험치 바
                          SizedBox(
                            width: 200,
                            child: Stack(
                              alignment: Alignment.center,
                              children: [
                                // 3-2-1. Linear Progress Bar
                                SizedBox(
                                  width: 183,
                                  height: 25,
                                  child: ClipRRect(
                                    borderRadius: const BorderRadius.all(
                                        Radius.circular(10)),
                                    child: LinearProgressIndicator(
                                      value: expValue,
                                      backgroundColor: const Color(0xFF727272),
                                      valueColor: expitemCount == 0
                                          ? const AlwaysStoppedAnimation<Color>(
                                              Color(0xFFF3A52F))
                                          : const AlwaysStoppedAnimation<Color>(
                                              Color(0xFF69C62F)),
                                    ),
                                  ),
                                ),

                                // 3-2-2. 도트 이미지
                                Image.asset(
                                  'assets/images/character_bar_gray.png',
                                  scale: 0.7,
                                ),

                                // 3-2-3. 경험치 값
                                Text(
                                  '$experience/$maxExperience',
                                  // '${experience ?? 0}/${maxExperience ?? 1}',
                                  style: const TextStyle(color: Colors.white),
                                )
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),

                  // 4. Exp item 관련
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      // 4-1. EXP item 이미지
                      Padding(
                        padding: const EdgeInsets.only(right: 20),
                        child: Image.asset(
                          'assets/images/character_expitem.png',
                          height: 80,
                        ),
                      ),

                      // 4-2. EXP item 설명
                      SizedBox(
                        height: 90,
                        width: 160,
                        child: DecoratedBox(
                          decoration: BoxDecoration(
                            color: const Color.fromARGB(255, 255, 255, 255)
                                .withOpacity(0.65),
                            borderRadius: BorderRadius.circular(5),
                          ),
                          child: const Center(
                            child: Text(
                              '캐릭터의 경험치를\n5 올릴 수 있는\n아이템입니다.',
                              textAlign: TextAlign.center,
                              style: TextStyle(
                                fontSize: 14,
                                color: Colors.black,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),

                  Padding(
                    padding: const EdgeInsets.only(top: 20, bottom: 10),
                    child: SizedBox(
                      width: screenWidth * 0.7,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          // 4-3. '-' 버튼
                          GestureDetector(
                            onTap: () {
                              if (expitemCount > 0) {
                                setState(() {
                                  expitemCount--;
                                  if (experience == 0) {
                                    level--;
                                    maxExperience = getMaxExperience(level);
                                    experience = maxExperience - 5;
                                  } else {
                                    experience -= 5;
                                  }
                                  fixExperience -= 5;
                                  expValue = experience /
                                      maxExperience; // expValue 값 변경
                                });
                              }
                            },
                            child: Image.asset(
                              'assets/buttons/yellow_minus_button.png',
                              scale: 0.75,
                            ),
                          ),

                          // 4-4. 사용할 경험치 아이템 개수 표시
                          SizedBox(
                            height: 30,
                            width: 70,
                            child: DecoratedBox(
                              decoration: BoxDecoration(
                                color: const Color.fromARGB(255, 255, 255, 255)
                                    .withOpacity(0.65),
                                borderRadius: BorderRadius.circular(5),
                              ),
                              child: Center(
                                child: Text(
                                  '$expitemCount',
                                  // 어떤 코드인지 고민 더 필요
                                  style: Theme.of(context).textTheme.titleLarge,
                                ),
                              ),
                            ),
                          ),

                          // 4-5. '+' 버튼
                          GestureDetector(
                            onTap: () {
                              if (expitemCount < quantity) {
                                setState(() {
                                  expitemCount++;
                                  experience += 5;
                                  if (experience == maxExperience) {
                                    level++;
                                    experience = 0;
                                    maxExperience = getMaxExperience(level);
                                  }
                                  fixExperience += 5;
                                  expValue = experience /
                                      maxExperience; // expValue 값 변경
                                });
                              }
                            },
                            child: Image.asset(
                              'assets/buttons/yellow_plus_button.png',
                              scale: 0.75,
                            ),
                          ),

                          // 4-6. 'MAX' 버튼
                          GestureDetector(
                            onTap: expitemCount < quantity
                                ? () {
                                    setState(() {
                                      int remainingItems =
                                          quantity - expitemCount;
                                      fixExperience +=
                                          remainingItems * 5; // 경험치 추가
                                      expitemCount = quantity;

                                      List<int> levelData =
                                          getLevel(fixExperience);
                                      level = levelData[0]; // 레벨 업데이트
                                      experience = levelData[1]; // 경험치 업데이트
                                      maxExperience = getMaxExperience(
                                          level); // 최대 경험치 업데이트
                                      expValue = experience /
                                          maxExperience; // expValue 수정
                                    });
                                  }
                                : null,
                            child: Image.asset(
                              'assets/buttons/yellow_max_button.png',
                              scale: 0.75,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),

                  // 4-7. 경험치 아이템 보유 개수 표시
                  Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text(
                          '총 ',
                          style: TextStyle(fontSize: 16),
                        ),
                        Text(
                          quantity.toString(),
                          style: const TextStyle(fontSize: 22),
                        ),
                        const Text(
                          '개 보유',
                          style: TextStyle(fontSize: 16),
                        ),
                      ],
                    ),
                  ),

                  // 4-8. 경험치 아이템 관련 버튼
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // 4-8-1. '취소' 버튼
                      TextButton(
                        onPressed: () async {
                          Navigator.of(context).pop();
                        },
                        child: Image.asset(
                          'assets/buttons/red_cancle_button.png',
                          scale: 0.7,
                        ),
                      ),

                      // 4-8-2. '사용' 버튼 => 5. API 요청
                      TextButton(
                        onPressed: () async {
                          if (expitemCount == 0) {
                            return;
                          }

                          // 5-1. API 요청 및 데이터 처리
                          var responseExpitem = await getExpitem(expitemCount);
                          var expitemData = responseExpitem['data'];
                          var isLevelUp = expitemData['isLevelUp'];
                          var levelUpInfo = expitemData['levelUpInfo'];

                          // 5-2. 레벨업 : O => 레벨업 모달 이동 / X => 캐릭터 정보 페이지 이동
                          if (isLevelUp) {
                            await showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return Dialog(
                                  shape: RoundedRectangleBorder(
                                      borderRadius:
                                          BorderRadius.circular(20.0)),
                                  child: Container(
                                    width: 300,
                                    height: 500,
                                    decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(20.0),
                                    ),
                                    child: LevelUpModal(
                                        levelUpInfo: levelUpInfo,
                                        animal: animal),
                                  ),
                                );
                              },
                            );
                          }
                          Navigator.of(context).pop();
                          Navigator.pushReplacementNamed(
                              context, '/characterinfo');
                        },
                        child: Image.asset(
                          'assets/buttons/green_use_button.png',
                          scale: 0.7,
                        ),
                      ),
                    ],
                  )
                ],
              ),
            ),
        ],
      ),
      bottomNavigationBar: const BottomNavBar(
        selectedIndex: 0,
      ),
    );
  }
}
