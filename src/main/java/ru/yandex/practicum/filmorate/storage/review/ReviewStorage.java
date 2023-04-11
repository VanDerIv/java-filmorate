package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface ReviewStorage {
    Review getReview(Long id);

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long id);

    List<Review> getReviews(Film film, Integer count);

    void setScore(Review review, User user, Integer score);

    void removeScore(Review review, User user);
}
