package SSU.MyMedicine.DAO;

import SSU.MyMedicine.entity.Allergic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergicRepository extends JpaRepository<Allergic, Integer> {
    public boolean existsByInfo(String info);
    public Allergic findByInfo(String info);
}
