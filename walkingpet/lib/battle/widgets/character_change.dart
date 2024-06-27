import 'package:flutter/material.dart';
import 'package:walkingpet/character/widgets/character_box.dart';
import 'package:walkingpet/services/character/characterchange.dart';

class BattleCharacterChange extends StatefulWidget {
  const BattleCharacterChange({super.key});

  @override
  State<BattleCharacterChange> createState() => _CharacterChangeState();
}

class _CharacterChangeState extends State<BattleCharacterChange> {
  // 필요한 변수 만들기
  List characterInfoData = [];
  bool isLoading = true;
  int? selectCharacterId; // 유저가 현재 선택한 캐릭터 ID

  @override
  void initState() {
    super.initState();
    initInfo();
  }

  // API 요청으로 데이터 불러오기
  Future<void> initInfo() async {
    try {
      var responseInfo = await getCharacterChange();
      setState(() {
        characterInfoData = responseInfo['data']['characters'];
        selectCharacterId = responseInfo['data']['selectCharacterId'];
        isLoading = false;
      });
    } catch (e) {
      print('캐릭터 변경 모달 에러 발생! ${e.toString()}');
      isLoading = false;
    }
  }

  // CharacterBox 클릭 시, selectCharacterId 업데이트 함수
  void _handleCharacterSelected(int characterId) {
    setState(() {
      selectCharacterId = characterId; // 선택된 캐릭터 ID를 업데이트
    });
  }

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Stack(
      children: [
        // 1. 배경 (바깥)
        Positioned.fill(
          child: Container(
            decoration: BoxDecoration(
              color: const Color(0xFF00A8FF),
              borderRadius: BorderRadius.circular(5),
            ),
          ),
        ),

        // 2. 캐릭터 변경 제목
        const Positioned(
          top: 10,
          left: 0,
          right: 0,
          child: Center(
            child: Text(
              '캐릭터변경',
              style: TextStyle(
                fontSize: 30,
                color: Colors.white,
              ),
            ),
          ),
        ),

        // 3. 배경 (안쪽)
        Positioned(
          bottom: 15,
          left: 10,
          right: 10,
          child: Container(
            width: screenWidth * 0.7,
            height: screenHeight * 0.5,
            decoration: BoxDecoration(
              color: const Color(0xFF283C57),
              borderRadius: BorderRadius.circular(5),
            ),
          ),
        ),

        // 4. 캐릭터 선택 그리드
        if (isLoading)
          const Center(
              child: Text(
            '캐릭터 불러오는 중..',
            style: TextStyle(
              color: Colors.white,
            ),
          ))
        else
          Positioned(
            top: screenHeight * 0.1,
            left: screenWidth * 0.05,
            right: screenWidth * 0.05,
            bottom: screenHeight * 0.1,
            child: GridView.builder(
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3, // 한 행에 보여줄 개수
                childAspectRatio: 1 / 1.5, // 아이템의 가로 세로 비율
                mainAxisSpacing: 5, // 수평 간격
                crossAxisSpacing: 5, // 수직 간격
              ),
              itemCount: characterInfoData.length,
              itemBuilder: (BuildContext context, int index) {
                return CharacterBox(
                  characterId: characterInfoData[index]['characterId'] ?? 1,
                  characterName:
                      characterInfoData[index]['characterName'] ?? '이름로딩중',
                  characterGrade:
                      characterInfoData[index]['characterGrade'] ?? 0,
                  //
                  userCharacterLevel:
                      characterInfoData[index]['userCharacterLevel'] ?? 1,
                  userCharacterUpgrade:
                      characterInfoData[index]['userCharacterUpgrade'] ?? 0,
                  userCharacterStatus:
                      characterInfoData[index]['userCharacterStatus'] ?? false,
                  //
                  isSelected: selectCharacterId ==
                      characterInfoData[index]['characterId'],
                  onSelected: _handleCharacterSelected,
                );
              },
            ),
          ),

        // 5. 변경 버튼
        if (!isLoading)
          Positioned(
            bottom: 16,
            left: screenWidth * 0.1,
            right: screenWidth * 0.1,
            child: TextButton(
              onPressed: () async {
                await postCharacterChange(context, selectCharacterId as int);
                Navigator.pushNamed(context, '/battleready');
              },
              child: Stack(
                alignment: Alignment.center,
                children: [
                  Image.asset('assets/buttons/green_button.png'),
                  const Text(
                    '변경하기',
                    style: TextStyle(
                      fontSize: 22,
                      color: Colors.black,
                    ),
                  ),
                ],
              ),
            ),
          ),
      ],
    );
  }
}
