import 'package:flutter/material.dart';
import 'package:medicineapp/screens/presc_list_screen.dart';
import 'package:medicineapp/screens/signup_screen.dart';
import 'package:medicineapp/services/api_services.dart';
import 'package:medicineapp/models/user_model.dart';
import 'package:persistent_bottom_nav_bar/persistent_tab_view.dart';

class UserInfoScreen extends StatefulWidget {
  final int uid;
  final Function func;

  UserInfoScreen({
    Key? key,
    required this.uid,
    required this.func,
  }) : super(key: key);

  @override
  _UserInfoScreenState createState() => _UserInfoScreenState();
}

class _UserInfoScreenState extends State<UserInfoScreen> {
  late Future<UserModel> _userInfoFuture;
  final ApiService _apiService = ApiService();
  List<String> selectedAllergies = [];
  List<String> allAllergies = [
    "페니실린",
    "집먼지진드기",
    "계란",
    "우유",
    "복숭아",
    "견과류",
    "꽃가루",
  ];

  @override
  void initState() {
    super.initState();
    _userInfoFuture = _apiService.getUserInfo(widget.uid);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('My Page'),
        backgroundColor: Colors.white,
        elevation: 5,
        shadowColor: Colors.grey[300],
        actions: [
          // IconButton(
          //   onPressed: () {
          //     Navigator.pushReplacement(
          //       context,
          //       MaterialPageRoute(
          //         builder: (context) =>
          //             PrescListScreen(uid: widget.uid, func: widget.func),
          //       ),
          //     );
          //   },
          //   icon: Icon(Icons.arrow_back_sharp, color: Colors.grey[600]),
          // ),
        ],
      ),
      body: FutureBuilder<UserModel>(
        future: _userInfoFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(
              child: CircularProgressIndicator(),
            );
          } else if (snapshot.hasError) {
            return Center(
              child: Text('Error: ${snapshot.error}'),
            );
          } else {
            final user = snapshot.data!;
            return Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '알러지 정보',
                    style: TextStyle(
                      fontSize: 20,
                    ),
                  ),
                  SizedBox(height: 10),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: user.allergic.map((allergic) {
                      return Text(
                        '${allergic}',
                        style: TextStyle(fontSize: 16),
                      );
                    }).toList(),
                  ),
                  ElevatedButton(
                    onPressed: () async {
                      _showAllergiesDialog(context);
                      saveInput();
                      edit(context, user.uID, selectedAllergies);
                    },
                    child: Text('알러지 수정'),
                  )
                ],
              ),
            );
          }
        },
      ),
    );
  }

  void edit(
      BuildContext context, int uid, List<String> selectedAllergies) async {
    await _apiService.edituser(uid, selectedAllergies);
  }

  void _showAllergiesDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AllergySelectionDialog(
          allAllergies: allAllergies,
          selectedAllergies: selectedAllergies,
          onSelectionChanged: (selection) {
            setState(() {
              selectedAllergies = selection;
            });
          },
        );
      },
    );
  }

  TextEditingController otherAllergyController = TextEditingController();

  void saveInput() {
    String? otherAllergy = otherAllergyController.text;
    if (otherAllergy != null && otherAllergy.isNotEmpty) {
      selectedAllergies.add(otherAllergy);
    }
  }
}
