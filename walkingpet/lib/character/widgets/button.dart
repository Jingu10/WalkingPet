import 'package:flutter/material.dart';

class DottedButton extends StatelessWidget {
  final VoidCallback onTap;
  final String label;
  final double dotSize;
  final Color dotColor;
  final Color textColor;
  final int numberOfDots;

  const DottedButton({
    super.key,
    required this.onTap,
    required this.label,
    this.dotSize = 4.0,
    this.dotColor = Colors.black,
    this.textColor = Colors.black,
    this.numberOfDots = 10,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(8.0),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(30.0),
          border: Border.all(color: dotColor),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              label,
              style: TextStyle(color: textColor),
            ),
            const SizedBox(width: 8.0),
            Wrap(
              children: List.generate(numberOfDots, (index) {
                return Container(
                  width: dotSize,
                  height: dotSize,
                  margin: const EdgeInsets.symmetric(horizontal: 2.0),
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: dotColor,
                  ),
                );
              }),
            ),
          ],
        ),
      ),
    );
  }
}
