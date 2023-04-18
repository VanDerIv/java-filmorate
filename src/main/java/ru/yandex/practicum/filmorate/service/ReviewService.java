package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private static final Integer ROW_COUNT = 10;
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review createReview(Review review) {
        if (review.getReviewId() != 0) {
            throw new ValidationException("Поле id НЕ должно быть задано для нового ревью");
        }
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        if (review.getReviewId() == 0) {
            throw new ValidationException("Поле id должно быть задано");
        }
        Review oldReview = getReview(review.getReviewId());
        review.setFilmId(oldReview.getFilmId());
        review.setUserId(oldReview.getUserId());
        return reviewStorage.updateReview(review);
    }

    public Review getReview(Long id) {
        Optional<Review> review = reviewStorage.getReview(id);
        return review.orElseThrow(() -> new NotFoundException(String.format("Ревью %d не найдено", id)));
    }

    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviews(Optional<Film> film, Optional<Integer> count) {
        return reviewStorage.getReviews(film, count.orElse(ROW_COUNT));
    }

    public void setLike(Review review, User user) {
        reviewStorage.removeScore(review, user);
        reviewStorage.setScore(review, user, 1);
    }

    public void setDislike(Review review, User user) {
        reviewStorage.removeScore(review, user);
        reviewStorage.setScore(review, user, -1);
    }

    public void removeLike(Review review, User user) {
        reviewStorage.removeScore(review, user);
    }

    public void removeDislike(Review review, User user) {
        reviewStorage.removeScore(review, user);
    }

}
