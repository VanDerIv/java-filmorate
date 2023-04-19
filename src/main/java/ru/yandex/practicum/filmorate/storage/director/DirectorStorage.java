package ru.yandex.practicum.filmorate.storage.director;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(long id);

    Set<Director> getAllFilmsDirectors(Long id);
}
