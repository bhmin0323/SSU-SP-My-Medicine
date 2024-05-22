import 'dart:developer';
import 'package:flutter/material.dart';
import 'package:medicineapp/models/prescription_list_model.dart';
import 'package:medicineapp/models/user_model.dart';
import 'package:medicineapp/screens/login_screen.dart';
import 'package:medicineapp/ui_consts.dart';
import 'package:medicineapp/services/api_services.dart';
import 'package:medicineapp/widgets/prescription_widget.dart';
import 'package:restart_app/restart_app.dart';

class PrescListScreen extends StatefulWidget {
  final int uid;
  Function func;

  PrescListScreen({
    super.key,
    required this.uid,
    required this.func,
  });

  @override
  State<PrescListScreen> createState() => _PrescListScreenState();
}

class _PrescListScreenState extends State<PrescListScreen> {
  final ApiService apiService = ApiService();
  late Future<List<dynamic>> futureData;

  @override
  void initState() {
    super.initState();
    futureData = fetchData();
  }

  Future<List<dynamic>> fetchData() {
    return Future.wait([
      apiService.pingServer(),
      apiService.getPrescList(widget.uid),
    ]);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xfff2f2fe),
      appBar: AppBar(
        title: Container(
          padding: const EdgeInsets.only(
            left: 12,
            right: 0,
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Image.asset(
                'assets/logos/MM_logo_white.png',
                width: 110,
              ),
              Row(
                children: [
                  IconButton(
                      onPressed: () {
                        setState(() {
                          futureData = fetchData();
                        });
                      },
                      icon: const Icon(Icons.refresh,
                          color: Colors.white, size: 32)),
                  const Icon(Icons.notifications,
                      color: Colors.white, size: 32),
                  IconButton(
                      onPressed: () {
                        Restart.restartApp();
                      },
                      icon: const Icon(Icons.logout,
                          color: Colors.white, size: 32)),
                ],
              ),
            ],
          ),
        ),
        centerTitle: true,
        backgroundColor: Colors.deepPurple[200],
        elevation: 5,
        shadowColor: Colors.grey[300],
      ),
      body: Column(
        children: [
          Expanded(
            child: FutureBuilder<List<dynamic>>(
              future: futureData,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(
                    child: CircularProgressIndicator(),
                  );
                } else if (snapshot.hasError) {
                  return Center(
                    child: Text('처방전 정보가 없습니다.'),
                  );
                } else {
                  final responseData = snapshot.data;
                  if (responseData != null) {
                    final serverStatusCode = responseData[0];
                    final prescriptionList = responseData[1];
                    if (serverStatusCode != 204) {
                      return const Center(
                        child: Text('Server status error'),
                      );
                    } else {
                      log("presc_list_screen: ${prescriptionList.toString()}");
                      log("presc_list_screen: ${prescriptionList.prescIdList.toString()}");
                      if (prescriptionList == null ||
                          prescriptionList.prescIdList[0] == null) {
                        return const Center(
                          child: Text('No data'),
                        );
                      } else {
                        return ListView.builder(
                          itemCount: prescriptionList.length,
                          itemBuilder: (context, index) {
                            return PrescWidget(
                              index: index,
                              uid: widget.uid,
                              prescId: prescriptionList.prescIdList[
                                  prescriptionList.length - index - 1],
                            );
                          },
                        );
                      }
                    }
                  } else {
                    return const Center(
                      child: Text('No data'),
                    );
                  }
                }
              },
            ),
          ),
        ],
      ),
    );
  }
}
