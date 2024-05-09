import 'package:flutter/material.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/services/character/characterexpitem.dart';
import 'package:walkingpet/services/character/characterinfo.dart';

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
  double? expValue;
  bool isLoading = true;
  int expitemCount = 0;

  Map<String, dynamic> characterExpData = {};
  int? quantity;

  @override
  void initState() {
    super.initState();
    initInfo();
    // Expitem(quantity as int);
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
        expValue = (characterInfoData['experience'] ?? 0).toDouble() /
            (characterInfoData['maxExperience'] ?? 1).toDouble();

        print(characterInfoData);
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
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    child: Text(
                      characterInfoData['nickname'] ?? '닉네임로딩중',
                      style: const TextStyle(
                        fontSize: 35,
                      ),
                    ),
                  ),

                  // 2. 캐릭터 이미지
                  Image.asset(
                    'assets/animals/$animal/${animal}_idle.gif',
                    height: 200,
                    // scale: 0.3,
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
                            'Lv.$characterLevel',
                            style: const TextStyle(
                              fontSize: 23,
                            ),
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
                                      valueColor:
                                          const AlwaysStoppedAnimation<Color>(
                                              Color(0xFFF3A52F)),
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
                                  '${characterInfoData['experience'] ?? 0}/${characterInfoData['maxExperience'] ?? 0}',
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
                            onTap: expitemCount > 0
                                ? () => setState(() => expitemCount--)
                                : null,
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
                            onTap: expitemCount < characterInfoData['quantity']
                                ? () => setState(() => expitemCount++)
                                : null,
                            child: Image.asset(
                              'assets/buttons/yellow_plus_button.png',
                              scale: 0.75,
                            ),
                          ),

                          // 4-6. 'MAX' 버튼
                          GestureDetector(
                            onTap: expitemCount < characterInfoData['quantity']
                                ? () => setState(() => expitemCount =
                                    characterInfoData['quantity'])
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
                          characterInfoData['quantity'].toString(),
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
                        onPressed: () {
                          Navigator.pushNamed(context, '/characterinfo');
                        },
                        child: Image.asset(
                          'assets/buttons/red_cancle_button.png',
                          scale: 0.7,
                        ),
                      ),

                      // 4-8-2. '사용' 버튼
                      TextButton(
                        onPressed: () async {
                          // await getStatReset();
                          Navigator.pushNamed(context, '/characterinfo');
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
