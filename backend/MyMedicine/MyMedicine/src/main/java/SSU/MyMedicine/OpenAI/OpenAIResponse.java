package SSU.MyMedicine.OpenAI;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenAIResponse {
    private List<Choice> choices;
    @Getter
    public static class Choice{
        private int index;
        private Message message;
    }
}
