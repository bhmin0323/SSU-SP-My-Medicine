import 'dart:convert';
import 'dart:developer';
import 'dart:typed_data';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:medicineapp/models/prescription_list_model.dart';
import 'package:medicineapp/models/prescription_model.dart';
import 'package:medicineapp/models/user_model.dart';
import 'package:medicineapp/widgets/toast.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'dart:io';

class ApiService {
  static const String baseUrl = '43.200.168.39:8080';
  late http.Client httpClient;
  ApiService() {
    httpClient = http.Client();
  }
  //access 토큰
  late String accessHeaderValue;

  // GoogleSignIn 인스턴스 생성
  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: ['email'],
  );

  // 핑
  Future<int> pingServer() async {
    final url = Uri.http(baseUrl, '/status');
    final response = await http.get(url);
    log("/status: <${response.statusCode}>, <${response.body}>");
    if (response.statusCode != 204) {
      log('Server Response : ${response.statusCode}');
    }
    return response.statusCode;
  }

  //로그인
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
      log("accesstoken:${accessHeaderValue}");
      log("uID:${uID}");
      return int.parse(uID);
    } else if (response.statusCode == 401 || response.statusCode == 409) {
      log('Server Response : ${response.statusCode}');
      return -response.statusCode;
    } else {
      log('Server Response : ${response.statusCode}');
      return -1;
    }
  }

  //회원가입
  Future<int> signUp(
      String username, String password, List<String> allergies) async {
    final response = await http.post(
      Uri.parse('$baseUrl/signup'),
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

  // Google 로그인
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
          'access': '', // 액세스 토큰은 로컬 스토리지에서 가져올 것이므로 초기값은 빈 문자열
          HttpHeaders.cookieHeader: '', // 쿠키 헤더는 리프레시 토큰을 넣어주어야 함
        },
        body: jsonEncode(<String, String>{
          'idToken': googleAuth.idToken!,
          'accessToken': googleAuth.accessToken!,
        }),
      );

      if (response.statusCode == 200) {
        var data = jsonDecode(response.body);
        await _saveTokens(data['access'], data['refresh']);
      } else {
        throw Exception('Failed to login with Google');
      }
    } catch (e) {
      throw Exception('Failed to login with Google: $e');
    }
  }

  // 토큰 재발급
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
        HttpHeaders.cookieHeader: refreshToken, // 쿠키 헤더에 리프레시 토큰 추가
      },
      body: jsonEncode(<String, String>{
        'refresh': refreshToken,
      }),
    );

    if (response.statusCode == 200) {
      var data = jsonDecode(response.body);
      await _saveTokens(data['access'], data['refresh']);
    } else {
      throw Exception('Failed to reissue token');
    }
  }

  // 토큰 저장
  Future<void> _saveTokens(String accessToken, String refreshToken) async {
    final prefs = await SharedPreferences.getInstance();
    prefs.setString('access', accessToken);
    prefs.setString('refresh', refreshToken);
  }

  // 인증된 요청
  Future<http.Response> authenticatedRequest(String endpoint,
      {Map<String, String>? headers, dynamic body}) async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('access');
    if (accessToken == null) {
      throw Exception('Access token not found');
    }

    headers ??= {};
    headers['Authorization'] = 'Bearer $accessToken';

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

  // 사용자 정보 조회
  Future<UserModel> getUserInfo(int uid) async {
    final url = Uri.http(baseUrl, '/getUserInfo', {'uID': '$uid'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getUserInfo: <${response.statusCode}>, <${response.body}>");
    if (response.statusCode == 200) {
      final Map<String, dynamic> responseData =
          jsonDecode(utf8.decode(response.bodyBytes));
      final UserModel userData = UserModel.fromJson(responseData);
      return userData;
    }
    throw Exception('Failed to load user information');
  }

  // 처방전 리스트 조회
  Future<PrescListModel> getPrescList(int uid) async {
    final url = Uri.http(baseUrl, '/getPrescList', {'uID': '$uid'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getPrescList: <${response.statusCode}>, <${response.body}>");
    if (response.statusCode == 200) {
      final resData = jsonDecode(response.body);

      final prescData = PrescListModel.fromJson(resData);
      return prescData;
    }
    log("getPrescList Error: ${response.statusCode}");
    throw Error();
  }

  // 처방전 세부 조회
  Future<PrescModel> getPrescInfo(int prescId) async {
    final url = Uri.http(baseUrl, '/getPrescInfo', {'pID': '$prescId'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getPrescInfo: <${response.statusCode}>, <${utf8.decode(response.bodyBytes)}>");
    if (response.statusCode == 200) {
      final resData = jsonDecode(utf8.decode(response.bodyBytes));

      final prescData = PrescModel.fromJson(resData);

      return prescData;
    }
    log("getPrescInfo Error: ${response.statusCode}");
    throw Error();
  }

  // 처방전 이미지 조회
  Future<Uint8List> getPrescPic(int prescId) async {
    final url = Uri.http(baseUrl, '/getPrescPic', {'pID': '$prescId'});
    final response = await http.get(
      url,
      headers: {'access': accessHeaderValue},
    );
    log("/getPrescPic: <${response.statusCode}>, ${response.body.length}");
    if (response.statusCode == 200) {
      Uint8List resData = base64Decode(response.body);
      return resData;
    }
    log("getPrescPic Error: ${response.statusCode}");
    return Uint8List(0);
  }

  // 이미지 업로드
  Future<int> uploadImage(int uid, String regDate, int duration,
      List<String> medList, Uint8List image) async {
    final url = Uri.http(baseUrl, '/newPresc');

    var request = http.MultipartRequest('POST', url);

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
    var response = await request.send();
    log(" Error: ${response.statusCode}");
    if (response.statusCode == 200) {
      final respStr = await response.stream.bytesToString();
      final pID = int.parse(respStr.trim());
      return pID;
    } else {
      return -1;
    }
  }

  // 처방전 삭제
  Future<void> deletePrescription(int prescriptionId) async {
    try {
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
}
