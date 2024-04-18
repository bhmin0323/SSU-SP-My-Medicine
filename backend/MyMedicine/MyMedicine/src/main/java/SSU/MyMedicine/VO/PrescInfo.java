package SSU.MyMedicine.VO;

import SSU.MyMedicine.entity.Medicine;
import SSU.MyMedicine.entity.Prescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrescInfo {
    private LocalDate regDate;
    private Integer duration;
    private List<MedicineVO> medicine = new ArrayList<>();

    public PrescInfo(Prescription prescription){
        this.regDate = prescription.getRegDate();
        this.duration = prescription.getDuration();
        List<Medicine> medicineList = prescription.getMedList();
        for (Medicine med : medicineList){
            MedicineVO medicineVO = MedicineVO.builder()
                    .medName(med.getMedName())
                    .medComp(med.getMedComp())
                    .build();
            this.medicine.add(medicineVO);
        }
    }
}
