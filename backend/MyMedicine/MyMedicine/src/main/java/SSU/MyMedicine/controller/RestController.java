package SSU.MyMedicine.controller;

import SSU.MyMedicine.VO.UserVO;
import SSU.MyMedicine.entity.Allergic;
import SSU.MyMedicine.entity.User;
import SSU.MyMedicine.service.AllergicService;
import SSU.MyMedicine.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final UserService userService;
    private final AllergicService allergicService;

    public RestController(UserService userService, AllergicService allergicService) {
        this.userService = userService;
        this.allergicService = allergicService;
    }

    @GetMapping("/hello")
    public String test() {
        return "Hello world";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserVO userVO) {
        if (userService.existByName(userVO.getUsername()))
            return "Username already exists.";

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
    public ResponseEntity<?> login(@RequestBody UserVO userVO) {
        return null;
    }

    @GetMapping("/getAllergic")
    public String getAllergic(String username){
        return userService.findByName(username).toString();
    }

    @GetMapping("/")
    public String mainPage() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email;
    }


    @GetMapping("/ping")
    public ResponseEntity<String> alive() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);    //status 204
    }
}
