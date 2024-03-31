//package SSU.MyMedicine.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.stereotype.Service;
//
//
//import java.util.Optional;
//
//@Service
//public class TodoService {
//    private final TodoRepository todoRepository;
//
//    TodoService(TodoRepository todoRepository) {
//        this.todoRepository = todoRepository;
//    }
//
//    public TodoEntity save(TodoEntity todoEntity) {
//        todoRepository.save(todoEntity);
//        return todoEntity;
//    }
//
//    public boolean deleteTodo(Long id) {
//        try {
//            Optional<TodoEntity> targetTodo = todoRepository.findById(id);
//            todoRepository.deleteById(id);
//        } catch (EntityNotFoundException e) {
//            System.out.println("Entity not found with id : " + id);
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//}
