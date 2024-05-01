import 'package:flutter/material.dart';

class Rank extends StatelessWidget {
  final int ranking, step;
  final String nickname;

  Rank.fromJson(Map<String, dynamic> json, {super.key})
      : ranking = json['ranking'],
        step = json['step'],
        nickname = json['nickname'];

  // const Rank({
  //   super.key,
  //   required this.ranking,
  //   required this.nickname,
  //   required this.step,
  // });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(
        vertical: 5,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // 1. 순위
          SizedBox(
            width: 45,
            child: Text(
              ranking.toString(),
              style: const TextStyle(
                fontSize: 23,
              ),
              textAlign: TextAlign.center,
            ),
          ),

          // 2. 닉네임
          SizedBox(
            width: 130,
            child: Text(
              nickname,
              style: const TextStyle(
                fontSize: 17,
              ),
              textAlign: TextAlign.center,
            ),
          ),

          // 3. 걸음 수
          SizedBox(
            width: 150,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  step.toString(),
                  style: const TextStyle(
                    fontSize: 23,
                    color: Color.fromARGB(255, 241, 86, 9),
                  ),
                ),
                const SizedBox(width: 5),
                const Text(
                  '걸음',
                  style: TextStyle(
                    fontSize: 15,
                  ),
                ),
                const SizedBox(width: 10),
              ],
            ),
          ),
        ],
      ),
    );
  }
}




//  ranking, characterId, level, nickname, step