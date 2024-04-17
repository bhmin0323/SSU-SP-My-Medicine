package SSU.MyMedicine.service;

import SSU.MyMedicine.DAO.MedicineRepository;
import SSU.MyMedicine.entity.Medicine;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {
    final private MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository){
        this.medicineRepository = medicineRepository;
    }

    public boolean saveIfNotThere(List<String> medicineStrings){
        if (medicineStrings.isEmpty())
            return false;

        // save if entity does not exist
        for (String medicineStr : medicineStrings) {
            if(!medicineRepository.existsByMedName(medicineStr)){
                Medicine newAllergic = new Medicine().builder()
                        .medName(medicineStr)
                        .medComp(medicineStr)
                        .build();
                medicineRepository.save(newAllergic);
            }
        }
        return true;
    }

    public Medicine findByMedName(String medName){
        return medicineRepository.findByMedName(medName);
    }
}
