import 'package:audioplayers/audioplayers.dart';

class AudioManager {
  static final AudioManager _instance = AudioManager._internal();
  late AudioPlayer _audioPlayer;
  late String _currentAssetPath;

  factory AudioManager() {
    return _instance;
  }

  AudioManager._internal() {
    _audioPlayer = AudioPlayer();
    _audioPlayer.onPlayerComplete.listen((event) {
      Future.delayed(const Duration(seconds: 1), () {
        _audioPlayer.play(AssetSource(_currentAssetPath));
      });
    });
  }

  void play(String assetPath) async {
    _currentAssetPath = assetPath;
    await _audioPlayer.setReleaseMode(ReleaseMode.loop);
    await _audioPlayer.play(AssetSource(assetPath));
  }

  void stop() async {
    await _audioPlayer.stop();
  }

  void dispose() {
    _audioPlayer.dispose();
  }
}
