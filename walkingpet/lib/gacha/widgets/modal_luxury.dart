import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:walkingpet/services/gacha/open_box.dart';
import 'package:walkingpet/common/character_map.dart';

void showLuxuryModal(BuildContext context) async {
  double screenWidth = MediaQuery.of(context).size.width;
  double screenHeight = MediaQuery.of(context).size.height;
  // characterId 에 일치하는 gif 파일 받아오기 위한 변수
  String animal = '';
  try {
    var characterData = await openLuxuryBox();
    int characterId = characterData['characterId'];
    animal = CharacterMap.idToAnimal[characterId] ?? "Unknown";
    //캐릭터 아이디에 해당하는 동물 이름 가져오기 (gif 파일에 접근하기 위함)
    int grade = characterData['characterGrade']; // 희귀도 가져오기
    // grade 만큼의 별이 담긴 List 생성
    List<Widget> stars = List.generate(
        grade,
        (index) => Image.asset(
              'assets/items/one_star.png',
              width: screenWidth * 0.08,
            ));
    // 중복인지 확인
    bool isDuplicate = characterData['duplication'];
    // 중복인지에 따라 조건부 렌더링
    String statusText =
        isDuplicate ? "중복된 캐릭터입니다. \n 해당 캐릭터가 업그레이드 됩니다." : "NEW!";
    showDialog(
      context: context,
      barrierDismissible: true, //
      builder: (BuildContext context) {
        return Center(
          child: Align(
            alignment: const Alignment(0, -0.3),
            child: Material(
              color: Colors.transparent,
              // 클릭 이벤트 등의 Material 디자인 기능을 제공
              child: SizedBox(
                // alignment: Alignment.center,
                width: screenWidth,
                height: screenWidth,
                child: Stack(
                  children: [
                    Image.asset('assets/backgrounds/gacha_modal.png',
                        fit: BoxFit.cover),
                    Align(
                      alignment: const Alignment(0, 0.3),
                      child: Stack(
                        // 캐릭터 이미지를 입체로 띄우기 위함
                        children: [
                          Align(
                            alignment:
                                const Alignment(0, -0.3), // 세로축 값을 조정해 더 위로 이동
                            child: Image.asset(
                              'assets/animals/$animal/${animal}_idle.gif',
                              height: screenHeight * 0.2,
                            ),
                          ),
                          Column(
                            children: [
                              SizedBox(
                                  //상자 안 컨텐츠 위치 조정을 위한 sizedBox
                                  height: screenHeight * 0.05),
                              Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.center, //중앙 정렬
                                children: stars,
                              ),
                              Text(
                                '${characterData['characterName']}',
                                style: const TextStyle(fontSize: 30),
                              ),
                              SizedBox(
                                // 캐릭터 이름과 설명 사이 공간 확보
                                height: screenHeight * 0.15,
                              ),
                              SizedBox(
                                width: screenWidth * 0.6,
                                child: Text(
                                  statusText, // 중복인지 아닌지 알려주는 텍스트
                                  textAlign: TextAlign.center,
                                  style: const TextStyle(
                                    fontSize: 20,
                                  ),
                                ),
                              ),
                              GestureDetector(
                                  onTap: () {
                                    Navigator.of(context).pop();
                                  },
                                  child: SvgPicture.asset(
                                      'assets/buttons/button_confirm.svg'))
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  } catch (e) {
    // 가져오기 실패시
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('알림'),
          content: Text('$e',
              style: const TextStyle(fontSize: 20)), //exception 혹은 오류를 띄움
          actions: <Widget>[
            TextButton(
              child: const Text('닫기', style: TextStyle(fontSize: 20)),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }
}
