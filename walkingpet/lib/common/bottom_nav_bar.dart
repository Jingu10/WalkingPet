import 'package:flutter/material.dart';

class BottomNavBar extends StatelessWidget {
  final int selectedIndex;

  const BottomNavBar({super.key, required this.selectedIndex});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 8),
      color: const Color.fromRGBO(255, 255, 255, 1),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          navBarItem(context, Icons.pets, '캐릭터', 0),
          verticalDivider(),
          navBarItem(context, Icons.star, '뽑기', 1),
          verticalDivider(),
          navBarItem(context, Icons.home, '홈', 2),
          verticalDivider(),
          navBarItem(context, Icons.gamepad, '배틀', 3),
          verticalDivider(),
          navBarItem(context, Icons.group, '그룹', 4),
        ],
      ),
    );
  }

  Widget navBarItem(
      BuildContext context, IconData icon, String label, int index) {
    return InkWell(
      onTap: () {
        // Navigate
        _onItemTapped(context, index);
      },
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          Icon(icon,
              size: 30,
              color: selectedIndex == index
                  ? const Color.fromRGBO(3, 169, 244, 1)
                  : const Color.fromRGBO(214, 214, 214, 1)),
          Text(
            label,
            style: TextStyle(
                color: selectedIndex == index
                    ? const Color.fromRGBO(3, 169, 244, 1)
                    : const Color.fromRGBO(214, 214, 214, 1)),
          )
        ],
      ),
    );
  }

  Widget verticalDivider() {
    return Container(
      height: 30,
      width: 1,
      color: Colors.grey,
    );
  }
}

// 이 함수 내에 네비게이션 로직 추가
void _onItemTapped(BuildContext context, int index) {
  switch (index) {
    case 0:
      Navigator.pushReplacementNamed(context, '/characterinfo');
      break;
    case 1:
      Navigator.pushReplacementNamed(context, '/gacha');
      break;
    case 2:
      Navigator.pushReplacementNamed(context, '/home');
      break;
    case 3:
      Navigator.pushReplacementNamed(context, '/battle');
      break;
    case 4:
      Navigator.pushReplacementNamed(context, '/group');
      break;
  }
}
