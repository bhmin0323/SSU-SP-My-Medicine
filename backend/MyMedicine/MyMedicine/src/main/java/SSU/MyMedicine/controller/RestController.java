package SSU.MyMedicine.controller;

import SSU.MyMedicine.VO.*;
import SSU.MyMedicine.entity.Allergic;
import SSU.MyMedicine.entity.Medicine;
import SSU.MyMedicine.entity.Prescription;
import SSU.MyMedicine.entity.User;
import SSU.MyMedicine.service.AllergicService;
import SSU.MyMedicine.service.MedicineService;
import SSU.MyMedicine.service.PrescriptionService;
import SSU.MyMedicine.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final UserService userService;
    private final AllergicService allergicService;
    private final PrescriptionService prescriptionService;

    @Autowired
    public RestController(UserService userService, AllergicService allergicService, PrescriptionService prescriptionService) {
        this.userService = userService;
        this.allergicService = allergicService;
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserVO userVO) {
        if (userService.existByName(userVO.getUsername()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username Already Exist");

        // allergy exists
        if (!userVO.getAllergicList().isEmpty())
            allergicService.saveIfNotThere(userVO.getAllergicList());

        List<Allergic> allergicList = new ArrayList<>();
        for (String allergicInfo : userVO.getAllergicList()) {
            Allergic addAllergic = allergicService.findByInfo(allergicInfo);
            allergicList.add(addAllergic);
        }

        User user = userService.buildUserFromVO(userVO);
        user.setAllergicList(allergicList);
        userService.save(user);

        return user.toString();
    }

    @PostMapping("/login")
    public Integer login(@RequestBody LoginVO user) {
        User findUser = userService.findByName(user.getUsername());
        if (findUser == null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username not found");

        if (userService.authUser(user))
            return findUser.getUid();
        else
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Incorrect password");
    }

    @GetMapping("/getUserInfo")
    public GetUserInfoVO getAllergic(@RequestParam("uID") Integer uid) {
        User foundUser = userService.findByUid(uid);
        GetUserInfoVO user = new GetUserInfoVO();
        user.UserEntityToVO(foundUser);
        return user;
    }

    @PostMapping(path = "/newPresc", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> savePresc(
            @RequestPart("image") MultipartFile file,
            @RequestPart("prescription") PrescriptionVO prescription) throws IOException {
        Prescription newPresc = prescriptionService.save(file, prescription);
        return ResponseEntity.ok(prescription.toString());
    }

    @GetMapping("/getPrescList")
    public ResponseEntity<PrescListVO> prescList(@RequestParam("uID") Integer uid){
        User user = userService.findByUid(uid);
        return ResponseEntity.ok(new PrescListVO(userService.getPrescFromUser(user)));
    }

    @DeleteMapping("/delPresc")
    public ResponseEntity<String> delPresc(@RequestParam("pID")Integer pid){
        Prescription prescription = prescriptionService.findByPid(pid);
        prescriptionService.delete(prescription);
        return ResponseEntity.ok("Prescription deleted with pid : " + pid);
    }

//    @GetMapping("/")
//    public String mainPage() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        return email;
//    }

    @GetMapping("/status")
    public ResponseEntity<String> alive() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);    //status 204
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> IOExceptionHandler(IOException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
