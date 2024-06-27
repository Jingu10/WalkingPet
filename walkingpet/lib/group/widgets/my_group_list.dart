import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:walkingpet/group/widgets/group_card.dart';
import 'package:walkingpet/group/widgets/create_group.dart';

class MyGroup extends StatefulWidget {
  final List<Map<String, dynamic>> myGroups;

  // 생성자
  const MyGroup({
    super.key,
    required this.myGroups,
  });

  @override
  State<MyGroup> createState() => _MyGroupState();
}

class _MyGroupState extends State<MyGroup> {
  @override
  Widget build(BuildContext context) {
    //스크린 크기 받아오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    //그룹 수 n/3형식으로 표시
    String groupCountDisplay = "${widget.myGroups.length}/3";

    final List<GroupCard> groupCards = widget.myGroups.map((group) {
      return GroupCard(
        groupId: group['teamId'],
        groupName: group['teamName'],
        description: group['teamExplain'],
        numOfMember: group['userCount'],
        isProtected: group['hasPassword'],
      );
    }).toList();

    return SizedBox(
      height: screenHeight,
      child: SingleChildScrollView(
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('  $groupCountDisplay'),
                Align(
                  alignment: Alignment.centerRight,
                  child: Padding(
                    padding: EdgeInsets.all(screenHeight * 0.015),
                    child: InkWell(
                      onTap: () {
                        Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => const CreateGroup(),
                              settings:
                                  const RouteSettings(name: '/creategroup'),
                            ));
                      },
                      child: SvgPicture.asset(
                        'assets/buttons/new_create_group.svg',
                        width: screenWidth * 0.3,
                      ),
                    ),
                  ),
                ),
              ],
            ),
            widget.myGroups.isEmpty
                ? Padding(
                    padding:
                        EdgeInsets.symmetric(vertical: screenHeight * 0.05),
                    child: const Text(
                      "아직 가입한 그룹이 없습니다",
                      style: TextStyle(fontSize: 24),
                    ),
                  )
                : Column(
                    children: [
                      Column(children: groupCards),
                      Padding(
                        padding: EdgeInsets.all(screenWidth * 0.03),
                        child: Text("그룹은 최대 3개까지 가입할 수 있습니다.",
                            style: TextStyle(fontSize: screenWidth * 0.03)),
                      ),
                      const SizedBox(height: 20)
                    ],
                  ),
          ],
        ),
      ),
    );
  }
}
