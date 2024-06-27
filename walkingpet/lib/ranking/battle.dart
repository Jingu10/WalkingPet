import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/providers/character_info.dart';
import 'package:walkingpet/ranking/widgets/myrank.dart';
import 'package:walkingpet/ranking/widgets/top10.dart';
import 'package:walkingpet/ranking/widgets/top3.dart';
import 'package:walkingpet/services/ranking/battle.dart';
import 'package:provider/provider.dart';

class BattleRanking extends StatefulWidget {
  const BattleRanking({super.key});

  @override
  State<BattleRanking> createState() => _BattleRankingState();
}

class _BattleRankingState extends State<BattleRanking> {
  // 필요한 변수 만들기
  List top10 = [];
  Map<String, dynamic> myrank = {};
  List top3 = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchData();
  }

  // API 요청으로 데이터 불러오기
  Future<void> fetchData() async {
    try {
      var responseTop10 = await getBattleTop10();
      var responseMyRank = await getBattleMyRank();
      var responseTop3 = await getBattleTop3();

      setState(() {
        top10 = responseTop10['data']['topRanking'];
        myrank = responseMyRank['data'];
        top3 = responseTop3['data']['topRanking'];
        isLoading = false;
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
                // 1. 개인랭킹과의 줄맞춤을 위한 영역
                Container(
                  margin: const EdgeInsets.only(top: 15, bottom: 15),
                  child: const Text('  '),
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
                    child: Consumer<CharacterProvider>(
                        builder: (context, characterProvider, child) {
                      return MyRank(
                        ranking: myrank['ranking'] as int? ?? 0,
                        score: myrank['battleRating'] as int? ?? 0,
                        characterId: characterProvider.characterId,
                        nickname: characterProvider.nickname,
                        rankingUnit: '점',
                      );
                    }),
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
                  margin: const EdgeInsets.symmetric(horizontal: 7),

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
                                  nickname:
                                      item['nickname'] as String? ?? 'Unknown',
                                  score: item['battleRating'] as int? ?? 0,
                                  rankingUnit: '점',
                                );
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
