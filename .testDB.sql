drop database if exists sokontokoro_game;

create database sokontokoro_game character set utf8;

CREATE TABLE sokontokoro_game.score (
	game_name CHAR(10) NOT NULL, 
	user_id int NOT NULL, 
	point int NOT NULL, 
	create_date DATETIME NOT NULL, 
	update_date DATETIME NOT NULL, 
	final_date DATETIME NOT NULL, 
	count int NOT NULL, 
	PRIMARY KEY(game_name, user_id));


CREATE TABLE sokontokoro_game.user (
	id int NOT NULL KEY, 
	name CHAR(16) NOT NULL, 
	create_date DATETIME NOT NULL, 
	update_date DATETIME NULL, 
	deleted BOOLEAN NULL DEFAULT false, 
	admin BOOLEAN NULL DEFAULT false);
