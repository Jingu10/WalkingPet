import 'package:flutter/material.dart';

class CharacterProvider with ChangeNotifier {
  int _characterId;
  String _nickname;

  // Singleton instance
  static final CharacterProvider _instance = CharacterProvider._internal();

  factory CharacterProvider() {
    return _instance;
  }

  CharacterProvider._internal()
      : _characterId = 0,
        _nickname = '';

  int get characterId => _characterId;
  String get nickname => _nickname;

  set characterId(int id) {
    _characterId = id;
    notifyListeners();
  }

  set nickname(String name) {
    _nickname = name;
    notifyListeners();
  }

  void updateCharacter(int id, String name) {
    _characterId = id;
    _nickname = name;
    notifyListeners();
  }
}
