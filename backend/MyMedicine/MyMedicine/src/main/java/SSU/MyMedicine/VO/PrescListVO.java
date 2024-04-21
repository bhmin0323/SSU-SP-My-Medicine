package SSU.MyMedicine.VO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PrescListVO {
    List<Integer> pID;
    @JsonProperty("pID")
    public List<Integer> getpID(){
        return this.pID;
    }
    public PrescListVO(List<Integer> pID){
        this.pID = pID;
    }
}
