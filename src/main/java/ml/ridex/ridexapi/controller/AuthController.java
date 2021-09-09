package ml.ridex.ridexapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("passenger/login")
    public String passengerLogin() {
        System.out.println("It's working");
        return "Tada";
    }
}