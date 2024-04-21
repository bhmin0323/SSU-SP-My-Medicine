package SSU.MyMedicine.DAO;

import SSU.MyMedicine.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    public Prescription findByPid(Integer pID);
}
