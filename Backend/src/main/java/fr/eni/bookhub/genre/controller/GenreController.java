package fr.eni.bookhub.genre.controller;

import fr.eni.bookhub.genre.dto.response.GenreResponse;
import fr.eni.bookhub.genre.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/genre")
public class GenreController implements GenreControllerApi {

    private final GenreService genreService;

    @GetMapping("/all")
    public ResponseEntity<List<GenreResponse>> getGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }


}
