import 'package:flutter/material.dart';
import 'package:walkingpet/main.dart';
import 'package:walkingpet/services/character/statupdate.dart';

class CharacterInfoStat extends StatefulWidget {
  final String statname, statnameEn;
  final int characterId;
  int point, addpoint, statPoint;
  Function(String, int, int, int) updateStatPoint;

  CharacterInfoStat({
    super.key,
    required this.statname,
    required this.point,
    required this.addpoint,
    required this.characterId,
    required this.statnameEn,
    required this.updateStatPoint,
    required this.statPoint,
  });

  @override
  State<CharacterInfoStat> createState() => _CharacterInfoStatState();
}

class _CharacterInfoStatState extends State<CharacterInfoStat> {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 7),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // 1. 아이콘
          SizedBox(
            width: 25,
            // height: 20,
            child: Image.asset(_getImagePath(widget.statname)),
          ),

          // SvgPicture.asset(
          //   'assets/icons/icon_health.svg',
          //   // width: 20,
          //   height: 20,
          // ),

          // 2. 체력 / 공격력 / 방어력
          SizedBox(
            width: 70,
            child: Text(
              widget.statname,
              style: const TextStyle(
                fontSize: 20,
              ),
              textAlign: TextAlign.center,
            ),
          ),

          // 3. 능력치
          SizedBox(
            width: 95,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  widget.point.toString(),
                  style: const TextStyle(
                    fontSize: 20,
                  ),
                ),
                const Text(
                  '(',
                  style: TextStyle(
                    fontSize: 17,
                  ),
                ),
                const Text(
                  '+',
                  style: TextStyle(
                    fontSize: 13,
                    color: Color.fromARGB(255, 166, 39, 30),
                  ),
                ),
                Text(
                  widget.addpoint.toString(),
                  style: const TextStyle(
                    fontSize: 16,
                    color: Color.fromARGB(255, 166, 39, 30),
                  ),
                ),
                const Text(
                  ')',
                  style: TextStyle(
                    fontSize: 17,
                  ),
                ),
              ],
            ),
          ),

          // 4. '+' 버튼
          GestureDetector(
            onTap: _updateStats,
            child: widget.statPoint == 0
                ? Container() // statPoint가 0이면 이미지를 숨김
                : Image.asset('assets/buttons/yellow_plus_button.png',
                    scale: 0.8),
          ),
        ],
      ),
    );
  }

  String _getImagePath(String statname) {
    switch (statname) {
      case '체력':
        return 'assets/icons/icon_health.png';
      case '공격력':
        return 'assets/icons/icon_power.png';
      case '방어력':
        return 'assets/icons/icon_depense.png';
      default:
        return '';
    }
  }

  void _updateStats() async {
    try {
      var characterId = widget.characterId;
      var statnameEn = widget.statnameEn;
      var response = await updateStat(characterId, statnameEn);
      var data = response['data'];

      var point = data[statnameEn];
      var addpoint =
          data['add${statnameEn[0].toUpperCase()}${statnameEn.substring(1)}'];
      var statPoint = data['statPoint'];

      setState(() {
        widget.point = point;
        widget.addpoint = addpoint;
        widget.updateStatPoint(statnameEn, point, addpoint, statPoint);
      });
      print(data);
    } catch (e) {
      print('캐릭터 능력치 Update, 페이지 내 오류: $e');
    }
  }
}
