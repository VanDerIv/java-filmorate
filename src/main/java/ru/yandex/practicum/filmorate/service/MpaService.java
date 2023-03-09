package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getRatings() {
        return mpaStorage.getMpa();
    }

    public Mpa getRating(Integer id) {
        Mpa rating = mpaStorage.getMpa(id);
        if (rating == null) throw new NotFoundException(String.format("Рейтинг %d не найден", id));
        return rating;
    }
}
