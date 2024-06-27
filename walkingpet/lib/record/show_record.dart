import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:walkingpet/common/character_map.dart';
import 'package:walkingpet/home/widgets/mainfontstyle.dart';

void showRecord(BuildContext context, String nickname, int characterId,
    String imageUrl, String regDate) {
  double screenWidth = MediaQuery.of(context).size.width;
  double screenHeight = MediaQuery.of(context).size.height;
  String animal = CharacterMap.idToAnimal[characterId] ?? "";
  showDialog(
    context: context,
    barrierDismissible: true,
    builder: (BuildContext context) {
      return Center(
        child: Stack(
          alignment: Alignment.center,
          children: [
            Material(
              color: Colors.transparent,
              child: Image.asset('assets/images/diary.png'),
            ),
            Transform.translate(
              offset: Offset(screenWidth * -0.036, screenHeight * -0.04),
              child: Image.network(
                imageUrl,
                fit: BoxFit.cover,
                height: screenHeight * 0.33,
                width: screenWidth * 0.55,
                alignment: Alignment.center,
              ),
            ),
            Transform.translate(
                offset: Offset(screenWidth * -0.15, screenHeight * -0.2),
                child: MainFontStyle(size: screenWidth * 0.08, text: nickname)),
            Transform.translate(
                offset: Offset(screenWidth * 0.13, screenHeight * 0.16),
                child: MainFontStyle(
                    size: screenWidth * 0.06, text: regDate.split('T')[0])),
            SizedBox(
              child: Image.asset('assets/animals/$animal/${animal}_walk.gif'),
            )
          ],
        ),
      );
    },
  );
}
