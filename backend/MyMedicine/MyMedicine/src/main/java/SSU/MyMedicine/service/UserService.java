package SSU.MyMedicine.service;

import SSU.MyMedicine.DAO.UserRepository;
import SSU.MyMedicine.VO.GetUserInfoVO;
import SSU.MyMedicine.VO.LoginVO;
import SSU.MyMedicine.VO.UserVO;
import SSU.MyMedicine.entity.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

//    public User findById(long pid) {
//        Optional<User> result = userRepository.findById(pid);
//        User userEntity = null;
//        if (result.isPresent()) {
//            userEntity = result.get();
//        } else {
//            throw new RuntimeException("Did not find user id : " + pid);
//        }
//        return null;
//    }

    public User buildUserFromVO(UserVO user) {

        if (userRepository.existsByName(user.getUsername())) {
            throw new EntityExistsException("Member with name '" + user.getUsername() + "' already exists.");
        }

        User userEntity = User.builder()
                .name(user.getUsername())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .build();

        return userEntity;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Boolean existByName(String name) {
        if (userRepository.existsByName(name))
            return true;
        return false;
    }

    public User findByName(String username) {
        return userRepository.findByName(username);
    }

    public User findByUid(Integer uid) {
        User findUser = userRepository.findByUid(uid);
        if (findUser == null) {
            throw new EntityNotFoundException("Entity not found with uid : " + uid);
        }

        return findUser;
    }

    public boolean authUser(LoginVO user) {
        boolean login = bCryptPasswordEncoder.matches(user.getPassword(),
                userRepository.findByName(user.getUsername()).getPassword());
        return login;
    }

//    public List<TodoDTO> findTodoByEmail(String email) {
//        UserEntity user = userRepository.findByEmail(email);
//        if (user == null)
//            throw new EntityNotFoundException("Entity not found with email : " + email);
//
//        List<TodoEntity> todoList = user.getTodoEntityList();
//        if (todoList.isEmpty())
//            throw new RuntimeException("No todos found with email : " + email);
//
//        List<TodoDTO> todoDTOS = new ArrayList<>();
//        for (TodoEntity todoEntity : todoList) {
//            TodoDTO todoDTO = todoEntity.toTodoDTO();
//            todoDTOS.add(todoDTO);
//        }
//        return todoDTOS;
//    }
}
