package SSU.MyMedicine.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class PrescListVO {
    List<Integer> pID;
    public PrescListVO(List<Integer> pid){
        this.pID = pid;
    }
}
