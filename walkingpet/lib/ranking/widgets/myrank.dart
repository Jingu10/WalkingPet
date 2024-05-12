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
            height: 90,
            width: 100,
          ),

          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // 2. 순위
                  SizedBox(
                    width: 60,
                    child: Text(
                      '$ranking위',
                      style: const TextStyle(
                        fontSize: 22,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),

                  // 3. 닉네임
                  SizedBox(
                    width: 130,
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
                width: 160,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Text(
                      score.toString(),
                      style: const TextStyle(
                        fontSize: 24,
                        color: Color.fromARGB(255, 241, 86, 9),
                      ),
                    ),
                    const SizedBox(width: 5),
                    Text(
                      rankingUnit,
                      style: const TextStyle(
                        fontSize: 14,
                      ),
                    ),
                    const SizedBox(width: 5),
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
