import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:walkingpet/common/bottom_nav_bar.dart';
import 'package:walkingpet/group/group.dart';
import 'package:walkingpet/group/widgets/member_scrollable_list.dart';
import 'package:walkingpet/services/group/get_group_detail.dart';
import 'package:walkingpet/services/group/leave_group.dart';
import 'package:walkingpet/services/group/get_member_info.dart';
import 'package:walkingpet/services/group/join_group.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart'; //메인폰트스타일
import 'package:pixelarticons/pixelarticons.dart';

class GroupDetail extends StatefulWidget {
  final int groupId; //팀 이름

  const GroupDetail({
    super.key,
    required this.groupId,
  });

  @override
  State<GroupDetail> createState() => _GroupDetailState();
}

class _GroupDetailState extends State<GroupDetail> {
  //nullable 타입의 데이터 선언
  Map<String, dynamic>? groupData;
  List<dynamic>? memberData;
  //null인지 상태 체크해야 함
  bool isDetailLoading = true;
  bool isMemberLoading = true;
  @override
  void initState() {
    super.initState();
    _fetchGroupDetail();
    // getGroupMember(widget.groupId);
    _fetchMembers();
  }

  // 그룹 디테일 정보 가져오기
  Future<void> _fetchGroupDetail() async {
    try {
      final detailData = await getGroupDetail(widget.groupId);
      setState(() {
        groupData = detailData; // 데이터를 가져와 상태 변수에 저장
        isDetailLoading = false; //로딩 완료
      });
    } catch (e) {
      setState(() {
        isDetailLoading = false; // 에러 발생 시 로딩 중지
      });
      print("데이터 가져오기 에러 : $e");
    }
  }

  // 그룹 멤버 정보 가져오기
  Future<void> _fetchMembers() async {
    try {
      final data = await getGroupMember(widget.groupId);
      setState(() {
        memberData = data;
        isMemberLoading = false;
      });
    } catch (e) {
      setState(() {
        isMemberLoading = false; // 에러 발생 시 로딩 중지
      });
      print("멤버 데이터 가져오기 에러 : $e");
    }
  }

  // 회원 탈퇴 성공/실패 여부 유저에게 전달
  Future<void> _leaveGroupAndNavigate() async {
    try {
      await leaveGroup(widget.groupId);
      Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => const Group(),
          settings: const RouteSettings(name: '/groupdetail'),
        ),
      );
    } catch (e) {
      // 오류 처리
      print("에러발생 : $e");
    }
  }

  //회원가입 로직
  Future<void> joinGroupAndNavigate() async {
    try {
      await joinGroup(widget.groupId);
      Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => const Group(),
          settings: const RouteSettings(name: '/groupdetail'),
        ),
      );
    } catch (e) {
      // 오류 처리
      print("에러발생 : $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    // 로딩 중이면 스피너
    if (isDetailLoading || isMemberLoading) {
      //한쪽이라도 데이터 덜 가져왔으면 로딩 표시
      return const Scaffold(
          body: Center(
        child: CircularProgressIndicator(),
      ));
    }

    if (groupData == null) {
      return const Scaffold(
          body: Center(
        child: Text("그룹 정보를 가져올 수 없습니다"),
      ));
    }

    int goalStep = 10000; // 목표 걸음수

    String groupName = groupData!['teamName'] ?? '그룹 이름 없음';
    String description = groupData!['explain'] ?? '설명 없음';
    int groupTotalStep = groupData!['teamTotalSteps'] ?? 0;
    int groupRank = groupData!['teamPoint'] ?? 0;
    bool isJoined = groupData!['join'] ?? false;

    return Scaffold(
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
              color: Colors.white.withOpacity(0.6),
            ),
          ),
          Center(
            child: Container(
              width: screenWidth * 0.9,
              height: screenHeight * 0.8,
              decoration: BoxDecoration(
                  color: const Color(0xfffff3dc).withOpacity(0.9),
                  borderRadius: BorderRadius.circular(20)),
              child: Padding(
                padding:
                    const EdgeInsets.symmetric(vertical: 20, horizontal: 10),
                child: Column(
                  children: <Widget>[
                    MainFontStyle(
                        size: screenHeight * 0.04,
                        text: groupName,
                        color: const Color.fromARGB(255, 232, 249, 255)), //제목
                    const SizedBox(height: 10),
                    Text(
                      "그룹 순위 : $groupRank",
                      style: const TextStyle(fontSize: 20),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 20),
                      child: Container(
                        width: screenWidth * 0.8,
                        height: screenHeight * 0.1,
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.7),
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Column(
                          //뒤에 배경 깔기위해... container로 묶으려고
                          children: [
                            const Text(
                              "함께 10000보 걷기",
                              style: TextStyle(fontSize: 25),
                            ),
                            Stack(
                              alignment: Alignment.center,
                              // 프로그래스 바 위에 걸음 수를 띄우기 위해
                              children: <Widget>[
                                SizedBox(
                                  // 선형 프로그래스 바의 크기 조절을 위해
                                  width: screenWidth * 0.6,
                                  child: LinearProgressIndicator(
                                    // 그룹 목표
                                    value: groupTotalStep / goalStep,
                                    minHeight: 20,
                                    color: const Color(0xff7DF39D),
                                    backgroundColor: const Color(0xff43695c),
                                  ),
                                ),
                                Text(
                                  "$groupTotalStep / $goalStep",
                                  style: const TextStyle(
                                    color: Colors.white,
                                    fontSize: 25,
                                  ),
                                )
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                    const Text(
                      "그룹원 목록",
                      style: TextStyle(
                        fontSize: 25,
                      ),
                    ),
                    SizedBox(
                      height: screenHeight * 0.02,
                    ),
                    MemberScrollableList(groupMemberInfo: memberData ?? []),
                    SizedBox(
                      height: screenHeight * 0.04,
                    ),
                    //나가기 버튼 or 가입하기 버튼
                    Container(
                      child: isJoined
                          ? GestureDetector(
                              onTap: () => _leaveGroupAndNavigate(),
                              child: SvgPicture.asset(
                                'assets/buttons/leave_group_button.svg',
                                height: screenWidth * 0.1,
                              ),
                            )
                          : GestureDetector(
                              onTap: () => joinGroupAndNavigate(),
                              child: SvgPicture.asset(
                                'assets/buttons/join_group_button.svg',
                                height: screenWidth * 0.1,
                              ),
                            ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          Positioned(
            left: screenWidth * 0.5 - (screenWidth * 0.2 / 2),
            top: screenHeight * 0.36,
            child: Image.asset(
              'assets/images/three_star.png',
              width: screenWidth * 0.2,
            ),
          ),
          Positioned(
            top: screenHeight * 0.07,
            right: screenWidth * 0.05,
            child: TextButton(
              onPressed: () async {
                Navigator.pop(context);
              },
              style: ButtonStyle(
                padding: MaterialStateProperty.all(EdgeInsets.zero),
              ),
              child: const Icon(Pixel.close),
            ),
          ),
        ],
      ),
      bottomNavigationBar: const BottomNavBar(
        selectedIndex: 4,
      ),
    );
  }
}
