import 'package:flutter/material.dart';
import 'audio_manager.dart';

class MusicNavigatorObserver extends NavigatorObserver {
  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPush(route, previousRoute);
    if (route is! DialogRoute &&
        route.settings.name != '/characterexp' &&
        route.settings.name != '/creategroup' &&
        route.settings.name != '/groupdetail') {
      _handleRouteChange(route.settings.name);
    }
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPop(route, previousRoute);
    if (previousRoute is! DialogRoute) {
      _handleRouteChange(previousRoute?.settings.name);
    }
  }

  @override
  void didRemove(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didRemove(route, previousRoute);
    if (previousRoute is! DialogRoute) {
      _handleRouteChange(previousRoute?.settings.name);
    }
  }

  @override
  void didReplace({Route<dynamic>? newRoute, Route<dynamic>? oldRoute}) {
    super.didReplace(newRoute: newRoute, oldRoute: oldRoute);
    if (newRoute is! DialogRoute) {
      _handleRouteChange(newRoute?.settings.name);
    }
  }

  void _handleRouteChange(String? routeName) {
    if (routeName == '/home') {
      AudioManager().play('audio/home.mp3');
    } else if (routeName == '/goal') {
      AudioManager().play('audio/goal.mp3');
    } else if (routeName == '/ranking') {
      AudioManager().play('audio/ranking.mp3');
    } else if (routeName == '/characterinfo') {
      AudioManager().play('audio/character.mp3');
    } else if (routeName == '/gacha') {
      AudioManager().play('audio/gacha.mp3');
    } else if (routeName == '/battleready') {
      AudioManager().play('audio/battleReady.mp3');
    } else if (routeName == '/group') {
      AudioManager().play('audio/group.mp3');
    } else if (routeName == '/record') {
      AudioManager().play('audio/record.mp3');
    } else {
      AudioManager().stop();
    }
  }
}
