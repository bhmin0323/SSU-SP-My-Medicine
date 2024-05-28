import 'dart:convert' show base64Decode, jsonDecode, jsonEncode, utf8;
import 'dart:developer';
import 'dart:typed_data';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:medicineapp/models/prescription_list_model.dart';
import 'package:medicineapp/models/prescription_model.dart';
import 'package:medicineapp/models/user_model.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'dart:io';

class ApiService {
  static const String baseUrl = '43.200.168.39:8080';
  late http.Client httpClient;
  late String accessHeaderValue;
  final GoogleSignIn _googleSignIn = GoogleSignIn(scopes: ['email']);

  // 싱글톤 패턴 적용을 위한 인스턴스
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal() {
    httpClient = http.Client();
  }

// 액세스 토큰 초기화
  Future<void> _initializeAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    accessHeaderValue = prefs.getString('access') ?? '';
    if (accessHeaderValue.isEmpty) {
      throw Exception('Access token not found');
    }
  }

  // 서버 상태 확인
  Future<int> pingServer() async {
    await _ensureAccessTokenInitialized();
    final url = Uri.http(baseUrl, '/status');
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/status: <${response.statusCode}>, <${response.body}>");
    if (response.statusCode != 204) {
      log('Server Response : ${response.statusCode}');
    }
    return response.statusCode;
  }

// 로그인
  Future<int> login(String loginId, String password) async {
    final url = Uri.http(baseUrl, '/login');
    final Map<String, dynamic> loginData = {
      "username": loginId,
      "password": password
    };
    final response = await http.post(
      url,
      body: jsonEncode(loginData),
      headers: {'Content-Type': 'application/json'},
    );
    log("${loginData}");
    log("/login: REQ: $url");
    log("/login: <${response.statusCode}>, <${response.headers}>");
    if (response.statusCode == 200 || response.statusCode == 404) {
      accessHeaderValue = response.headers['access']!;
      String uID = response.headers['uid']!;
      log("/login api: accesstoken:${accessHeaderValue}");
      log("/login api: uID:${uID}");
      await _saveTokens(response.headers['set-cookie']!);
      await _saveAccessToken(accessHeaderValue); // Access 토큰 저장
      return int.parse(uID);
    } else if (response.statusCode == 401 || response.statusCode == 409) {
      log('/login api: Server Response : ${response.statusCode}');
      return -response.statusCode;
    } else {
      log('/login api: Server Response : ${response.statusCode}');
      return -1;
    }
  }

  // 회원가입
  Future<int> signUp(
      String username, String password, List<String> allergies) async {
    final url = Uri.http(baseUrl, '/signup');
    final response = await http.post(
      url,
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: jsonEncode(<String, dynamic>{
        'username': username,
        'password': password,
        'allergies': allergies,
      }),
    );

    if (response.statusCode == 200) {
      return 200;
    } else if (response.statusCode == 409) {
      return -409;
    } else {
      return -1;
    }
  }

  // 구글 로그인
  Future<void> googleLogin() async {
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        throw Exception('Google sign in was aborted.');
      }

      final GoogleSignInAuthentication googleAuth =
          await googleUser.authentication;

      final response = await http.post(
        Uri.parse('$baseUrl/oauth2/authorization/google'),
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
          'access': '',
          HttpHeaders.cookieHeader: '',
        },
        body: jsonEncode(<String, String>{
          'idToken': googleAuth.idToken!,
          'accessToken': googleAuth.accessToken!,
        }),
      );

      if (response.statusCode == 200) {
        var data = jsonDecode(response.body);
        await _saveTokens(data['refresh']);
      } else {
        throw Exception('Failed to login with Google');
      }
    } catch (e) {
      throw Exception('Failed to login with Google: $e');
    }
  }

  // 토큰 갱신
  Future<void> reissueToken() async {
    final prefs = await SharedPreferences.getInstance();
    final refreshToken = prefs.getString('refresh');

    if (refreshToken == null) {
      throw Exception('Refresh token not found');
    }

    final response = await http.post(
      Uri.parse('$baseUrl/reissue'),
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
        HttpHeaders.cookieHeader: refreshToken,
      },
      body: jsonEncode(<String, String>{
        'refresh': refreshToken,
      }),
    );

    if (response.statusCode == 200) {
      var data = jsonDecode(response.body);
      await _saveTokens(data['refresh']);
    } else {
      throw Exception('Failed to reissue token');
    }
  }

// 토큰 저장
  Future<void> _saveTokens(String refreshToken) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('refresh', refreshToken);
  }

// 액세스 토큰 저장
  Future<void> _saveAccessToken(String accessToken) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('access', accessToken);
  }

  // 인증된 요청
  Future<http.Response> authenticatedRequest(String endpoint,
      {Map<String, String>? headers, dynamic body}) async {
    await _ensureAccessTokenInitialized();

    headers ??= {};
    headers['Authorization'] = 'Bearer $accessHeaderValue';

    final response = await http.post(
      Uri.parse('$baseUrl/$endpoint'),
      headers: headers,
      body: body != null ? jsonEncode(body) : null,
    );

    if (response.statusCode == 401) {
      await reissueToken();
      return authenticatedRequest(endpoint, headers: headers, body: body);
    }

    return response;
  }

  // 사용자 정보 가져오기
  Future<UserModel> getUserInfo(int uid) async {
    await _ensureAccessTokenInitialized();
    final url = Uri.http(baseUrl, '/getUserInfo', {'uID': '$uid'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getUserInfo api: <${response.statusCode}>, <${response.body}>");
    if (response.statusCode == 200) {
      final Map<String, dynamic> responseData =
          jsonDecode(utf8.decode(response.bodyBytes));
      final UserModel userData = UserModel.fromJson(responseData);
      return userData;
    }
    throw Exception('Failed to load user information');
  }

  // 처방 목록 가져오기
  Future<PrescListModel> getPrescList(int uid) async {
    await _ensureAccessTokenInitialized();
    final url = Uri.http(baseUrl, '/getPrescList', {'uID': '$uid'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getPrescList api: <${response.statusCode}>, <${response.body}>");
    if (response.statusCode == 200) {
      final resData = jsonDecode(response.body);
      final prescData = PrescListModel.fromJson(resData);
      return prescData;
    }
    log("getPrescList api Error: ${response.statusCode}");
    throw Error();
  }

  // 처방 정보 가져오기
  Future<PrescModel> getPrescInfo(int prescId) async {
    await _ensureAccessTokenInitialized();
    final url = Uri.http(baseUrl, '/getPrescInfo', {'pID': '$prescId'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getPrescInfo api: <${response.statusCode}>, <${utf8.decode(response.bodyBytes)}>");
    if (response.statusCode == 200) {
      final resData = jsonDecode(utf8.decode(response.bodyBytes));
      final prescData = PrescModel.fromJson(resData);
      return prescData;
    }
    log("getPrescInfo api Error: ${response.statusCode}");
    throw Error();
  }

  // 처방 사진 가져오기
  Future<Uint8List> getPrescPic(int prescId) async {
    await _initializeAccessToken();
    final url = Uri.http(baseUrl, '/getPrescPic', {'pID': '$prescId'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log('/getimage api body1: ${response.body}');
    log('/getimage api reponse: ${response.statusCode}');
    if (response.statusCode == 200) {
      try {
        log('decode try');
        Uint8List resData = base64Decode(response.body);
        log('/getimage api body: success');
        return resData;
      } catch (e) {
        log('Error decoding image: $e');
        return Uint8List(0);
      }
    } else {
      log("Failed to fetch image, status code: ${response.statusCode}");
      return Uint8List(0);
    }
  }

  // 이미지 업로드
  Future<int> uploadImage(int uid, String regDate, int duration,
      List<String> medList, Uint8List image) async {
    await _initializeAccessToken();
    final url = Uri.http(baseUrl, '/newPresc');
    var request = http.MultipartRequest(
      'POST',
      url,
    );

    request.files.add(http.MultipartFile.fromBytes(
      'image',
      image,
      filename: 'upload.jpg',
      contentType: MediaType('image', 'jpg'),
    ));
    request.fields['uID'] = uid.toString();
    request.fields['regDate'] = regDate;
    request.fields['duration'] = duration.toString();
    request.fields['medList'] = medList.join(',');
    request.headers['access'] = accessHeaderValue;
    log('/uploadImage api: ${request.fields}');
    log('/uploadImage api: ${request.headers}');
    var response = await request.send();
    log("/uploadImage api statusCode: ${response.statusCode}");
    if (response.statusCode == 200) {
      final respStr = await response.stream.bytesToString();
      final pID = int.parse(respStr.trim());
      return pID;
    } else {
      return -1;
    }
  }

  // 처방 삭제
  Future<void> deletePrescription(int prescriptionId) async {
    try {
      await _initializeAccessToken();
      final url = Uri.http(baseUrl, '/delPresc', {'pID': '$prescriptionId'});
      final response = await http.delete(
        url,
        headers: {'access': accessHeaderValue},
      );
      if (response.statusCode == 200) {
        log("처방전이 성공적으로 삭제되었습니다.");
        return;
      } else {
        log("처방전 삭제에 실패했습니다. 상태 코드: ${response.statusCode}");
        throw Exception('처방전 삭제에 실패했습니다.');
      }
    } catch (e) {
      log("처방전 삭제 중 오류 발생: $e");
      throw e;
    }
  }

// 액세스 토큰 초기화 확인
  Future<void> _ensureAccessTokenInitialized() async {
    if (!isAccessTokenInitialized()) {
      await _initializeAccessToken();
    }
  }

// 액세스 토큰 초기화 여부 확인
  bool isAccessTokenInitialized() {
    return accessHeaderValue.isNotEmpty;
  }
}
