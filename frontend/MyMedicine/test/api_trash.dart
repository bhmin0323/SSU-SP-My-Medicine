//로그인
  // Future<int> login(String loginId, String password) async {
  //   final url = Uri.http(baseUrl, '/login');
  //   final Map<String, dynamic> loginData = {
  //     "username": loginId,
  //     "password": password
  //   };
  //   final response = await http.post(
  //     url,
  //     body: jsonEncode(loginData),
  //     headers: {'Content-Type': 'application/json'},
  //   );
  //   log("/login: REQ: $url");
  //   log("/login: <${response.statusCode}>, <${response.body}>");
  //   if (response.statusCode == 200) {
  //     return int.parse(response.body);
  //   } else if (response.statusCode == 401 || response.statusCode == 409) {
  //     log('Server Response : ${response.statusCode}');
  //     return -response.statusCode;
  //   } else {
  //     log('Server Response : ${response.statusCode}');
  //     return -1;
  //   }
  // }
    // 회원가입
  // Future<int> signUp(String username, String password, List allergyInfo) async {
  //   final url = Uri.http(baseUrl, '/signup');
  //   final Map<String, dynamic> userData = {
  //     "username": username,
  //     "password": password,
  //     "allergicList": allergyInfo,
  //   };

  //   final response = await http.post(
  //     url,
  //     body: jsonEncode(userData),
  //     headers: {'Content-Type': 'application/json'},
  //   );

  //   log("/signup: REQ: $userData");
  //   log("/signup: <${response.statusCode}>, <${response.body}>");

  //   if (response.statusCode == 200) {
  //     return 200;
  //   } else if (response.statusCode == 409) {
  //     return -409;
  //   } else {
  //     return -1;
  //   }
  // }