import 'dart:math' as math;
import 'dart:async';

import 'package:flutter/material.dart';
import 'package:walkingpet/battle/battleresult.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';
import 'package:percent_indicator/percent_indicator.dart';

class BattleSide extends StatefulWidget {
  final int health;
  final int power;
  final int defense;
  final int rating;
  final int characterId; // 이걸로 캐릭터의 종류를 구별
  final int userCharacterLevel;
  final String nickname;
  final bool isLeft;
  final List<dynamic> attackDamage;
  final List<dynamic> receivedDamage;
  final List<dynamic> userHealth;
  final List<dynamic> loseDamage;
  final Map<String, dynamic> battleResult;

  const BattleSide(
      {super.key,
      required this.health,
      required this.power,
      required this.defense,
      required this.rating,
      required this.characterId,
      required this.userCharacterLevel,
      required this.nickname,
      required this.isLeft,
      required this.attackDamage,
      required this.receivedDamage,
      required this.userHealth,
      required this.loseDamage,
      required this.battleResult});

  @override
  State<BattleSide> createState() => _BattleSideState();
}

class _BattleSideState extends State<BattleSide> {
  String animal = "cow";

  double _currentPercent = 0.0;

  int _sequenceIndex = 0;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    animal = CharacterMap.idToAnimal[widget.characterId] ?? "Unknown";
    final List<dynamic> damageSequence = widget.loseDamage;

    // 여기에 지연 후 실행할 코드를 작성
    _currentPercent = damageSequence[_sequenceIndex].clamp(0.0, 1.0);
    _timer = Timer.periodic(const Duration(milliseconds: 2000), (timer) {
      if (_sequenceIndex < damageSequence.length) {
        setState(() {
          // 각 시퀀스 값을 현재 퍼센트에 추가하고, 결과가 1을 초과하지 않도록 합니다.
          _currentPercent = damageSequence[_sequenceIndex].clamp(0.0, 1.0);

          if (_sequenceIndex + 1 < damageSequence.length) {
            Timer(const Duration(milliseconds: 500), () {
              _sequenceIndex++;
            });
          } else {
            _timer?.cancel();
            Timer(const Duration(seconds: 1), () {
              if (widget.isLeft) {
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) => BattleResult(
                      battleResult: widget.battleResult,
                      animal: animal,
                    ),
                  ),
                );
              }
            });
          }
        });
      } else {}
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Column(
      children: [
        SizedBox(
          height: screenHeight * 0.37,
        ),

        SizedBox(
          height: screenHeight * 0.15,
          child: Column(
            children: [
              Row(
                children: [
                  Transform.translate(
                    offset: Offset(0, screenHeight * 0.004),
                    child:
                        MainFontStyle(size: screenWidth * 0.04, text: "점수: "),
                  ),
                  MainFontStyle(
                    size: screenWidth * 0.08,
                    text: widget.rating.toString(),
                  ),
                ],
              ),
              Stack(
                alignment: Alignment.center,
                children: [
                  Transform.translate(
                    offset: widget.isLeft
                        ? Offset(screenWidth * 0.02, 0)
                        : Offset(screenWidth * -0.02, 0),
                    child: LinearPercentIndicator(
                      backgroundColor: Colors.grey,
                      progressColor: Colors.green,
                      width: screenWidth * 0.352,
                      percent: 1,
                      animation: true,
                      lineHeight: screenHeight * 0.0095,
                      barRadius: const Radius.circular(2),
                    ),
                  ),
                  Transform(
                    alignment: Alignment.center,
                    transform: widget.isLeft
                        ? Matrix4.rotationY(math.pi)
                        : Matrix4.identity(),
                    child: Transform.translate(
                      offset: widget.isLeft
                          ? Offset(screenWidth * -0.02, 0)
                          : Offset(screenWidth * -0.02, 0),
                      child: LinearPercentIndicator(
                        backgroundColor: Colors.transparent,
                        width: screenWidth * 0.352,
                        percent: _currentPercent,
                        animation: true,
                        lineHeight: screenHeight * 0.0095,
                        barRadius: const Radius.circular(2),
                        animateFromLastPercent: true,
                        animationDuration: 500,
                      ),
                    ),
                  ),
                  Image.asset(
                    widget.isLeft
                        ? 'assets/images/health_bar_left.png'
                        : 'assets/images/health_bar_right.png',
                    scale: 5,
                  ),
                ],
              ),
            ],
          ),
        ),

        SizedBox(
          height: screenHeight * 0.025,
        ),

        SizedBox(
          height: screenHeight * 0.22,
          width: screenWidth * 0.44,
          child: Stack(
            children: [
              if (widget.attackDamage[_sequenceIndex] < 0)
                Transform.translate(
                  offset: Offset(screenWidth * -0.01, screenHeight * -0.03),
                  child: Container(
                    alignment: Alignment.center,
                    child: MainFontStyle(
                      size: screenWidth * 0.05,
                      text: "-${widget.receivedDamage[_sequenceIndex]}",
                      color: Colors.red,
                    ),
                  ),
                ),
              Transform(
                alignment: Alignment.center,
                transform: widget.isLeft
                    ? Matrix4.identity()
                    : Matrix4.rotationY(math.pi),
                // 여기서 올바르게 translate 적용
                child: Transform.translate(
                  offset: Offset(screenWidth * 0.15,
                      animal == "duck" || animal == "squirrel" ? 10 : 0),
                  child: SizedBox(
                    height: animal == "duck" || animal == "squirrel"
                        ? screenHeight * 0.3
                        : screenHeight * 0.2,
                    child: Image.asset(
                      widget.attackDamage[_sequenceIndex] >= 0
                          ? 'assets/animals/$animal/${animal}_attack.gif'
                          : 'assets/animals/$animal/${animal}_hurt.gif',
                      fit: animal == "duck" || animal == "squirrel"
                          ? BoxFit.scaleDown
                          : BoxFit.fitHeight,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),

        // 캐릭터 정보 박스
        Container(
          width: screenWidth * 0.42,
          height: screenHeight * 0.22,
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.6), // 투명한 흰색 배경 추가
            borderRadius: BorderRadius.circular(10.0), // 모서리를 둥글게 처리
          ),
          padding: EdgeInsets.fromLTRB(
            screenWidth * 0.05,
            screenHeight * 0.01,
            screenWidth * 0.05,
            screenHeight * 0.01,
          ),
          child: Center(
            child: Column(
              children: [
                SizedBox(
                  height: screenHeight * 0.016,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    MainFontStyle(
                        size: screenWidth * 0.04,
                        text: "Lv.${widget.userCharacterLevel}"),
                    SizedBox(
                      width: screenWidth * 0.028,
                    ),
                    MainFontStyle(
                        size: screenWidth * 0.03, text: widget.nickname),
                  ],
                ),
                SizedBox(
                  height: screenHeight * 0.015,
                ),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Image.asset(
                      'assets/icons/icon_health.png', // assets 폴더에 위치한 SVG 파일
                      height: screenHeight * 0.025, // 높이 지정
                      width: screenWidth * 0.06,
                    ),
                    SizedBox(
                      width: screenWidth * 0.04,
                    ),
                    MainFontStyle(
                      size: screenWidth * 0.047,
                      text: (widget.health).toString(),
                    ),
                  ],
                ),
                SizedBox(
                  height: screenHeight * 0.007,
                ),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Image.asset(
                      'assets/icons/icon_power.png', // assets 폴더에 위치한 SVG 파일
                      height: screenHeight * 0.025, // 높이 지정
                      width: screenWidth * 0.06,
                    ),
                    SizedBox(
                      width: screenWidth * 0.04,
                    ),
                    MainFontStyle(
                        size: screenWidth * 0.047,
                        text: (widget.power).toString()),
                  ],
                ),
                SizedBox(
                  height: screenHeight * 0.007,
                ),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Image.asset(
                      'assets/icons/icon_depense.png', // assets 폴더에 위치한 SVG 파일
                      height: screenHeight * 0.025, // 높이 지정
                      width: screenWidth * 0.06,
                    ),
                    SizedBox(
                      width: screenWidth * 0.04,
                    ),
                    MainFontStyle(
                        size: screenWidth * 0.047,
                        text: (widget.defense).toString()),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
