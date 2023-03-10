package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	@Test
	public void testUserStorage() {
		List<User> users = userStorage.getUsers();
		assertEquals(0, users.size());

		User user = userStorage.getUser(1L);
		assertNull(user);

		User user1 = User.builder().id(1).name("Петя").login("Peta").email("peta@yandex.ru")
				.birthday(LocalDate.of(2003, 2, 1)).build();
		userStorage.createUser(user1);
		user = userStorage.getUser(1L);
		assertNotNull(user);
		assertThat(user).hasFieldOrPropertyWithValue("id", 1L);

		User user2 = User.builder().id(2).name("Саша").login("Roma").email("roma@yandex.ru")
				.birthday(LocalDate.of(2008, 4, 8)).build();
		userStorage.createUser(user2);
		users = userStorage.getUsers();
		assertEquals(2, users.size());

		user2.setName("Рома");
		userStorage.updateUser(user2);
		user = userStorage.getUser(2L);
		assertNotNull(user);
		assertThat(user).hasFieldOrPropertyWithValue("name", "Рома");

		userStorage.addUserToFriend(user1, user2);
		user = userStorage.getUser(1L);
		Set<Long> friends = user.getFriends();
		assertNotNull(friends);
		assertEquals(1, friends.size());
		assertEquals(2, friends.stream().findFirst().get());

		userStorage.removeUserFromFriend(user1, user2);
		user = userStorage.getUser(1L);
		friends = user.getFriends();
		assertNotNull(friends);
		assertEquals(0, friends.size());
	}

	@Test
	public void testFilmStorage() {
		List<Film> films = filmStorage.getFilmes();
		assertEquals(0, films.size());

		Film film = filmStorage.getFilm(1L);
		assertNull(film);

		Film film1 = Film.builder().id(1).name("Титаник").description("Красивый фильм о любви")
				.releaseDate(LocalDate.of(1997, 11, 1)).duration(194).build();
		filmStorage.createFilm(film1);
		film = filmStorage.getFilm(1L);
		assertNotNull(film);
		assertThat(film).hasFieldOrPropertyWithValue("id", 1L);

		Film film2 = Film.builder().id(2).name("Гладиатор").description("Много драк и крови")
				.releaseDate(LocalDate.of(2001, 2, 10)).duration(130).build();
		filmStorage.createFilm(film2);
		films = filmStorage.getFilmes();
		assertEquals(2, films.size());

		Mpa mpa = Mpa.builder().id(4).name("R").build();
		film2.setMpa(mpa);
		filmStorage.updateFilm(film2);
		film = filmStorage.getFilm(2L);
		assertNotNull(film);
		assertThat(film).hasFieldOrPropertyWithValue("mpa", mpa);

		User user1 = User.builder().id(1).name("Петя").login("Peta").email("peta@yandex.ru")
				.birthday(LocalDate.of(2003, 2, 1)).build();
		userStorage.createUser(user1);
		filmStorage.setLike(film1, user1);
		film = filmStorage.getFilm(1L);
		Set<Long> likes = film.getLikes();
		assertNotNull(likes);
		assertEquals(1, likes.size());
		assertEquals(1, likes.stream().findFirst().get());

		filmStorage.removeLike(film1, user1);
		film = filmStorage.getFilm(1L);
		likes = film.getLikes();
		assertNotNull(likes);
		assertEquals(0, likes.size());
	}

}
