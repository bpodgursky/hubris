CREATE TABLE IF NOT EXISTS game_syncs(
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  cookies_id INT NOT NULL,
  game_id BIGINT(20) NOT NULL,
  UNIQUE(cookies_id, game_id),
  FOREIGN KEY (cookies_id)
    REFERENCES np_cookies(id)
) ENGINE=INNODB;
