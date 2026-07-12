package pdfanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pdfanalyzer.dto.AnalysisResponse;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public AnalysisResponse analyzePdf(String pdfText) throws Exception {

        if (pdfText.length() > 12000) {
            pdfText = pdfText.substring(0, 12000);
        }

        String prompt = """
Analyze the following PDF.

Return ONLY valid JSON.

{
"documentType":"",
"title":"",
"authors":"",
"summary":"",
"keyTakeaway":""
}

PDF:

""" + pdfText;

        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "openrouter/free",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());

        String json =
                root.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();

        json = json.replace("```json", "");
        json = json.replace("```", "");
        json = json.trim();

        return mapper.readValue(json, AnalysisResponse.class);

    }
}