import 'package:flutter/material.dart';

class Star extends StatelessWidget {
  final int count;

  const Star({super.key, this.count = 1});

  @override
  Widget build(BuildContext context) {
    List<Widget> stars = List<Widget>.generate(
      count, // count 값에 따라 별의 개수 조절
      (index) => index == 1 && count == 3
          ? Transform.translate(
              offset: const Offset(0, -7),
              child: const Icon(
                Icons.star,
                color: Colors.red,
              ),
            )
          : const Icon(
              Icons.star,
              color: Colors.red,
            ),
    );

    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: stars,
    );
  }
}
