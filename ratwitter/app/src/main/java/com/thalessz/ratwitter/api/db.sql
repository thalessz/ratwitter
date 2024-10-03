create database ratwitter;
use ratwitter;

create table usuario(
  	id integer primary key auto_increment not null,
    nome varchar(255) not null,
  	username varchar(255),
  	password varchar(255),
  	email varchar(255));
    
create table posts(
	 id integer primary key auto_increment not null,
   content text not null,
	 user_id integer not null,
	 foreign key(user_id) references usuario(id));