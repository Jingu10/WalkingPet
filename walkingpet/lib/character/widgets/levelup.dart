import 'package:flutter/material.dart';

class LevelUpModal extends StatelessWidget {
  final Map<String, dynamic> levelUpInfo;
  final String animal;

  const LevelUpModal(
      {super.key, required this.levelUpInfo, required this.animal});

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Stack(
      children: [
        // 1. 배경 이미지
        Positioned.fill(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(10),
            child: Image.asset(
              'assets/backgrounds/levelup_sunset.png',
              fit: BoxFit.cover,
            ),
          ),
        ),

        // 2. 투명 레이어 (전체 영역)
        Positioned(
          child: Container(
            width: screenWidth,
            height: screenHeight,
            decoration: BoxDecoration(
              color: const Color.fromARGB(255, 255, 255, 255).withOpacity(0.3),
              borderRadius: BorderRadius.circular(10),
            ),
          ),
        ),

        // 3. 내용
        Center(
          child: Stack(
            children: [
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // 2. 캐릭터 이미지
                  Image.asset(
                    'assets/animals/$animal/${animal}_idle.gif',
                    height: 200,
                    scale: 0.3,
                  ),

                  // 3. 레벨 상승 표시
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Text(
                        'Lv.${levelUpInfo['nowLevel']}',
                        style: const TextStyle(fontSize: 20),
                      ),
                      const Text('  >  '),
                      Text(
                        'Lv.${levelUpInfo['nextLevel']}',
                        style: const TextStyle(
                          fontSize: 32,
                          color: Color.fromARGB(255, 16, 113, 210),
                        ),
                      ),
                    ],
                  ),

                  // 4. 보상
                  // const Text('--- 보상 ---'),
                  SizedBox(
                    height: screenHeight * 0.13,
                    width: screenWidth * 0.6,
                    child: DecoratedBox(
                      decoration: BoxDecoration(
                        color: const Color.fromARGB(255, 255, 255, 255)
                            .withOpacity(0.65),
                        borderRadius: BorderRadius.circular(5),
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          // 4-1. 능력치 포인트
                          Text(
                            '능력치 포인트 + ${levelUpInfo['levelUpReward']['statPoint']}',
                            style: const TextStyle(fontSize: 16),
                          ),

                          // 4-2. 상자
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              // 4-2-1. 일반 상자
                              if (levelUpInfo['levelUpReward']['itemReward']
                                          ['Normal Box'] !=
                                      null &&
                                  levelUpInfo['levelUpReward']['itemReward']
                                          ['Normal Box'] >
                                      0)
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Image.asset(
                                      'assets/items/itembox_normal.png',
                                      height: 70,
                                    ),
                                    Text(
                                      'X ${levelUpInfo['levelUpReward']['itemReward']['Normal Box']}',
                                      style: const TextStyle(fontSize: 16),
                                    ),
                                  ],
                                ),

                              const SizedBox(width: 15),

                              // 4-2-2. 고급 상자
                              if (levelUpInfo['levelUpReward']['itemReward']
                                          ['Luxury Box'] !=
                                      null &&
                                  levelUpInfo['levelUpReward']['itemReward']
                                          ['Luxury Box'] >
                                      0)
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Image.asset(
                                      'assets/items/itembox_special.png',
                                      height: 55,
                                    ),
                                    Text(
                                      'X ${levelUpInfo['levelUpReward']['itemReward']['Luxury Box']}',
                                      style: const TextStyle(fontSize: 16),
                                    ),
                                  ],
                                ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),

                  const SizedBox(
                    height: 10,
                  ),

                  // 5. 확인 버튼
                  TextButton(
                    onPressed: () => Navigator.of(context).pop(),
                    child: Image.asset(
                      'assets/buttons/yesiknow_button.png',
                      scale: 0.85,
                    ),
                  ),
                ],
              ),

              // 1. 레벨업 표시
              const Align(
                alignment: Alignment(0, -0.9),
                child: Text(
                  'LEVEL UP !',
                  style: TextStyle(fontSize: 40),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
