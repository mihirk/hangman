CREATE TABLE word_dictionary (
  word_id SERIAL PRIMARY KEY NOT NULL,
  word    TEXT               NOT NULL UNIQUE
);
