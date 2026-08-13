-- Física Quest — Esquema SQL equivalente al esquema de Room (versión 1)
-- Base de datos local SQLite, 100% offline.

CREATE TABLE IF NOT EXISTS user_profile (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    explorerName            TEXT    NOT NULL,
    avatarId                INTEGER NOT NULL DEFAULT 0,
    totalStars              INTEGER NOT NULL DEFAULT 0,
    shipPiecesRecovered     INTEGER NOT NULL DEFAULT 0,
    createdAtEpochMillis    INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS world (
    id                      INTEGER PRIMARY KEY,
    "order"                 INTEGER NOT NULL,
    title                   TEXT    NOT NULL,
    subtitle                TEXT    NOT NULL,
    description             TEXT    NOT NULL,
    puzzleType              TEXT    NOT NULL,
    starsRequiredToUnlock   INTEGER NOT NULL,
    shipPieceName           TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS level (
    id                      INTEGER PRIMARY KEY,
    worldId                 INTEGER NOT NULL,
    levelNumberInWorld      INTEGER NOT NULL,
    title                   TEXT    NOT NULL,
    instructions            TEXT    NOT NULL,
    difficulty              INTEGER NOT NULL,
    conceptCardId           INTEGER,
    FOREIGN KEY (worldId) REFERENCES world(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_level_worldId ON level(worldId);
CREATE INDEX IF NOT EXISTS idx_level_conceptCardId ON level(conceptCardId);

CREATE TABLE IF NOT EXISTS level_object (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    levelId                 INTEGER NOT NULL,
    objectType              TEXT    NOT NULL,
    positionX               REAL    NOT NULL,
    positionY               REAL    NOT NULL,
    extraValue              REAL,
    extraLabel              TEXT,
    FOREIGN KEY (levelId) REFERENCES level(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_level_object_levelId ON level_object(levelId);

CREATE TABLE IF NOT EXISTS level_rule (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    levelId                 INTEGER NOT NULL,
    ruleType                TEXT    NOT NULL,
    value1                  REAL    NOT NULL,
    value2                  REAL,
    FOREIGN KEY (levelId) REFERENCES level(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_level_rule_levelId ON level_rule(levelId);

CREATE TABLE IF NOT EXISTS level_progress (
    id                          INTEGER PRIMARY KEY AUTOINCREMENT,
    userProfileId               INTEGER NOT NULL,
    levelId                     INTEGER NOT NULL,
    stars                       INTEGER NOT NULL DEFAULT 0,
    bestAttemptsToComplete      INTEGER,
    completed                   INTEGER NOT NULL DEFAULT 0,
    unlocked                    INTEGER NOT NULL DEFAULT 0,
    lastPlayedAtEpochMillis     INTEGER,
    FOREIGN KEY (userProfileId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (levelId) REFERENCES level(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_level_progress_userProfileId ON level_progress(userProfileId);
CREATE INDEX IF NOT EXISTS idx_level_progress_levelId ON level_progress(levelId);
CREATE UNIQUE INDEX IF NOT EXISTS idx_level_progress_user_level ON level_progress(userProfileId, levelId);

CREATE TABLE IF NOT EXISTS attempt (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    levelProgressId         INTEGER NOT NULL,
    attemptNumber           INTEGER NOT NULL,
    success                 INTEGER NOT NULL,
    efficiencyScore         REAL    NOT NULL,
    starsEarned             INTEGER NOT NULL,
    hintUsed                INTEGER NOT NULL,
    timestampEpochMillis    INTEGER NOT NULL,
    FOREIGN KEY (levelProgressId) REFERENCES level_progress(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_attempt_levelProgressId ON attempt(levelProgressId);

CREATE TABLE IF NOT EXISTS hint (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    levelId     INTEGER NOT NULL,
    "order"     INTEGER NOT NULL,
    text        TEXT    NOT NULL,
    FOREIGN KEY (levelId) REFERENCES level(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_hint_levelId ON hint(levelId);

CREATE TABLE IF NOT EXISTS concept_card (
    id                  INTEGER PRIMARY KEY,
    worldId             INTEGER NOT NULL,
    "order"             INTEGER NOT NULL,
    title               TEXT    NOT NULL,
    shortExplanation    TEXT    NOT NULL,
    everydayExample     TEXT    NOT NULL,
    FOREIGN KEY (worldId) REFERENCES world(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_concept_card_worldId ON concept_card(worldId);

CREATE TABLE IF NOT EXISTS boss_challenge (
    id                          INTEGER PRIMARY KEY,
    worldId                     INTEGER NOT NULL,
    basePuzzleLevelId           INTEGER NOT NULL,
    title                       TEXT    NOT NULL,
    scientistName               TEXT    NOT NULL,
    introDialogue               TEXT    NOT NULL,
    mixedConceptsDescription    TEXT    NOT NULL,
    starsRequiredToUnlock       INTEGER NOT NULL,
    rewardBadgeId               INTEGER,
    FOREIGN KEY (worldId) REFERENCES world(id) ON DELETE CASCADE,
    FOREIGN KEY (basePuzzleLevelId) REFERENCES level(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_boss_challenge_worldId ON boss_challenge(worldId);
CREATE INDEX IF NOT EXISTS idx_boss_challenge_basePuzzleLevelId ON boss_challenge(basePuzzleLevelId);

CREATE TABLE IF NOT EXISTS badge (
    id                      INTEGER PRIMARY KEY,
    code                    TEXT    NOT NULL,
    title                   TEXT    NOT NULL,
    description             TEXT    NOT NULL,
    criteriaDescription     TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS user_badge (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    userProfileId           INTEGER NOT NULL,
    badgeId                 INTEGER NOT NULL,
    earnedAtEpochMillis     INTEGER NOT NULL,
    FOREIGN KEY (userProfileId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (badgeId) REFERENCES badge(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_user_badge_userProfileId ON user_badge(userProfileId);
CREATE INDEX IF NOT EXISTS idx_user_badge_badgeId ON user_badge(badgeId);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_badge_user_badge ON user_badge(userProfileId, badgeId);
