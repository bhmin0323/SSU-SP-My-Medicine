package SSU.MyMedicine.VO;

import SSU.MyMedicine.entity.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserVO {
    private String username;
    private String password;
    private List<String> allergicList = new ArrayList<>();

}
