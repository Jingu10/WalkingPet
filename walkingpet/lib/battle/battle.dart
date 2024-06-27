import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/battle/battleresult.dart';
import 'package:walkingpet/battle/battleside.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';
import 'package:walkingpet/services/battle/getbattleinfo.dart';

class Battle extends StatefulWidget {
  final Map<String, dynamic> myCharacterData; // widget.characterData로 사용
  const Battle({super.key, required this.myCharacterData});

  @override
  State<Battle> createState() => _BattleState();
}

class _BattleState extends State<Battle> with WidgetsBindingObserver {
  Map<String, dynamic> battleData = {};
  bool isLoading = true;
  late AudioPlayer _audioPlayer;

  @override
  void initState() {
    super.initState();
    initBattleInfo(); // 위젯이 로드될 때 fetchData 호출
    WidgetsBinding.instance.addObserver(this);
    _audioPlayer = AudioPlayer();
    _playMusic();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _stopMusic();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _playMusic();
    } else if (state == AppLifecycleState.paused) {
      _stopMusic();
    }
  }

  void _playMusic() async {
    await _audioPlayer.setReleaseMode(ReleaseMode.loop);
    await _audioPlayer.play(AssetSource('audio/battle.mp3'));
  }

  void _stopMusic() async {
    await _audioPlayer.stop();
  }

  Future<void> initBattleInfo() async {
    try {
      var response = await getBattleInfo();
      setState(() {
        battleData = response['data']; // API 응답을 상태에 저장
        isLoading = false;
      });
    } catch (e) {
      isLoading = false;
    }
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return PopScope(
      canPop: false,
      child: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage("assets/backgrounds/battle.png"),
            //fit: BoxFit.cover, // 배경 이미지가 전체 화면을 채우도록 설정
          ),
        ),
        child: Scaffold(
          backgroundColor: Colors.transparent, // Scaffold의 배경을 투명하게 설정
          body: Stack(
            children: [
              InkWell(
                onTap: () {
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BattleResult(
                        battleResult: battleData['battleResultInfo'],
                        animal: CharacterMap.idToAnimal[
                                widget.myCharacterData['characterId']] ??
                            "cow",
                      ),
                    ),
                  );
                },
                child: Transform.translate(
                  offset: Offset(0, -screenHeight * 0.3),
                  child: Center(
                    child: Stack(
                      alignment: AlignmentDirectional.center,
                      children: [
                        Image.asset(
                          'assets/buttons/skip_button.png',
                          scale: 1.8,
                        ),
                        MainFontStyle(size: screenWidth * 0.05, text: "SKIP")
                      ],
                    ),
                  ),
                ),
              ),
              Column(
                children: [
                  if (!isLoading)
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        BattleSide(
                            health: widget.myCharacterData['health'],
                            power: widget.myCharacterData['power'],
                            defense: widget.myCharacterData['defense'],
                            rating: widget.myCharacterData['rating'],
                            characterId: widget.myCharacterData['characterId'],
                            userCharacterLevel: widget.myCharacterData['level'],
                            nickname: widget.myCharacterData['nickname'],
                            isLeft: true, // 입은 데미지
                            attackDamage: battleData['battleProgressInfo']
                                ['userAttackDamage'],
                            receivedDamage: battleData['battleProgressInfo']
                                ['enemyAttackDamage'],
                            userHealth: battleData['battleProgressInfo']
                                ['userHealth'],
                            loseDamage: battleData['battleProgressInfo']
                                ['userLoseDamage'],
                            battleResult: battleData['battleResultInfo']),
                        Transform.translate(
                          offset: Offset(0, screenHeight * 0.37),
                          child: MainFontStyle(
                              size: screenWidth * 0.05, text: "vs"),
                        ),
                        BattleSide(
                            health: battleData['enemyInfo']['health'],
                            power: battleData['enemyInfo']['power'],
                            defense: battleData['enemyInfo']['defense'],
                            rating: battleData['enemyInfo']['rating'],
                            characterId: battleData['enemyInfo']['characterId'],
                            userCharacterLevel: battleData['enemyInfo']
                                ['level'],
                            nickname: battleData['enemyInfo']['nickname'],
                            isLeft: false,
                            attackDamage: battleData['battleProgressInfo']
                                ['enemyAttackDamage'],
                            receivedDamage: battleData['battleProgressInfo']
                                ['userAttackDamage'],
                            userHealth: battleData['battleProgressInfo']
                                ['userHealth'],
                            loseDamage: battleData['battleProgressInfo']
                                ['enemyLoseDamage'],
                            battleResult: battleData['battleResultInfo']),
                      ],
                    ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
