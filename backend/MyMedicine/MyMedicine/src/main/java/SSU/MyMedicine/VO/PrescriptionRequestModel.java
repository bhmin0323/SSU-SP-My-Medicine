package SSU.MyMedicine.VO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class PrescriptionRequestModel {
    private MultipartFile image;
    private Integer uID;
    private LocalDate regDate;
    private Integer duration;
    private List<String> medList;
}
