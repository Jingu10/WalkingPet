import 'dart:convert';

import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:http/http.dart' as http;
import 'package:walkingpet/services/Interceptor.dart';

Future<File> pickImage() async {
  final picker = ImagePicker();
  final pickedFile = await picker.pickImage(source: ImageSource.camera);
  if (pickedFile != null) {
    File image = File(pickedFile.path);
    return image;
  }
  return File('');
}

// Future<Void>는 반환 값이 없고, 완료까지 일정 시간이 소요될 수 있는 비동기 작업을 수행
Future<Map<String, dynamic>> uploadImage({
  required File imageFile,
  required int characterId,
  required double latitude,
  required double longitude,
}) async {
  final client = AuthInterceptor();
  final uri = Uri.parse('https://walkingpet.co.kr/record/upload');
  final request = http.MultipartRequest('POST', uri);

  // Add characterId, latitude, and longitude to the request
  request.fields['characterId'] = characterId.toString();
  request.fields['latitude'] = latitude.toString();
  request.fields['longitude'] = longitude.toString();

  // Determine the MIME type of the image file
  final mimeTypeData =
      lookupMimeType(imageFile.path, headerBytes: [0xFF, 0xD8])?.split('/');

  // Convert the image file to MultipartFile and add to the request
  final file = await http.MultipartFile.fromPath(
    'image',
    imageFile.path,
    contentType: MediaType(mimeTypeData![0], mimeTypeData[1]),
  );

  request.files.add(file);

  try {
    final streamedResponse = await client.send(request);
    final response = await http.Response.fromStream(streamedResponse);
    if (response.statusCode == 200) {
      final jsonData = jsonDecode(utf8.decode(response.bodyBytes));
      return jsonData;
    } else {
      throw Exception(
          'Failed to upload image. Status code: ${response.statusCode}');
    }
  } catch (e) {
    throw Exception('Failed to upload image. Error: $e');
  }
}
