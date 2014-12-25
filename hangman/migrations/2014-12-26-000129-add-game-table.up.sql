CREATE TYPE gamestatus AS ENUM ('IN_PROGRESS', 'WON', 'LOST');

CREATE TABLE games (
  game_id      SERIAL PRIMARY KEY,
  game_uuid    TEXT UNIQUE,
  word_id      INT REFERENCES word_dictionary (word_id),
  game_status  gamestatus,
  tries        NUMERIC CONSTRAINT less_than_equal_to_eleven CHECK (tries < 12),
  guessed_word TEXT
);