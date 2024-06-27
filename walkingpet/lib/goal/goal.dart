import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/goal/widgets/daily.dart';
import 'package:walkingpet/goal/widgets/goal_discription_modal.dart';
import 'package:walkingpet/goal/widgets/weekly.dart';
// 페이지 로드 시 목표 달성 여부를 로드
import 'package:walkingpet/services/goal/get_goal_info.dart';
import 'package:walkingpet/providers/step_counter.dart';
import 'package:provider/provider.dart';
import 'package:pixelarticons/pixelarticons.dart';
import 'package:flutter_svg/flutter_svg.dart';

class Goal extends StatefulWidget {
  const Goal({super.key});

  @override
  State<Goal> createState() => _GoalState();
}

class _GoalState extends State<Goal> {
  List<bool> dailyGoals = [];
  List<bool> weeklyGoals = [];
  Map<String, bool> stampedDays = {};
  //로딩 상태를 나타내는 변수
  bool isLoading = true;
  //일일 목표 관련
  bool isAttended = true;
  bool isWalked3000 = false;
  bool isWalked5000 = false;
  bool isWalked7000 = false;
  bool isWalked10000 = false;
  // 걸음 수 초기화
  int steps = 0;

  @override
  void initState() {
    super.initState();
    //then() 메서드를 사용하여 Future가 완료되었을 때 얻은 결과를 처리
    getGoalInfo().then(
      (data) {
        setState(
          () {
            //data를 State 변수에 저장
            // steps = data['step'];
            dailyGoals = List<bool>.from(data['dailyGoal']);
            weeklyGoals = List<bool>.from(data['weeklyGoal']);
            print('받아서 새로 저장한 데이터: $weeklyGoals');
            // Map 업데이트
            stampedDays = {
              '월요일': weeklyGoals[0],
              '화요일': weeklyGoals[1],
              '수요일': weeklyGoals[2],
              '목요일': weeklyGoals[3],
              '금요일': weeklyGoals[4],
              '토요일': weeklyGoals[5],
              '일요일': weeklyGoals[6]
            };
            // 초기 일일 목표 상태를 업데이트
            updateDailyGoals(steps);
            isLoading = false;
          },
        );
      },
    );
  }

  void updateDailyGoals(int steps) {
    setState(() {
      isWalked3000 = steps >= 3000;
      isWalked5000 = steps >= 5000;
      isWalked7000 = steps >= 7000;
      isWalked10000 = steps >= 10000;
    });
  }

  @override
  Widget build(BuildContext context) {
    // 현재 화면의 크기를 가져오기
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    // 걸음수 가져오기
    int steps = context.watch<StepCounter>().steps ?? 0; // 초기값으로 0 사용
    int goalStep = 10000;
    // 현재 걸음 수를 기반으로 일일 목표 업데이트
    updateDailyGoals(steps);

    return Scaffold(
      // appBar: AppBar(
      //   title: const Text('목표'),
      // ),
      body: isLoading
          ? const Center(
              child: CircularProgressIndicator(),
            )
          : Stack(
              children: <Widget>[
                //1. 배경 이미지
                Positioned.fill(
                  child: Image.asset(
                    'assets/backgrounds/goal.png',
                    fit: BoxFit.cover,
                  ),
                ),
                Center(
                  child: Container(
                    width: screenWidth * 0.9,
                    height: screenHeight * 0.9,
                    color: const Color(0xfffff3dc).withOpacity(0.8),
                  ),
                ),
                Transform.translate(
                  offset: Offset(screenWidth * 0.08, screenHeight * 0.06),
                  child: InkWell(
                    onTap: () async {
                      await GoalDiscriptionModal.showDiscription(
                          context: context);
                    },
                    child: SvgPicture.asset(
                      'assets/icons/question_mark.svg',
                      width: screenWidth * 0.1,
                    ),
                  ),
                ),
                // 닫기 버튼
                Positioned(
                  top: screenHeight * 0.05, // 원하는 만큼 더 내리기
                  right: screenWidth * 0.05,

                  child: TextButton(
                    onPressed: () async {
                      Navigator.pop(context);
                      // Navigator.pushReplacementNamed(
                      //     context, '/home'); // 현재 경로를 '/home'으로 교체
                    },
                    style: ButtonStyle(
                      padding: MaterialStateProperty.all(
                          EdgeInsets.zero), // 버튼의 내부 패딩 제거
                    ),
                    child: const Icon(Pixel.close),
                    // child: Text(
                    //   'X',
                    //   style: TextStyle(
                    //       fontSize: screenWidth * 0.10, color: Colors.black),
                    // ),
                  ),
                ),
                Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    SizedBox(
                      height: screenHeight * 0.08,
                    ),
                    Center(
                      child: SizedBox(
                        width: screenWidth * 0.6,
                        height: screenWidth * 0.6,
                        child: FittedBox(
                          child: Column(
                            children: [
                              Stack(
                                alignment: Alignment.center,
                                children: <Widget>[
                                  CircularProgressIndicator(
                                    backgroundColor: const Color(0xffeeeeee),
                                    color: const Color(0xff47E1A9),
                                    value: steps / goalStep,
                                    strokeWidth: 3,
                                  ),
                                  Column(
                                    children: [
                                      Text(
                                        '$steps',
                                        style: const TextStyle(
                                          fontSize: 8,
                                        ),
                                      ),
                                      const Text(
                                        "오늘",
                                        style: TextStyle(fontSize: 5),
                                      ),
                                      Text('목표 : $goalStep',
                                          style: const TextStyle(fontSize: 3))
                                    ],
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                    Center(
                      child: Padding(
                        padding:
                            EdgeInsets.symmetric(vertical: screenHeight * 0.02),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              "일일 목표",
                              style: TextStyle(
                                fontSize: screenHeight * 0.04,
                              ),
                            ),
                            DailyGoalItem(
                              title: "출석",
                              isActivated: isAttended,
                              isCompleted: dailyGoals[0],
                              goalSteps: 0,
                            ),
                            DailyGoalItem(
                              title: "3000 걸음",
                              isActivated: isWalked3000,
                              isCompleted: dailyGoals[1],
                              goalSteps: 3000,
                            ),
                            DailyGoalItem(
                              title: "5000 걸음",
                              isActivated: isWalked5000,
                              isCompleted: dailyGoals[2],
                              goalSteps: 5000,
                            ),
                            DailyGoalItem(
                              title: "7000 걸음",
                              isActivated: isWalked7000,
                              isCompleted: dailyGoals[3],
                              goalSteps: 7000,
                            ),
                            DailyGoalItem(
                              title: "10000 걸음",
                              isActivated: isWalked10000,
                              isCompleted: dailyGoals[4],
                              goalSteps: 10000,
                            ),
                            Text(
                              "주간 목표",
                              style: TextStyle(
                                fontSize: screenHeight * 0.04,
                              ),
                            ),
                            Column(
                              children: [
                                Row(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      WeeklyGoalItem(
                                          // 단축연산 - 왼쪽 값이 null이면 false 반환
                                          text: '월요일',
                                          isStamped:
                                              stampedDays['월요일'] ?? false),
                                      WeeklyGoalItem(
                                          text: '화요일',
                                          isStamped:
                                              stampedDays['화요일'] ?? false),
                                      WeeklyGoalItem(
                                          text: '수요일',
                                          isStamped:
                                              stampedDays['수요일'] ?? false),
                                      WeeklyGoalItem(
                                          text: '목요일',
                                          isStamped:
                                              stampedDays['목요일'] ?? false),
                                    ]),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    WeeklyGoalItem(
                                        text: '금요일',
                                        isStamped: stampedDays['금요일'] ?? false),
                                    WeeklyGoalItem(
                                        text: '토요일',
                                        isStamped: stampedDays['토요일'] ?? false),
                                    WeeklyGoalItem(
                                        text: '일요일',
                                        isStamped: stampedDays['일요일'] ?? false),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
    );
  }
}
