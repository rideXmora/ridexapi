package ml.ridex.ridexapi.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@PreAuthorize("hasAuthority('DRIVER')")
@RequestMapping("/api/passenger")
public class PassengerController {
    @GetMapping
    public String test(Principal principal) {
        System.out.println(principal.getName());
        return "Authed";
    }
}
