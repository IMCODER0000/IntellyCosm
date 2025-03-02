package gachonproject.mobile.api;

import gachonproject.mobile.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenAIController {
    
    private final OpenAiService openAiService;

    @GetMapping("/api/completion")
    public String getCompletion(@RequestParam String prompt) {
        return openAiService.getCompletion(prompt);
    }
}
