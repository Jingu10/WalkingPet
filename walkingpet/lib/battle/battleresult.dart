import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/battle/resultfontstyle.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';
import 'package:walkingpet/main.dart';

class BattleResult extends StatelessWidget {
  final Map<String, dynamic> battleResult;
  final String animal;

  const BattleResult({
    super.key,
    required this.battleResult,
    required this.animal,
  });

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return PopScope(
      canPop: false,
      child: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage("assets/backgrounds/battleResult.png"),
            fit: BoxFit.cover, // 배경 이미지가 전체 화면을 채우도록 설정
          ),
        ),
        child: Scaffold(
          backgroundColor: Colors.transparent,
          body: Center(
            child: Column(
              children: [
                SizedBox(
                  height: screenHeight * 0.26,
                ),
                ResultFontStyle(
                  size: screenWidth * 0.11,
                  text: battleResult['battleResult'] ? "WIN" : "LOSE",
                  color:
                      battleResult['battleResult'] ? Colors.blue : Colors.red,
                ),
                SizedBox(
                  height: screenHeight * 0.1,
                ),
                SizedBox(
                  height: screenHeight * 0.2,
                  child: Image.asset(
                    battleResult['battleResult']
                        ? 'assets/animals/$animal/${animal}_attack.gif'
                        : 'assets/animals/$animal/${animal}_idle.gif',
                    scale: 1.2,
                  ),
                ),
                SizedBox(
                  height: screenHeight * 0.03,
                ),
                Stack(
                  children: [
                    Container(
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.6), // 투명한 흰색 배경 추가
                        borderRadius:
                            BorderRadius.circular(10.0), // 모서리를 둥글게 처리
                      ),
                      width: screenWidth * 0.6,
                      height: screenHeight * 0.1,
                      child: Center(
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              children: [
                            Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                SizedBox(
                                    width: screenWidth * 0.09,
                                    height: screenHeight * 0.06,
                                    child: Image.asset(
                                      'assets/images/character_expitem.png',
                                      fit: BoxFit.contain,
                                    )),
                                MainFontStyle(
                                    size: screenWidth * 0.05,
                                    text:
                                        "x${battleResult['rewardItem']['reward']['Exp Item']}"),
                              ],
                            ),
                            if (battleResult['rewardItem']['reward']
                                    ['Luxury Box'] >
                                0)
                              Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  SizedBox(
                                      width: screenWidth * 0.09,
                                      height: screenHeight * 0.06,
                                      child: Image.asset(
                                        'assets/items/itembox_special.png',
                                        fit: BoxFit.contain,
                                      )),
                                  MainFontStyle(
                                      size: screenWidth * 0.05,
                                      text:
                                          "x${battleResult['rewardItem']['reward']['Luxury Box']}"),
                                ],
                              ),
                            if (battleResult['rewardItem']['reward']
                                    ['Normal Box'] >
                                0)
                              Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  SizedBox(
                                      width: screenWidth * 0.09,
                                      height: screenHeight * 0.06,
                                      child: Image.asset(
                                        'assets/items/itembox_normal.png',
                                        fit: BoxFit.cover,
                                      )),
                                  MainFontStyle(
                                      size: screenWidth * 0.05,
                                      text:
                                          "x${battleResult['rewardItem']['reward']['Normal Box']}"),
                                ],
                              ),
                          ])),
                    ),
                    Transform.translate(
                      offset: Offset(screenWidth * 0.25, screenHeight * -0.026),
                      child:
                          MainFontStyle(size: screenWidth * 0.05, text: "보상"),
                    ),
                  ],
                ),
                SizedBox(
                  height: screenHeight * 0.015,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    MainFontStyle(size: screenWidth * 0.055, text: "점수:"),
                    MainFontStyle(
                        size: screenWidth * 0.12,
                        text: " ${battleResult['resultRating']} "),
                    ResultFontStyle(
                        size: screenWidth * 0.09,
                        text:
                            '(${battleResult['rewardRating'] > 0 ? "+" : ""}${battleResult['rewardRating']})',
                        color: battleResult['rewardRating'] > 0
                            ? Colors.blue
                            : Colors.red),
                  ],
                ),
                SizedBox(
                  height: screenHeight * 0.015,
                ),
                InkWell(
                  onTap: () {
                    Navigator.pushReplacementNamed(context, '/battleready');
                  },
                  child: Image.asset(
                    'assets/buttons/yesiknow_button.png',
                    scale: 0.8,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
