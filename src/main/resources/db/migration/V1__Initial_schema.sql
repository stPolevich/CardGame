-- 1. Таблица пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL
);

-- 2. Таблица игровых сессий
CREATE TABLE game_sessions (
                               id BIGSERIAL PRIMARY KEY,
                               status VARCHAR(50) NOT NULL,
                               current_turn_player_id BIGINT,
                               winner_id BIGINT,
                               created_at TIMESTAMP NOT NULL,
                               max_players INTEGER NOT NULL DEFAULT 4,
                               target_score INTEGER NOT NULL DEFAULT 30
);

-- 3. Таблица игроков
CREATE TABLE players (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES users(id),
                         game_session_id BIGINT NOT NULL REFERENCES game_sessions(id),
                         score INTEGER NOT NULL DEFAULT 0,
                         is_blocked BOOLEAN NOT NULL DEFAULT false,
                         turn_order INTEGER NOT NULL DEFAULT 0,
                         is_active BOOLEAN NOT NULL DEFAULT true
);

ALTER TABLE game_sessions
    ADD CONSTRAINT fk_winner FOREIGN KEY (winner_id) REFERENCES players(id);

-- 4. Таблица шаблонов карт
CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       type VARCHAR(50) NOT NULL,
                       value INTEGER NOT NULL,
                       action_type VARCHAR(50)
);

-- 5. Таблица колод для каждой сессии
CREATE TABLE game_decks (
                            game_session_id BIGINT NOT NULL REFERENCES game_sessions(id),
                            card_id BIGINT NOT NULL REFERENCES cards(id),
                            position INTEGER NOT NULL,
                            PRIMARY KEY (game_session_id, position)
);

-- 6. Таблица ходов
CREATE TABLE turns (
                       id BIGSERIAL PRIMARY KEY,
                       game_session_id BIGINT NOT NULL REFERENCES game_sessions(id),
                       player_id BIGINT NOT NULL REFERENCES players(id),
                       card_id BIGINT NOT NULL REFERENCES cards(id),
                       affected_player_id BIGINT REFERENCES players(id),
                       score_change INTEGER NOT NULL,
                       timestamp TIMESTAMP NOT NULL
);

-- 7. Индексы для оптимизации
CREATE INDEX idx_game_session_status ON game_sessions(status);
CREATE INDEX idx_player_game_session ON players(game_session_id);
CREATE INDEX idx_turn_game_session ON turns(game_session_id);