package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.Optional;
import javax.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(long id) {
        Optional<Director> director = directorStorage.getDirectorById(id);
        return director.orElseThrow(() -> new NotFoundException(String.format("Режисёр %d не найден", id)));
    }

    public Director createDirector(Director director) {
        if (director.getId() != 0) {
            director.setId(0);
        }
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if (director.getId() == 0) {
            throw new ValidationException("Поле id должно быть задано");
        }
        getDirectorById(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirectorById(long id) {
        directorStorage.deleteDirectorById(id);
    }

}
