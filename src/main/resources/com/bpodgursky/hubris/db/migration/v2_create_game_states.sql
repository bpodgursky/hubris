CREATE TABLE IF NOT EXISTS game_states (
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  game_id INT NOT NULL,
  cookies_id INT NOT NULL,
  time TIMESTAMP NOT NULL,
  state LONGTEXT NOT NULL,
  INDEX(game_id),
  FOREIGN KEY (cookies_id)
    REFERENCES np_cookies(id)
) ENGINE=INNODB;
