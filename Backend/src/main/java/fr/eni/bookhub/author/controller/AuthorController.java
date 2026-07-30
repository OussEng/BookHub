package fr.eni.bookhub.author.controller;

import fr.eni.bookhub.author.dto.response.AuthorResponse;
import fr.eni.bookhub.author.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/all")
    public ResponseEntity<List<AuthorResponse>> getAllAuthors(){
        return ResponseEntity.ok(authorService.getAllAuthors());
    }



}
