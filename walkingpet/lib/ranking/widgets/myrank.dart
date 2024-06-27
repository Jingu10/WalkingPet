import 'package:flutter/material.dart';
import 'package:walkingpet/common/character_map.dart';

class MyRank extends StatelessWidget {
  final int ranking, score, characterId;
  final String nickname, rankingUnit;

  const MyRank(
      {super.key,
      required this.ranking,
      required this.score,
      required this.nickname,
      required this.characterId,
      required this.rankingUnit});

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    String animal = CharacterMap.idToAnimal[characterId] ?? "Unknown";

    return Padding(
      padding: const EdgeInsets.symmetric(
        vertical: 5,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          // 1. 캐릭터 이미지
          Image.asset(
            'assets/animals/$animal/${animal}_idle.gif',
            // height: 90,
            // width: 100,
            height: screenHeight * 0.11,
            width: screenWidth * 0.25,
          ),

          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // 2. 순위
                  SizedBox(
                    // width: 60,
                    width: screenWidth * 0.15,
                    child: Text(
                      score > 0 ? '$ranking위' : '-',
                      style: const TextStyle(
                        fontSize: 22,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),

                  // 3. 닉네임
                  SizedBox(
                    // width: 130,
                    width: screenWidth * 0.35,
                    child: Text(
                      nickname,
                      style: const TextStyle(
                        fontSize: 18,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ],
              ),

              // 4. 걸음 수
              SizedBox(
                // width: 160,
                width: screenWidth * 0.43,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Text(
                      score >= 0 ? score.toString() : '-',
                      style: const TextStyle(
                        fontSize: 24,
                        color: Color.fromARGB(255, 241, 86, 9),
                      ),
                    ),
                    SizedBox(width: screenWidth * 0.01),
                    Text(
                      rankingUnit,
                      style: const TextStyle(
                        fontSize: 14,
                      ),
                    ),
                    SizedBox(width: screenWidth * 0.01),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
