import 'package:flutter/material.dart';
import 'package:walkingpet/common/character_map.dart';

class Top1to3 extends StatelessWidget {
  final int ranking, characterId;
  final String nickname;

  const Top1to3({
    super.key,
    required this.ranking,
    required this.characterId,
    required this.nickname,
  });

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    String animal = CharacterMap.idToAnimal[characterId] ?? "Unknown";

    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: 5,
      ),
      child: Column(
        children: [
          // 1. 순위 (1~3위)
          // 1,2,3위는 해당하는 메달 이미지를, 나머지는 텍스트만 나오도록 수정
          SizedBox(
            // width: 36,
            width: screenWidth * 0.1,
            child: ranking == 1
                ? Image.asset(
                    'assets/icons/gold_medal.png',
                    width: screenWidth * 0.1,
                    height: screenHeight * 0.05,
                    fit: BoxFit.contain,
                  )
                : ranking == 2
                    ? Image.asset(
                        'assets/icons/silver_medal.png',
                        width: screenWidth * 0.1,
                        height: screenHeight * 0.05,
                        fit: BoxFit.contain,
                      )
                    : ranking == 3
                        ? Image.asset(
                            'assets/icons/bronze_medal.png',
                            width: screenWidth * 0.1,
                            height: screenHeight * 0.05,
                            fit: BoxFit.contain,
                          )
                        : Text(
                            ranking.toString(),
                            style: const TextStyle(
                              fontSize: 22,
                            ),
                            textAlign: TextAlign.center,
                          ),
          ),

          // 2. 캐릭터 이미지
          Image.asset(
            'assets/animals/$animal/${animal}_idle.gif',
            // height: 90,
            // width: 90,
            height: screenHeight * 0.11,
            width: screenWidth * 0.23,
          ),

          // 걸음수 생략
          // Text(
          //   step,
          //   style: const TextStyle(
          //     fontSize: 20,
          //   ),
          // ),

          // 3. 닉네임
          Text(
            nickname,
            style: const TextStyle(
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}
