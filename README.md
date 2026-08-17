#  Física Quest (Physics Quest)

**Física Quest** es un juego educativo de Android, 100% offline y en español,
pensado para niñas y niños de 8 a 12 años. Convierte la física en una
aventura: el jugador viaja por cinco mundos recuperando las piezas de una
nave científica, resolviendo puzzles que usan conceptos de física real
—sin pedir nunca una ecuación exacta.

- **Nombre visible:** Física Quest
- **Paquete:** `com.kidslab.physicsquest`
- **Versión:** 1.0.0
- **Repositorio:** `physics-quest-kids-android`
- **Idioma:** Español (100%)
- **Edad recomendada:** 8-12 años

##  Los cinco mundos

| # | Mundo | Concepto físico | Puzzle |
|---|-------|------------------|--------|
| 1 | Movimiento | Trayectoria, ángulo y fuerza | Lanzar una pelota a una meta |
| 2 | Fuerzas | Palanca, punto de apoyo, ventaja mecánica | Levantar cargas con una palanca |
| 3 | Máquinas simples | Plano inclinado, esfuerzo | Elegir la mejor rampa |
| 4 | Energía | Energía potencial y cinética | Construir un recorrido tipo montaña rusa |
| 5 | Sonido y ondas | Frecuencia y amplitud | Ajustar un sonido para abrir una puerta sónica |

Cada mundo tiene 6 niveles (30 en total): 5 niveles normales y 1 desafío de
**jefe científico** que cierra el mundo. Al superarlo se recupera una pieza
de la nave y se gana una insignia.

##  Contenido del juego

- **30 niveles** jugables, definidos como datos (no hardcodeados en las pantallas).
- **5 desafíos de jefe científico**, uno por mundo.
- **25 tarjetas de concepto** (5 por mundo), pensadas para leerse en menos de 30 segundos.
- **8 insignias** coleccionables.
- **Sistema de estrellas**: 1 por completar, 1 por pocos intentos, 1 por solución eficiente. Los fallos nunca penalizan de forma permanente.
- **Sistema de pistas**: se habilitan después de 2 intentos fallidos y nunca restan puntos.
- **Sonido generado localmente**: el puzzle de Sonido sintetiza tonos puros en el propio dispositivo (sin archivos de audio ni internet).

##  Arquitectura y tecnología

- **Kotlin** + **Jetpack Compose** + **Canvas** para toda la interfaz.
- **MVVM**: cada pantalla tiene su `ViewModel` con `StateFlow`.
- **Room** para persistencia local (12 entidades, ver `docs/BASE_DE_DATOS.md`).
- **Motor de puzzles separado de Compose**: los 5 motores de física
  (`TrajectoryEngine`, `LeverEngine`, `InclinedPlaneEngine`, `EnergyEngine`,
  `SoundEngine`) viven en Kotlin puro, sin ninguna dependencia de Android,
  y se prueban con JUnit normal.
- **Niveles definidos por datos**: cada nivel se compone de filas en
  `LevelObject` y `LevelRule`. Añadir un nivel nuevo no requiere tocar
  ninguna pantalla de Compose.
- **Inyección de dependencias manual** (`AppContainer`, sin frameworks externos).
- **100% offline**: no se declara ningún permiso de red, no hay login, ni
  servicios externos.
- **minSdk 24**, **compileSdk/targetSdk 34**, **JDK 17**, *core library
  desugaring* habilitado para `java.time`.

##  Estructura del proyecto

```
physics-quest-kids-android/
├── app/
│   └── src/
│       ├── main/kotlin/com/kidslab/physicsquest/
│       │   ├── data/           # Room (entidades, DAOs, base de datos, semillas, repositorio)
│       │   ├── domain/         # Motores de física, modelos y políticas (puro Kotlin)
│       │   ├── di/             # Contenedor de dependencias manual
│       │   └── ui/             # Pantallas de Compose + ViewModels + navegación
│       └── test/kotlin/...     # Pruebas unitarias y de persistencia (Robolectric)
├── docs/                       # Documentación (manuales, guía pedagógica, esquema de BD)
├── .github/workflows/          # CI y Release de GitHub Actions
└── SUBIR_A_GITHUB.md           # Guía paso a paso para subir el proyecto desde el navegador
```

##  Documentación

- [`docs/MANUAL_TECNICO.md`](docs/MANUAL_TECNICO.md) — arquitectura, cómo añadir niveles, cómo compilar.
- [`docs/MANUAL_USUARIO.md`](docs/MANUAL_USUARIO.md) — cómo jugar, pensado para niños, niñas y familias.
- [`docs/GUIA_PEDAGOGICA.md`](docs/GUIA_PEDAGOGICA.md) — guía para docentes: objetivos de aprendizaje por mundo.
- [`docs/BASE_DE_DATOS.md`](docs/BASE_DE_DATOS.md) — esquema completo de la base de datos, con diagrama ER en Mermaid.
- [`docs/sql/schema.sql`](docs/sql/schema.sql) — el esquema anterior en SQL.
- [`docs/pdf/`](docs/pdf/) — versión en PDF de los tres manuales principales.
- [`SUBIR_A_GITHUB.md`](SUBIR_A_GITHUB.md) — cómo subir este proyecto a GitHub y publicar la primera Release.

##  Compilar el proyecto

Este proyecto se generó en un entorno sin acceso a internet ni al SDK de
Android, por lo que **no ha sido compilado dentro de esa sesión**. Para
compilarlo:

1. Abre la carpeta `physics-quest-kids-android` en Android Studio (Koala o
   posterior). Android Studio detectará el proyecto Gradle y descargará las
   dependencias automáticamente (necesitas conexión a internet la primera vez).
2. O bien, compílalo desde GitHub Actions: cada `push` a `main` ejecuta el
   flujo `CI - Física Quest`, que compila el APK de depuración, corre las
   pruebas y el análisis de Lint.
3. Para generar una Release firmable, crea una etiqueta `vX.Y.Z` (por
   ejemplo `v1.0.0`) y el flujo `Release - Física Quest` compilará el APK
   `FisicaQuest-v1.0.0.apk` y publicará la Release **Física Quest v1.0.0**
   automáticamente.

##  Pruebas

El proyecto incluye pruebas unitarias para: trayectoria, fuerza, meta,
palanca, plano inclinado, energía, sonido, estrellas, pistas, desbloqueo de
mundos y persistencia (con Room en memoria vía Robolectric). Se ejecutan con:

```
gradle testDebugUnitTest
```

##  Licencia y créditos

Proyecto educativo generado para **Wari** (Lima, Perú) como parte de una
colección de apps educativas infantiles en español, 100% offline.
