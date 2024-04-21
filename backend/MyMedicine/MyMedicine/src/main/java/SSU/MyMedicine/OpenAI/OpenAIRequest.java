package SSU.MyMedicine.OpenAI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class OpenAIRequest {
    private String model;
    private List<Message> messages;
    private int n;
    private double temperature;
    private int max_tokens;
}

