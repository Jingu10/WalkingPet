import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/exit_alert_modal.dart';
import 'package:walkingpet/gacha/widgets/gacha_box.dart';
import 'package:walkingpet/gacha/widgets/modal_luxury.dart';
import 'package:walkingpet/gacha/widgets/modal_normal.dart';
import 'package:walkingpet/main.dart';
import 'package:walkingpet/providers/gachabox_count_provider.dart';
import 'package:walkingpet/services/gacha/count_remain_box.dart';

class Gacha extends StatefulWidget {
  const Gacha({super.key});

  @override
  State<Gacha> createState() => _GachaState();
}

class _GachaState extends State<Gacha> {
  @override
  // 페이지가 처음 로드될 때 남은 상자 정보 받아옴
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<BoxCounterProvider>(context, listen: false)
          .initializeBoxCounts();
    });
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    // 박스 카운트 가져오기
    final boxCounter = Provider.of<BoxCounterProvider>(context);

    //새로 나온 캐릭터 홍보
    const String itemName = "브론즈 드래곤";
    const int rating = 3; //별의 개수
    List<Widget> stars = List.generate(
        rating,
        (index) => Image.asset(
              'assets/items/one_star.png',
              width: screenWidth * 0.08,
            ));
    return PopScope(
      canPop: false,
      onPopInvoked: (didPop) {
        handleExit(context);
      },
      child: Scaffold(
        // appBar: AppBar(
        //   title: const Text('뽑기 Page'),
        // ),
        body: Stack(
          children: <Widget>[
            //1. 배경이미지
            Positioned.fill(
              child: Image.asset('assets/backgrounds/morning.png',
                  fit: BoxFit.cover),
            ),
            Center(
              child: Container(
                width: screenWidth * 0.9,
                height: screenHeight * 0.85,
                //모서리 둥글게
                decoration: BoxDecoration(
                    color: const Color(0xfffff3dc).withOpacity(0.9),
                    borderRadius: BorderRadius.circular(20)),
                child: Padding(
                  padding: EdgeInsets.symmetric(vertical: screenHeight * 0.05),
                  child: Column(
                    //컨테이너 안에 컬럼 넣기
                    mainAxisAlignment: MainAxisAlignment.start, //컨테이너 상단부터 시작
                    children: <Widget>[
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Row(children: stars),
                          // ...List.generate(
                          //   rating,
                          //   (_) => Icon(
                          //     Icons.star,
                          //     color: const Color(0xffff004c),
                          //     size: screenWidth * 0.07,
                          //   ),
                          // ),
                          SizedBox(width: screenHeight * 0.02), //텍스트와 별 사이 여백
                          Text(
                            itemName,
                            style: TextStyle(
                              fontSize: screenWidth * 0.07,
                            ),
                          ),
                        ],
                      ),
                      Text(
                        "출시!",
                        style: TextStyle(
                          fontSize: screenWidth * 0.06,
                        ),
                      ),
                      SizedBox(
                        //츌시 <-> 스테이지 사이 공간
                        height: screenHeight * 0.1,
                      ),
                      // Stack(
                      //   alignment: Alignment.topCenter,
                      //   children: <Widget>[
                      //     Image.asset('assets/items/gacha_stage.png'),
                      //   ],
                      // ),
                      const Spacer(),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          GachaBox(
                            boxName: '일반 상자',
                            boxImage: 'assets/items/itembox_normal.png',
                            buttonImage:
                                'assets/buttons/button_gacha_normal.svg',
                            inactivedButtonImage:
                                'assets/buttons/normal_inactive.svg',
                            remainCount: boxCounter.normalBoxCount,
                            // onTap: () => showGachaModal(context),
                            onTap: () async {
                              boxCounter.openNormalBox(); // 상태 업데이트
                              showNormalModal(context); //모달창 표시
                            },
                          ),
                          const SizedBox(width: 40), //일반상자 ~ 고급상자 사이 공간
                          GachaBox(
                            boxName: '고급 상자',
                            boxImage: 'assets/items/itembox_special.png',
                            buttonImage:
                                'assets/buttons/button_gacha_special.svg',
                            inactivedButtonImage:
                                'assets/buttons/luxury_inactive.svg',
                            remainCount: boxCounter.luxuryBoxCount,
                            // onTap: () => showGachaModal(context),
                            onTap: () async {
                              boxCounter.openLuxuryBox();
                              showLuxuryModal(context);
                            },
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Positioned(
              top: screenWidth / 8, //화면 높이 기준 위치

              child: Image.asset(
                  'assets/animals/bronze_dragon/bronze_dragon_ready.gif',
                  width: 400,
                  fit: BoxFit.cover),
            ),
          ],
        ),
        bottomNavigationBar: const BottomNavBar(
          selectedIndex: 1,
        ),
      ),
    );
  }
}
