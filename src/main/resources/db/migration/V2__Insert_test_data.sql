-- 1. Пользователи
INSERT INTO users (id, username, password, name) VALUES
                                                     (1, 'alice', 'password123', 'Alice Wonderland'),
                                                     (2, 'bob',   'password123', 'Bob Builder'),
                                                     (3, 'carol', 'password123', 'Carol Singer');

-- 2. Игровые сессии
INSERT INTO game_sessions (id, status, current_turn_player_id, created_at) VALUES
                                                                               (1, 'WAITING_FOR_PLAYERS', NULL, NOW() - INTERVAL '1 hour'),
                                                                               (2, 'IN_PROGRESS', 1, NOW() - INTERVAL '30 minutes'),
                                                                               (3, 'FINISHED', NULL, NOW() - INTERVAL '2 days');

-- 3. Игроки
INSERT INTO players (id, user_id, game_session_id, score, is_blocked, turn_order, is_active) VALUES
                                                                                                 (1, 1, 1, 0, false, 0, true),
                                                                                                 (2, 2, 1, 0, false, 1, true),
                                                                                                 (3, 1, 2, 15, false, 0, true),
                                                                                                 (4, 3, 2, 10, true, 1, false),
                                                                                                 (5, 2, 3, 30, false, 0, false);

-- 4. Шаблоны карт (мастер-колода)
INSERT INTO cards (name, type, value, action_type) VALUES
                                                       ('Points+3_1', 'POINTS', 3, NULL),
                                                       ('Points+3_2', 'POINTS', 3, NULL),
                                                       ('Points+3_3', 'POINTS', 3, NULL),
                                                       ('Points+4_1', 'POINTS', 4, NULL),
                                                       ('Points+4_2', 'POINTS', 4, NULL),
                                                       ('Points+4_3', 'POINTS', 4, NULL),
                                                       ('Points+5_1', 'POINTS', 5, NULL),
                                                       ('Points+5_2', 'POINTS', 5, NULL),
                                                       ('Points+5_3', 'POINTS', 5, NULL),
                                                       ('Points+6_1', 'POINTS', 6, NULL),
                                                       ('Points+6_2', 'POINTS', 6, NULL),
                                                       ('Points+6_3', 'POINTS', 6, NULL),
                                                       ('Block_1', 'ACTION', 1, 'BLOCK'),
                                                       ('Block_2', 'ACTION', 1, 'BLOCK'),
                                                       ('Block_3', 'ACTION', 1, 'BLOCK'),
                                                       ('Steal_1', 'ACTION', 2, 'STEAL'),
                                                       ('Steal_2', 'ACTION', 2, 'STEAL'),
                                                       ('Steal_3', 'ACTION', 2, 'STEAL'),
                                                       ('DoubleDown_1', 'ACTION', 2, 'DOUBLEDOWN'),
                                                       ('DoubleDown_2', 'ACTION', 2, 'DOUBLEDOWN');

-- 5. Пример колоды для сессии (можно раскомментировать и заполнить)
-- INSERT INTO game_decks (game_session_id, card_id, position) VALUES
--     (1, 1, 0), (1, 2, 1), (1, 5, 2),
--     (2, 3, 0), (2, 4, 1), (2, 1, 2),
--     (3, 5, 0), (3, 1, 1), (3, 4, 2);

-- 6. Ходы
INSERT INTO turns (game_session_id, player_id, card_id, affected_player_id, score_change, timestamp) VALUES
                                                                                                         (2, 1, 3, 4, 3, NOW() - INTERVAL '20 minutes'),
                                                                                                         (2, 3, 4, NULL, 20, NOW() - INTERVAL '15 minutes'),
                                                                                                         (3, 5, 5, NULL, 10, NOW() - INTERVAL '1 day');

-- 7. Сброс последовательностей
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('game_sessions_id_seq', (SELECT MAX(id) FROM game_sessions));
SELECT setval('players_id_seq', (SELECT MAX(id) FROM players));
SELECT setval('cards_id_seq', (SELECT MAX(id) FROM cards));
SELECT setval('turns_id_seq', (SELECT MAX(id) FROM turns));