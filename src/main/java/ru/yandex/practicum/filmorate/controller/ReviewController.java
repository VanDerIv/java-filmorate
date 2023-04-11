package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, FilmService filmService, UserService userService) {
        this.reviewService = reviewService;
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable final Long id) {
        return reviewService.getReview(id);
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody final Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody final Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable final Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping()
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(required = false) Integer count) {
        Film film = filmId == null ? null : filmService.getFilm(filmId);
        return reviewService.getReviews(film, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable final Long id, @PathVariable final Long userId) {
        Review review = reviewService.getReview(id);
        User user = userService.getUser(userId);
        reviewService.setLike(review, user);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void setDislike(@PathVariable final Long id, @PathVariable final Long userId) {
        Review review = reviewService.getReview(id);
        User user = userService.getUser(userId);
        reviewService.setDislike(review, user);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable final Long id, @PathVariable final Long userId) {
        Review review = reviewService.getReview(id);
        User user = userService.getUser(userId);
        reviewService.removeLike(review, user);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable final Long id, @PathVariable final Long userId) {
        Review review = reviewService.getReview(id);
        User user = userService.getUser(userId);
        reviewService.removeDislike(review, user);
    }

}
