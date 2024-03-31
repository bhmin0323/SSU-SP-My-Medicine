package SSU.MyMedicine.service;

import SSU.MyMedicine.DAO.UserRepository;
import SSU.MyMedicine.DTO.TodoDTO;
import SSU.MyMedicine.VO.UserVO;
import SSU.MyMedicine.entity.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
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

    public User findByName(String username){
        return userRepository.findByName(username);
    }

//    public void deleteById(long pid) {
//        userRepository.deleteById(pid);
//    }

//    public boolean signin(UserEntity user) {
//        UserEntity result = userRepository.findByEmail(user.getEmail());
//        if (result == null) {
//            return false;
//        } else {
//            if (result.getPassword().equals(user.getPassword()))
//                return true;
//            else return false;
//        }
//    }

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
