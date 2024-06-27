import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/ranking/widgets/myrank.dart';
import 'package:walkingpet/ranking/widgets/top10.dart';
import 'package:walkingpet/ranking/widgets/top3.dart';
import 'package:walkingpet/services/ranking/personal.dart';

class PersonalRanking extends StatefulWidget {
  const PersonalRanking({super.key});

  @override
  State<PersonalRanking> createState() => _PersonalRankingState();
}

class _PersonalRankingState extends State<PersonalRanking> {
  // 필요한 변수 만들기
  List top10 = [];
  Map<String, dynamic> myrank = {};
  List top3 = [];
  bool isLoading = true;
  String selectedTimeFrame = 'realtime';

  @override
  void initState() {
    super.initState();
    fetchData();
  }

  // API 요청으로 데이터 불러오기 => 기본 realtime으로 설정
  Future<void> fetchData({String timeframe = 'realtime'}) async {
    try {
      var responseTop10 = await getTop10(timeframe: timeframe);
      var responseMyRank = await getMyRank(timeframe: timeframe);
      var responseTop3 = await getTop3(timeframe: timeframe);

      setState(() {
        top10 = responseTop10['data']['topRanking'];
        myrank = responseMyRank['data'];
        top3 = responseTop3['data']['topRanking'];
        isLoading = false;
        selectedTimeFrame = timeframe;
      });
    } catch (e) {
      setState(() {
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: isLoading
          ? const CircularProgressIndicator() // 로딩 중 인디케이터 추가
          : Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                // 1. 어제 / 실시간 / 누적 선택

                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'yesterday'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 10, bottom: 20),
                        child: const Text('  '),
                      ),
                    ),
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'yesterday'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 15, bottom: 15),
                        child: Text(
                          ' 어제 ',
                          style: TextStyle(
                            fontWeight: selectedTimeFrame == 'yesterday'
                                ? FontWeight.bold
                                : FontWeight.normal,
                            color: selectedTimeFrame == 'yesterday'
                                ? const Color.fromARGB(255, 9, 118, 208)
                                : Colors.black,
                          ),
                        ),
                      ),
                    ),
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'realtime'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 15, bottom: 15),
                        child: const Text('|'),
                      ),
                    ),
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'realtime'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 15, bottom: 15),
                        child: Text(
                          ' 실시간 ',
                          style: TextStyle(
                            fontWeight: selectedTimeFrame == 'realtime'
                                ? FontWeight.bold
                                : FontWeight.normal,
                            color: selectedTimeFrame == 'realtime'
                                ? const Color.fromARGB(255, 9, 118, 208)
                                : Colors.black,
                          ),
                        ),
                      ),
                    ),
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'realtime'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 15, bottom: 15),
                        child: const Text('|'),
                      ),
                    ),
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'accumulation'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 15, bottom: 15),
                        child: Text(
                          ' 누적 ',
                          style: TextStyle(
                            fontWeight: selectedTimeFrame == 'accumulation'
                                ? FontWeight.bold
                                : FontWeight.normal,
                            color: selectedTimeFrame == 'accumulation'
                                ? const Color.fromARGB(255, 9, 118, 208)
                                : Colors.black,
                          ),
                        ),
                      ),
                    ),
                    GestureDetector(
                      onTap: () => fetchData(timeframe: 'accumulation'),
                      child: Container(
                        margin: const EdgeInsets.only(top: 15, bottom: 15),
                        child: const Text('  '),
                      ),
                    ),
                  ],
                ),

                // 2. 1~3위 표시
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ...top3.map((item) {
                      return Top1to3(
                        ranking: item['ranking'] as int? ?? 0,
                        characterId: item['characterId'] as int? ?? 0,
                        nickname: item['nickname'] as String? ?? 'Unknown',
                      );
                    }),
                  ],
                ),

                // 3. 유저의 랭킹 표시
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 20),
                  child: Container(
                    decoration: BoxDecoration(
                      color: const Color.fromARGB(255, 255, 255, 255)
                          .withOpacity(0.8),
                      borderRadius: BorderRadius.circular(5),
                    ),
                    margin: const EdgeInsets.symmetric(horizontal: 7),
                    child: MyRank(
                      ranking: myrank['ranking'] as int? ?? 0,
                      score: myrank['step'] as int? ?? 0,
                      characterId: myrank['characterId'] as int? ?? 0,
                      nickname: myrank['nickname'] as String? ?? 'Unknown',
                      rankingUnit: '걸음',
                    ),
                  ),
                ),

                // 4. 4~10위 표시
                Container(
                  // 4-1. 영역 표시
                  decoration: BoxDecoration(
                    color: const Color.fromARGB(255, 255, 255, 255)
                        .withOpacity(0.8),
                    borderRadius: BorderRadius.circular(5),
                  ),
                  margin: const EdgeInsets.symmetric(
                    horizontal: 7,
                  ),

                  child: Column(
                    children: [
                      // 4-2. Top 10 텍스트
                      const Text(
                        'Top 10',
                        style: TextStyle(fontSize: 24),
                      ),

                      // 4-3. 내용 (스크롤 가능)
                      SizedBox(
                        height: 220,
                        child: SingleChildScrollView(
                          scrollDirection: Axis.vertical,
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              ...top10.map((item) {
                                return Rank(
                                    ranking: item['ranking'] as int? ?? 0,
                                    nickname: item['nickname'] as String? ??
                                        'Unknown',
                                    score: item['step'] as int? ?? 0,
                                    rankingUnit: '걸음');
                              }),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
    );
  }
}
