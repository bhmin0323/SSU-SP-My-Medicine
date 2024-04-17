package SSU.MyMedicine.DAO;

import SSU.MyMedicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    public boolean existsByMedName(String medName);
    public Medicine findByMedName(String medName);
}
