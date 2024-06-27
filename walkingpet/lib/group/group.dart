import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:nes_ui/nes_ui.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/common/exit_alert_modal.dart';
import 'package:walkingpet/group/widgets/my_group_list.dart';
import 'package:walkingpet/group/widgets/search_group_list.dart';
import 'package:walkingpet/services/group/get_group_info.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';

class Group extends StatefulWidget {
  const Group({super.key});

  @override
  State<Group> createState() => _GroupState();
}

class _GroupState extends State<Group> {
  // 그룹 데이터 담을 빈 배열
  List<Map<String, dynamic>> myGroups = [];
  List<Map<String, dynamic>> searchGroups = [];
  // 로딩중인가?
  bool isLoding = true;

  @override
  void initState() {
    super.initState();
    _fetchMyGroups();
    _fetchAllGroups();
  }

  //내 그룹 정보 가져오기
  Future<void> _fetchMyGroups() async {
    try {
      final groups = await getMyGroup();
      setState(() {
        myGroups = groups;
        isLoding = false;
      });
    } catch (e) {
      setState(() {
        isLoding = false;
        // 에러 처리
        print('에러: $e');
      });
    }
  }

  //전체 그룹 정보 가져오기
  Future<void> _fetchAllGroups() async {
    try {
      final allGroups = await getAllGroup(); //search(all)group 페이지로 인자 넘겨줘야 함
      setState(() {
        searchGroups = allGroups;
        print("api요청으로 받아온 groups : $searchGroups"); //여기서는 받아진다.
      });
    } catch (e) {
      print("에러");
    }
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    return PopScope(
      canPop: false,
      onPopInvoked: (didPop) {
        handleExit(context);
      },
      child: Scaffold(
        resizeToAvoidBottomInset: false,
        body: Stack(
          children: <Widget>[
            Positioned.fill(
              child: Image.asset(
                'assets/backgrounds/group.png',
                fit: BoxFit.cover,
              ),
            ),
            Positioned.fill(
              child: Container(
                color: Colors.white.withOpacity(0.65),
              ),
            ),
            Center(
              child: Container(
                width: screenWidth * 0.9,
                height: screenHeight * 0.7,
                decoration: BoxDecoration(
                    color: const Color.fromARGB(255, 255, 222, 173)
                        .withOpacity(0.75),
                    borderRadius: BorderRadius.circular(20)),
                // child: const Column(
                //   children: [
                //     GroupCard(groupName: "야호"),
                //   ],
                // ), //컨테이너 안 child
              ),
            ),
            Positioned(
              top: screenHeight * 0.09,
              left: screenWidth * 0.05,
              child: Material(
                color: Colors.transparent,
                child: DefaultTextStyle(
                  style: const TextStyle(
                    fontSize: 25,
                    color: Colors.black,
                    fontFamily: "DungGeunMo",
                  ),
                  child: SizedBox(
                    width: screenWidth * 0.9,
                    height: screenHeight * 0.73,
                    child: NesTabView(
                      tabs: [
                        NesTabItem(
                          child: MyGroup(myGroups: myGroups), //인자 넘겨주기
                          label: "내 그룹",
                        ),
                        NesTabItem(
                          child: SearchGroup(
                            incommingAllGroups: searchGroups,
                          ),
                          label: "그룹 탐색",
                        ),
                       
                      ],
                    ),
                  ),
                ),
              ),
            )
          ],
        ),
        bottomNavigationBar: const BottomNavBar(
          selectedIndex: 4,
        ),
      ),
    );
  }
}
