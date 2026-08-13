# Esquema de la base de datos — Física Quest

Base de datos local **Room** (SQLite), 100% offline, sin ninguna
sincronización remota. Versión de esquema: 1.

## Diagrama entidad-relación

```mermaid
erDiagram
    UserProfile ||--o{ LevelProgress : "tiene"
    UserProfile ||--o{ UserBadge : "gana"
    World ||--o{ Level : "contiene"
    World ||--o{ ConceptCard : "agrupa"
    World ||--o| BossChallenge : "termina en"
    Level ||--o{ LevelObject : "define"
    Level ||--o{ LevelRule : "define"
    Level ||--o{ Hint : "ofrece"
    Level ||--o{ LevelProgress : "referenciado por"
    Level }o--o| ConceptCard : "desbloquea"
    Level ||--o| BossChallenge : "es la base de"
    LevelProgress ||--o{ Attempt : "acumula"
    Badge ||--o{ UserBadge : "otorgada como"
    BossChallenge }o--o| Badge : "recompensa con"

    UserProfile {
        long id PK
        string explorerName
        int avatarId
        int totalStars
        int shipPiecesRecovered
        long createdAtEpochMillis
    }

    World {
        long id PK
        int order
        string title
        string subtitle
        string description
        string puzzleType
        int starsRequiredToUnlock
        string shipPieceName
    }

    Level {
        long id PK
        long worldId FK
        int levelNumberInWorld
        string title
        string instructions
        int difficulty
        long conceptCardId FK
    }

    LevelObject {
        long id PK
        long levelId FK
        string objectType
        float positionX
        float positionY
        float extraValue
        string extraLabel
    }

    LevelRule {
        long id PK
        long levelId FK
        string ruleType
        float value1
        float value2
    }

    LevelProgress {
        long id PK
        long userProfileId FK
        long levelId FK
        int stars
        int bestAttemptsToComplete
        boolean completed
        boolean unlocked
        long lastPlayedAtEpochMillis
    }

    Attempt {
        long id PK
        long levelProgressId FK
        int attemptNumber
        boolean success
        float efficiencyScore
        int starsEarned
        boolean hintUsed
        long timestampEpochMillis
    }

    Hint {
        long id PK
        long levelId FK
        int order
        string text
    }

    ConceptCard {
        long id PK
        long worldId FK
        int order
        string title
        string shortExplanation
        string everydayExample
    }

    BossChallenge {
        long id PK
        long worldId FK
        long basePuzzleLevelId FK
        string title
        string scientistName
        string introDialogue
        string mixedConceptsDescription
        int starsRequiredToUnlock
        long rewardBadgeId FK
    }

    Badge {
        long id PK
        string code
        string title
        string description
        string criteriaDescription
    }

    UserBadge {
        long id PK
        long userProfileId FK
        long badgeId FK
        long earnedAtEpochMillis
    }
```

## Descripción de las tablas

| Tabla | Propósito |
|-------|-----------|
| `user_profile` | Perfil único y local del explorador/a. Sin login. |
| `world` | Los 5 mundos, su tipo de puzzle y el umbral de estrellas para desbloquearse. |
| `level` | Los 30 niveles (6 por mundo, el 6º es el jefe científico). |
| `level_object` | Los elementos físicos/visuales de cada nivel (pelota, meta, obstáculo, carga, rampa, tramo de pista, altavoz, etc.), en coordenadas normalizadas 0f..1f. |
| `level_rule` | Los parámetros de evaluación de cada nivel (tolerancias, umbrales, rangos). |
| `level_progress` | El mejor resultado histórico del jugador en cada nivel (nunca se guarda un resultado peor que el actual). |
| `attempt` | Historial completo de cada intento (éxito/fallo, eficiencia, si se usó pista), útil para el desbloqueo de pistas y estadísticas. |
| `hint` | Los textos de pista de cada nivel, en orden. |
| `concept_card` | Las 25 tarjetas de concepto (5 por mundo). |
| `boss_challenge` | Los 5 desafíos de jefe científico, uno por mundo. |
| `badge` | Las 8 insignias coleccionables. |
| `user_badge` | Relación N:M entre el perfil y las insignias que ganó. |

Ver el SQL equivalente en [`sql/schema.sql`](sql/schema.sql).
