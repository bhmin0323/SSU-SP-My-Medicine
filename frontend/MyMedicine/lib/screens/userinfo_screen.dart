import 'package:flutter/material.dart';
import 'package:medicineapp/screens/presc_list_screen.dart';
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

  @override
  void initState() {
    super.initState();
    _userInfoFuture = _apiService.getUserInfo(widget.uid);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Container(
          padding: const EdgeInsets.only(
            bottom: 1,
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
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
              const Text('My Page'),
              const SizedBox(width: 30, height: 1),
            ],
          ),
        ),
        backgroundColor: Colors.white,
        elevation: 5,
        shadowColor: Colors.grey[300],
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
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('User ID: ${user.uID}'),
                  SizedBox(height: 10),
                  Text('Allergies:'),
                  Column(
                    children: user.allergic.map((allergic) {
                      return Text('${allergic.info}');
                    }).toList(),
                  ),
                ],
              ),
            );
          }
        },
      ),
    );
  }
}
