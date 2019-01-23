CREATE TABLE users
(
  username TEXT          NOT NULL,
  password TEXT          NOT NULL,
  enabled  INT DEFAULT 1 NOT NULL
);

CREATE UNIQUE INDEX users_username_uindex
  on users (username);

CREATE TABLE authorities
(
  username  TEXT NOT NULL,
  authority TEXT NOT NULL
);
