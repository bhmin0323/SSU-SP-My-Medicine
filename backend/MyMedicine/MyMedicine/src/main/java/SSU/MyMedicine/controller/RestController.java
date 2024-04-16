package SSU.MyMedicine.controller;

import SSU.MyMedicine.VO.GetUserInfoVO;
import SSU.MyMedicine.VO.LoginVO;
import SSU.MyMedicine.VO.UserVO;
import SSU.MyMedicine.entity.Allergic;
import SSU.MyMedicine.entity.User;
import SSU.MyMedicine.service.AllergicService;
import SSU.MyMedicine.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/signup")
    public String signup(@RequestBody UserVO userVO) {
        if (userService.existByName(userVO.getUsername()))
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username Already Exist");

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
            return  findUser.getUid();
        else
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Incorrect password");
    }

    @GetMapping("/getUserInfo")
    public GetUserInfoVO getAllergic(@RequestParam("uID") Integer uid){
        User foundUser = userService.findByUid(uid);
        GetUserInfoVO user = new GetUserInfoVO();
        user.UserEntityToVO(foundUser);
        return  user;
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
    public ResponseEntity<String> entityNotFoundExceptionHandler(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
