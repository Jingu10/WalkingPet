import 'package:flutter/material.dart';
import 'package:nes_ui/nes_ui.dart';
import 'package:walkingpet/main.dart';
import 'package:walkingpet/ranking/personal.dart';
import 'package:walkingpet/ranking/group.dart';
import 'package:walkingpet/ranking/battle.dart';

class Ranking extends StatelessWidget {
  const Ranking({super.key});

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
              'assets/backgrounds/ranking.png',
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

          // // 2. 투명 레이어 (특정 영역만)
          // Positioned(
          //   left: screenWidth * 0.04,
          //   top: screenHeight * 0.16,
          //   child: Container(
          //     width: screenWidth * 0.92,
          //     height: screenHeight * 0.82,
          //     color: const Color.fromARGB(255, 255, 255, 255).withOpacity(0.8),
          //   ),
          // ),

          // 2. 투명 레이어 (Top10 영역)
          // Positioned(
          //   left: screenWidth * 0.07,
          //   top: screenHeight * 0.55,
          //   child: Container(
          //     width: screenWidth * 0.85,
          //     height: screenHeight * 0.35,
          //     color: const Color.fromARGB(255, 255, 255, 255).withOpacity(0.8),
          //   ),
          // ),

          // 3. 내용
          // 전체적으로 padding 지정
          Padding(
            padding: const EdgeInsets.all(15),
            child: Column(
              children: [
                // 3-1. 랭킹 & X버튼
                Padding(
                  padding: const EdgeInsets.only(bottom: 5),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        '랭킹',
                        style: TextStyle(fontSize: screenWidth * 0.08),
                      ),
                      TextButton(
                        onPressed: () async {
                          Navigator.pop(context);
                          // Navigator.pushReplacementNamed(
                          //     context, '/home'); // 현재 경로를 '/home'으로 교체
                        },
                        style: ButtonStyle(
                          padding: MaterialStateProperty.all(
                              EdgeInsets.zero), // 버튼의 내부 패딩 제거
                        ),
                        child: Text(
                          'X',
                          style: TextStyle(
                              fontSize: screenWidth * 0.08,
                              color: Colors.black),
                        ),
                      )
                    ],
                  ),
                ),

                // 3-2. Tab : 개인(personal) / 그룹(Group) / 배틀(Battle)
                const Expanded(
                  child: NesTabView(
                    tabs: [
                      NesTabItem(
                        child: PersonalRanking(),
                        label: '개인랭킹',
                      ),
                      NesTabItem(
                        child: BattleRanking(),
                        label: '배틀랭킹',
                      ),
                      NesTabItem(
                        child: GroupRanking(),
                        label: '그룹랭킹',
                      ),
                    ],
                  ),
                ),
              ],
            ),
          )
        ],
      ),
    );
  }
}
