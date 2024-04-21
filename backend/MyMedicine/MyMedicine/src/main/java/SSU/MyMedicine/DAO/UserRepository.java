package SSU.MyMedicine.DAO;

import SSU.MyMedicine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public boolean existsByName(String name);
    public User findByName(String name);
    public User findByUid(Integer uid);

}
