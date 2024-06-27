import 'package:flutter/material.dart';
import 'package:walkingpet/common/character_map.dart';

class CharacterBox extends StatefulWidget {
  final int characterId,
      characterGrade,
      userCharacterLevel,
      userCharacterUpgrade;
  final String characterName;
  final bool userCharacterStatus;
  final bool isSelected; // 상태 관리를 위해 이름 변경
  final Function(int) onSelected; // 선택 콜백

  const CharacterBox(
      {super.key,
      required this.characterId,
      required this.characterGrade,
      required this.userCharacterLevel,
      required this.userCharacterUpgrade,
      required this.characterName,
      required this.userCharacterStatus,
      required this.isSelected,
      required this.onSelected});

  @override
  State<CharacterBox> createState() => _CharacterBoxState();
}

class _CharacterBoxState extends State<CharacterBox> {
  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    // double screenHeight = MediaQuery.of(context).size.height;

    String animal = CharacterMap.idToAnimal[widget.characterId] ?? "buddy";

    return GestureDetector(
      onTap: widget.userCharacterStatus
          ? () => widget.onSelected(widget.characterId)
          : null,
      child: Container(
        width: screenWidth * 0.2,
        decoration: BoxDecoration(
          color: const Color(0xFF1D2A3D),
          border: Border.all(
            color: widget.isSelected
                ? const Color(0xFF5ACB01)
                : const Color(0xFF3C6093), // 조건부 색상
            width: widget.isSelected ? 3 : 2, // 조건부 두께
          ),
          borderRadius: BorderRadius.circular(10),
          // boxShadow: [
          //   BoxShadow(
          //     color: Colors.grey.withOpacity(0.5), // 그림자 색상
          //     spreadRadius: 2, // 그림자 범위
          //     blurRadius: 7, // 그림자 흐림 효과
          //     offset: const Offset(0, 3), // 그림자 위치 조정
          //   ),
          // ],
        ),
        child: Stack(
          children: [
            Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    // 1. 캐릭터 고유 레벨 (1-3성)
                    Row(
                      children: [
                        ...List.generate(
                          widget.characterGrade,
                          (_) => const Icon(
                            Icons.star,
                            color: Colors.yellow,
                            size: 10,
                          ),
                        ),
                      ],
                    ),

                    // 2. 사용자의 캐릭터 보유 개수
                    if (widget.userCharacterLevel != 0)
                      Row(
                        children: [
                          const Text(
                            '+',
                            style:
                                TextStyle(color: Colors.yellow, fontSize: 10),
                          ),
                          Text(
                            widget.userCharacterUpgrade.toString(),
                            style: const TextStyle(
                                color: Colors.yellow, fontSize: 10),
                          ),
                        ],
                      )
                  ],
                ),

                // 3. 캐릭터 이미지(id)
                Image.asset(
                  'assets/animals/$animal/${animal}_idle.gif',
                  height: 65,
                  // scale: 1,
                ),

                // 4. 사용자의 캐릭터 레벨
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text(
                      'Lv. ',
                      style: TextStyle(color: Colors.white, fontSize: 10),
                    ),
                    Text(
                      widget.userCharacterLevel.toString(),
                      style: const TextStyle(color: Colors.white, fontSize: 10),
                    ),
                  ],
                ),

                // 5. 캐릭터 고유 이름
                Text(
                  widget.characterName,
                  style: const TextStyle(color: Colors.white, fontSize: 11),
                )
              ],
            ),

            // 6. 사용자의 캐릭터 보유 여부 => 자물쇠로 표시
            if (!widget.userCharacterStatus)
              Center(
                child: Image.asset(
                  'assets/images/character_lock.png',
                  scale: 18,
                ),
              ),
          ],
        ),
      ),
    );
  }
}
