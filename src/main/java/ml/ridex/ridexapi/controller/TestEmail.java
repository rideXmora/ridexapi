package ml.ridex.ridexapi.controller;

import ml.ridex.ridexapi.service.SendGridEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/email")
public class TestEmail {
    @Value("${ADMIN_EMAIL_ADDRESS}")
    private String adminEmailAddress;
    @Value("${EMAIL_ORG_REG_TEMPLATE_ID}")
    private String orgRegEmailTemplate;

    @Autowired
    private SendGridEmailService sendGridEmailService;

    @GetMapping
    public String emailTest() throws IOException {
        Map<String, String> dynamicData = new HashMap<>();
        dynamicData.put("org_name", "PickMe");
        dynamicData.put("password", "13243");
        sendGridEmailService.sendHTMLEmail(adminEmailAddress, "kavindasr@gmail.com", orgRegEmailTemplate, dynamicData);
        return "Email sent";
    }
}
