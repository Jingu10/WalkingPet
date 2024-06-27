import 'package:flutter/cupertino.dart';
import 'package:walkingpet/services/gacha/count_remain_box.dart';

class BoxCounterProvider extends ChangeNotifier {
  int normalBoxCount = 0;
  int luxuryBoxCount = 0;

  //상자 수 초기화
  Future<void> initializeBoxCounts() async {
    var data = await countRemainBox();
    normalBoxCount = data['normalBoxCount'] ?? 0;
    luxuryBoxCount = data['luxuryBoxCount'] ?? 0;
    notifyListeners(); //리스너에게 상태 변경 알림
  }

  //일반 상자 수 업데이트
  void openNormalBox() {
    if (normalBoxCount > 0) {
      normalBoxCount--;
      notifyListeners();
    }
  }

  //고급 상자 수 업데이트
  void openLuxuryBox() {
    if (luxuryBoxCount > 0) {
      luxuryBoxCount--;
      notifyListeners();
    }
  }
}
