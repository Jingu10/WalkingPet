import 'package:flutter/material.dart';
import 'package:walkingpet/ranking/widgets/group_rank.dart';
import 'package:walkingpet/services/ranking/group.dart';

class GroupRanking extends StatefulWidget {
  const GroupRanking({super.key});

  @override
  State<GroupRanking> createState() => _GroupRankingState();
}

class _GroupRankingState extends State<GroupRanking> {
  // 필요한 변수 만들기
  int mygroupCount = 0;
  List myGroups = [];
  List groups = [];
  // Map<String, dynamic> myrank = {};
  bool isLoading = true;
  final PageController _pageController = PageController();
  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    fetchData();
  }

  // API 요청으로 데이터 불러오기
  Future<void> fetchData() async {
    try {
      var responseMyGroupConut = await getMyGroupCount();
      var responseMyGroup = await getMyGroup();
      var responseGroup = await getGroup();

      setState(() {
        mygroupCount = responseMyGroupConut['data']['groupCount'];
        myGroups = responseMyGroup['data']['rankings'];
        groups = responseGroup['data']['rankings'];
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        isLoading = false;
      });
    }
  }

  void _previousPage() {
    if (_currentPage > 0) {
      setState(() {
        _currentPage--;
      });
      _pageController.previousPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  void _nextPage() {
    if (_currentPage < myGroups.length - 1) {
      setState(() {
        _currentPage++;
      });
      _pageController.nextPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return Center(
      child: isLoading
          ? const CircularProgressIndicator() // 로딩 중 인디케이터 추가
          : Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                // 1. 개인랭킹과의 줄맞춤을 위한 영역
                // Container(
                //   margin: const EdgeInsets.only(top: 15, bottom: 15),
                //   child: const Text('  '),
                // ),

                // 2. 유저의 그룹 랭킹 표시
                const Padding(
                  padding: EdgeInsets.only(top: 15, bottom: 5),
                  child: Text(
                    'My Group',
                    style: TextStyle(fontSize: 30),
                    // '----- My Group -----',
                    // style: TextStyle(fontSize: 24),
                  ),
                ),

                // 2-1. 가입한 그룹이 없을 때
                if (mygroupCount == 0)
                  Container(
                    height: screenHeight * 0.11,
                    width: screenWidth * 0.7,
                    decoration: BoxDecoration(
                      color: const Color.fromARGB(255, 255, 255, 255)
                          .withOpacity(0.8),
                      borderRadius: BorderRadius.circular(5),
                    ),
                    margin:
                        const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                    padding: const EdgeInsets.symmetric(vertical: 5),
                    child: const Center(
                      child: Text(
                        '함께 걷는 그룹이\n존재하지 않습니다.',
                        style: TextStyle(
                            fontSize: 20,
                            color: Color.fromARGB(255, 8, 84, 145)),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ),

                // 2-2. 가입한 그룹이 있을 때
                if (mygroupCount > 0)
                  Stack(
                    children: [
                      SizedBox(
                        width: screenWidth * 0.9,
                        height: screenHeight * 0.13,
                        child: PageView(
                          controller: _pageController,
                          onPageChanged: (index) {
                            setState(() {
                              _currentPage = index;
                            });
                          },
                          children: myGroups.map((item) {
                            return GroupRank(
                              ranking: item['ranking'] as int? ?? 0,
                              teamPoint: item['teamPoint'] as int? ?? 0,
                              teamName:
                                  item['teamName'] as String? ?? 'Unknown',
                            );
                          }).toList(),
                        ),
                      ),
                      Positioned(
                        left: 0,
                        top: 25,
                        bottom: 0,
                        child: Visibility(
                          visible: _currentPage > 0,
                          child: IconButton(
                            icon: const Icon(
                              Icons.arrow_left,
                              color: Colors.blue,
                            ),
                            onPressed: _previousPage,
                            iconSize: 50.0,
                          ),
                        ),
                      ),
                      Positioned(
                        right: 0,
                        top: 25,
                        bottom: 0,
                        child: Visibility(
                          visible: _currentPage < myGroups.length - 1,
                          child: IconButton(
                            icon: const Icon(
                              Icons.arrow_right,
                              color: Colors.blue,
                            ),
                            onPressed: _nextPage,
                            iconSize: 50.0,
                          ),
                        ),
                      ),
                    ],
                  ),

                // 3. 모든 그룹 랭킹 표시
                const Padding(
                  padding: EdgeInsets.only(top: 20, bottom: 5),
                  child: Text(
                    'Top 10',
                    style: TextStyle(fontSize: 30),
                    // '-----  Top 10  -----',
                    // style: TextStyle(fontSize: 24),
                  ),
                ),

                SizedBox(
                  height: screenHeight * 0.47,
                  child: SingleChildScrollView(
                    scrollDirection: Axis.vertical,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        ...groups.map((item) {
                          return GroupRank(
                            ranking: item['ranking'] as int? ?? 0,
                            teamPoint: item['teamPoint'] as int? ?? 0,
                            teamName: item['teamName'] as String? ?? 'Unknown',
                          );
                        }),
                      ],
                    ),
                  ),
                ),
              ],
            ),
    );
  }
}
