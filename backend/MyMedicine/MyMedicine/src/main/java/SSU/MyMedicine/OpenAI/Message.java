package SSU.MyMedicine.OpenAI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Message {
    private String role;
    private String content;
}
