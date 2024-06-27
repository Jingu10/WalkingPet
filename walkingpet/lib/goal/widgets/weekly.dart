import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class WeeklyGoalItem extends StatelessWidget {
  final String text;
  final bool isStamped;

  const WeeklyGoalItem({
    super.key,
    required this.text,
    required this.isStamped,
  });

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;
    return Padding(
      padding: EdgeInsets.all(screenWidth * 0.01),
      child: Stack(
        children: [
          Image.asset(
              isStamped
                  ? 'assets/buttons/weekly_stamped.png'
                  : 'assets/buttons/weekly_plain.png',
              scale: 0.7
              // fit: BoxFit.cover, //할당된 공간에 꽉 차게
              ),
          Positioned(
            bottom: 0.1,
            right: 14,
            child: Text(text,
                style: TextStyle(
                  fontSize: screenHeight * 0.02,
                  color: Colors.white,
                )),
          ),
        ],
      ),
    );
  }
}
