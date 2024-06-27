import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:walkingpet/group/widgets/search_group_card.dart';
import 'package:walkingpet/group/widgets/create_group.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:walkingpet/services/group/get_search_result.dart';

class SearchGroup extends StatefulWidget {
  final List<Map<String, dynamic>> incommingAllGroups;

  const SearchGroup({
    super.key,
    required this.incommingAllGroups,
  });

  @override
  State<SearchGroup> createState() => _SearchGroupState();
}

class _SearchGroupState extends State<SearchGroup> {
  TextEditingController searchKeyword = TextEditingController();
  List<Map<String, dynamic>> displayedGroups = []; //화면에 표시할 그룹 결정
  bool isSearchActive = false; // 검색 활성화중인지 확인하는 변수

  //초기상태
  @override
  void initState() {
    super.initState();
    displayedGroups = widget.incommingAllGroups;
  }

  //서치중인지 여부 확인 후 디스플레이 설정
  void handleSearch(String keyword) async {
    if (keyword.isEmpty) {
      print("키워드가 비었습니다. 모든 그룹을 표시합니다");
      setState(() {
        displayedGroups = widget.incommingAllGroups;
        isSearchActive = false;
      });
      return;
    }
    try {
      final results = await getSearchResult(keyword);
      print("서치 결과 출력 : $results");
      setState(() {
        displayedGroups = results;
        isSearchActive = true;
      });
    } catch (e) {
      print("서치 결과 받아오기 실패");
    }
  }

  @override
  Widget build(BuildContext context) {
    //스크린 크기 받아오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    final List<SearchGroupCard> groupCards =
        widget.incommingAllGroups.map((group) {
      return SearchGroupCard(
          groupId: group['teamId'],
          groupName: group['teamName'],
          description: group['teamExplain'],
          numOfMember: group['userCount'],
          isProtected: group['hasPassword']);
    }).toList();
    return SizedBox(
      height: screenHeight, //스크롤뷰 기본 크기 지정
      child: SingleChildScrollView(
          child: Column(children: [
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
                      settings: const RouteSettings(name: '/creategroup'),
                    ));
              },
              child: SvgPicture.asset(
                'assets/buttons/new_create_group.svg',
                width: screenWidth * 0.3,
              ),
            ),
          ),
        ),
        Row(
          //여기 textfield 추가하나끼 오류남
          children: [
            Expanded(
                //검색창 영역
                child: Padding(
              padding: EdgeInsets.only(
                  left: screenWidth * 0.05, right: screenWidth * 0.02),
              child: SizedBox(
                height: screenHeight * 0.09, //textFiled 높이 조정 sizedbox로 가능
                child: TextField(
                  maxLength: 10,
                  controller: searchKeyword,
                  decoration: InputDecoration(
                    labelText: "그룹 이름으로 검색하세요.",
                    fillColor: Colors.white.withOpacity(0.6),
                    filled: true,
                  ),
                ),
              ),
            )),
            Padding(
              padding: EdgeInsets.only(
                  bottom: screenHeight * 0.03, right: screenWidth * 0.05),
              child: InkWell(
                onTap: () {
                  handleSearch(searchKeyword.text);
                },
                child: Image.asset('assets/buttons/search_icon.png',
                    width: screenWidth * 0.12),
              ),
            )
          ],
        ),
        //검색 활성화, 검색 결과 없음
        if (isSearchActive && displayedGroups.isEmpty)
          SizedBox(
            height: screenHeight * 0.5,
            child: const Text("검색 결과가 없습니다"),
          ),
        if (isSearchActive && displayedGroups.isNotEmpty)
          ...displayedGroups.map((group) => SearchGroupCard(
                groupId: group['teamId'],
                groupName: group['teamName'],
                description: group['teamExplain'],
                numOfMember: group['userCount'],
                isProtected: group['hasPassword'],
              )),
        if (!isSearchActive) ...groupCards,
      ])),
    );
  }
}
