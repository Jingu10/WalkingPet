import 'package:flutter/material.dart';

class Rank extends StatelessWidget {
  final int ranking, score;
  final String nickname, rankingUnit;

  const Rank({
    super.key,
    required this.ranking,
    required this.nickname,
    required this.score,
    required this.rankingUnit,
  });

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Padding(
      padding: const EdgeInsets.symmetric(
        vertical: 5,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // 1. 순위
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
                            score > 0 ? ranking.toString() : '-',
                            style: const TextStyle(
                              fontSize: 22,
                            ),
                            textAlign: TextAlign.center,
                          ),
          ),

          // 2. 닉네임
          SizedBox(
            // width: 120,
            width: screenWidth * 0.32,
            child: Text(
              nickname,
              style: const TextStyle(
                fontSize: 16,
              ),
              textAlign: TextAlign.center,
            ),
          ),

          // 3. 걸음 수
          SizedBox(
            // width: 130,
            width: screenWidth * 0.34,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  score >= 0 ? score.toString() : '-',
                  style: const TextStyle(
                    fontSize: 22,
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
    );
  }
}
