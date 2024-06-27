import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/common/character_map.dart';

class MemberScrollableList extends StatelessWidget {
  final List<dynamic> groupMemberInfo;
  //캐릭터 아이디 : 동물 종류

  const MemberScrollableList({
    super.key,
    required this.groupMemberInfo,
  });

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    return Container(
      width: screenWidth * 0.9,
      height: screenHeight * 0.35,
      decoration: BoxDecoration(
        color: const Color(0xffe9c6a8),
        borderRadius: BorderRadius.circular(20),
      ),
      child: ListView.builder(
        //스크롤 가능한 리스트
        itemCount: groupMemberInfo.length,
        itemBuilder: (context, index) {
          var member = groupMemberInfo[index];
          String animal =
              CharacterMap.idToAnimal[member['characterId']] ?? 'unknown';
          String imagePath = 'assets/animals/$animal/${animal}_idle.gif';
          return ListTile(
            title: Row(
              children: [
                Image.asset(
                  imagePath,
                  height: screenWidth * 0.2,
                ),
                Text(member['nickname'],
                    style: TextStyle(fontSize: screenWidth * 0.044)),
              ],
            ),
            trailing: Row(
              mainAxisSize: MainAxisSize.min, // 필요한 만큼의 최소 크기 사용
              children: [
                Text(
                  "${member['step']} ",
                  style: TextStyle(
                      fontSize: screenWidth * 0.08,
                      color: const Color.fromARGB(255, 243, 48, 5)),
                ),
                SizedBox(
                  width: screenWidth * 0.02,
                ),
                const Text("걸음",
                    style: TextStyle(
                      fontSize: 20,
                    )),
              ],
            ),
          );
        },
      ),
    );
  }
}
