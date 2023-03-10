insert into MPA(ID, NAME, DESCRIPTION) values (1, 'G', 'у фильма нет возрастных ограничений');
insert into MPA(ID, NAME, DESCRIPTION) values (2, 'PG', 'детям рекомендуется смотреть фильм с родителями');
insert into MPA(ID, NAME, DESCRIPTION) values (3, 'PG-13', 'детям до 13 лет просмотр не желателен');
insert into MPA(ID, NAME, DESCRIPTION) values (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
insert into MPA(ID, NAME, DESCRIPTION) values (5, 'NC-17', 'лицам до 18 лет просмотр запрещён');

insert into GENRES(ID, NAME) values (1, 'Комедия');
insert into GENRES(ID, NAME) values (2, 'Драма');
insert into GENRES(ID, NAME) values (3, 'Мультфильм');
insert into GENRES(ID, NAME) values (4, 'Триллер');
insert into GENRES(ID, NAME) values (5, 'Документальный');
insert into GENRES(ID, NAME) values (6, 'Боевик');
commit;