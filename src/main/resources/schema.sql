DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS users;

--USERS
CREATE TABLE IF NOT EXISTS users
(
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email character varying(100) NOT NULL,
    login character varying(255) NOT NULL,
    name character varying(255),
    birthday date,
    CONSTRAINT user_login_con UNIQUE (login),
    CONSTRAINT user_email_con UNIQUE (email)
);

CREATE UNIQUE INDEX IF NOT EXISTS ind_uniq_user_login
    ON users
        (login ASC NULLS LAST);

CREATE UNIQUE INDEX IF NOT EXISTS ind_uniq_user_email
    ON users
        (email ASC NULLS LAST);

--USER_FRIENDS
CREATE TABLE IF NOT EXISTS user_friends
(
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    friend_id bigint REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT friend_uniq_con UNIQUE (user_id, friend_id),
    CONSTRAINT user_not_friend_con CHECK (user_id != friend_id)
);

CREATE INDEX IF NOT EXISTS fki_fk_friend_friend_id
    ON user_friends
        (friend_id ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS fki_fk_friend_user_id
    ON user_friends
        (user_id ASC NULLS LAST);

CREATE UNIQUE INDEX IF NOT EXISTS user_friends_uniq_pk
    ON user_friends
        (user_id ASC NULLS LAST, friend_id ASC NULLS LAST);

--MPA
CREATE TABLE IF NOT EXISTS mpa
(
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name character varying(30)  NOT NULL,
    description character varying(4000)
);

--FILMS
CREATE TABLE IF NOT EXISTS films
(
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name character varying(255) NOT NULL, --название у фильмов может совпадать
    description character varying(4000),
    release_date date,
    duration smallint,
    mpa bigint REFERENCES mpa (id) ON DELETE RESTRICT
);

--FILM_LIKES
CREATE TABLE IF NOT EXISTS film_likes
(
    film_id bigint REFERENCES films (id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT like_unic_con UNIQUE (film_id, user_id)
);

CREATE INDEX IF NOT EXISTS fki_fk_like_film_id
    ON film_likes
        (film_id ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS fki_fk_like_user_id
    ON film_likes
        (user_id ASC NULLS LAST);

--GENRES
CREATE TABLE IF NOT EXISTS genres
(
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name character varying(255) NOT NULL
);

--FILM_GENRES
CREATE TABLE IF NOT EXISTS film_genres
(
    film_id bigint REFERENCES films (id) ON DELETE CASCADE,
    genre_id bigint REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT film_genre_uniq_con UNIQUE (film_id, genre_id)
);

CREATE INDEX IF NOT EXISTS fki_fk_film_genre_film_id
    ON film_genres
        (film_id ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS fki_fk_film_genre_genre_id
    ON film_genres
        (genre_id ASC NULLS LAST);