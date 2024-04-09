package SSU.MyMedicine.VO;

import SSU.MyMedicine.entity.Allergic;
import SSU.MyMedicine.entity.User;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GetUserInfoVO {
    private Integer uid;
    private String name;
    private List<Allergic> allergic;

    public void UserEntityToVO(User user){
        this.uid = user.getUid();
        this.name = user.getName();
        this.allergic = user.getAllergicList();
    }
}
