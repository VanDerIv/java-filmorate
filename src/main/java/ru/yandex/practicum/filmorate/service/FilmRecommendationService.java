package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Getter
public class FilmRecommendationService {

    private final FilmService filmService;
    private final UserService userService;

    private Map<Integer, Map<Integer, Double>> diff = new HashMap<>();
    private Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();
    private Map<Integer, HashMap<Integer, Double>> data = new HashMap<>();
    private Map<Integer, HashMap<Integer, Double>> outputData = new HashMap<>();
    private List<Integer> films;

    public FilmRecommendationService(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
        films = filmService.getFilmes().stream().map(f -> Long.valueOf(f.getId()).intValue()).collect(Collectors.toList());
    }

    public List<Film> getFilmsRecommendationsForUser(long userId) {
        calculateRanking();
        buildDifferencesMatrix(data);
        Map<Integer, Double> filmRecommendation = getFilmRecommendation((int) userId, data);
        return null;
    }

    public void setLike(int id_film, int id_user) {
        filmService.setLike(filmService.getFilm((long) id_film),userService.getUser((long) id_user));
    }

    private void buildDifferencesMatrix(Map<Integer, HashMap<Integer, Double>> data) {
        for (HashMap<Integer, Double> user : data.values()) {
            for (Map.Entry<Integer, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Integer, Double>());
                    freq.put(e.getKey(), new HashMap<Integer, Integer>());
                }
                for (Map.Entry<Integer, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i).doubleValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    private Map<Integer, Double> getFilmRecommendation(int userId, Map<Integer, HashMap<Integer, Double>> data) {
        HashMap<Integer, Double> uPred = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> uFreq = new HashMap<Integer, Integer>();
        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Map.Entry<Integer, HashMap<Integer, Double>> e : data.entrySet()) {
            for (Integer j : e.getValue().keySet()) {
                for (Integer k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j).doubleValue() + e.getValue().get(j).doubleValue();
                        double finalValue = predictedValue * freq.get(k).get(j).intValue();
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
                    } catch (NullPointerException e1) {
                    }
                }
            }
            HashMap<Integer, Double> clean = new HashMap<Integer, Double>();
            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j).doubleValue() / uFreq.get(j).intValue());
                }
            }
            for (Integer j : films) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
        return outputData.get(userId);
    }

    private void calculateRanking() {
        for (User user: userService.getUsers()) {
            Integer userId = (int) user.getId();
            data.put(userId, new HashMap<>());
            for (Film film: filmService.getFilmes()) {
                Double rank;
                if (film.getLikes().contains((long) userId)) {
                    rank = 1.0;
                } else {
                    rank = 0.0;
                }
                Integer filmId = (int) film.getId();
                data.get(userId).put(filmId,rank);
            }
        }
    }
}
