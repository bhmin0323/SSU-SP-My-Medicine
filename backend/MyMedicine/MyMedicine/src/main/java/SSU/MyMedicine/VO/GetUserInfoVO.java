package SSU.MyMedicine.VO;

import SSU.MyMedicine.entity.Allergic;
import SSU.MyMedicine.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GetUserInfoVO {
    private Integer uID;
    private String name;
    private List<Allergic> allergic;

    @JsonProperty("uID")
    public Integer getuID(){
        return this.uID;
    }
    public void UserEntityToVO(User user){
        this.uID = user.getUid();
        this.name = user.getName();
        this.allergic = user.getAllergicList();
    }
}
