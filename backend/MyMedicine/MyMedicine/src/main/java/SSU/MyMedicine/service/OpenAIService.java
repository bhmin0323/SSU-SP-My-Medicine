package SSU.MyMedicine.service;

import SSU.MyMedicine.OpenAI.Message;
import SSU.MyMedicine.OpenAI.OpenAIRequest;
import SSU.MyMedicine.OpenAI.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenAIService {
    @Value("${openai.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;

    public OpenAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String runAPI(String prompt){
        OpenAIRequest request = new OpenAIRequest(
                "gpt-3.5-turbo",
                List.of(new Message("user", prompt)),
                1,
                0,
                100);
        OpenAIResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", request, OpenAIResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
