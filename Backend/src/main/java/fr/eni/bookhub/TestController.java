package fr.eni.bookhub;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TestController {


    @GetMapping("/api/me")
    public ResponseEntity<String> me(Authentication authentication) {
        return ResponseEntity.ok("Logged in as: " + authentication.getName());
    }


}
