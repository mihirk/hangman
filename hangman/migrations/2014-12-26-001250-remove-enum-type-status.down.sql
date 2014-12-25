CREATE TYPE gamestatus AS ENUM ('IN_PROGRESS', 'WON', 'LOST');

ALTER TABLE games
    ALTER COLUMN game_status TYPE gamestatus;