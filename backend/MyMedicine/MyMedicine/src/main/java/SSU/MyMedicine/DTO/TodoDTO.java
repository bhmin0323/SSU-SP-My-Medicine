package SSU.MyMedicine.DTO;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TodoDTO {
    Long id;
    String title;
    String detail;
    String authorEmail;
    Boolean isDone;
}
