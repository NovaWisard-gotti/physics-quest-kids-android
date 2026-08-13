# Cómo subir Física Quest a GitHub (solo desde el navegador)

Esta guía no requiere instalar Git ni usar la terminal: todo se hace desde
el navegador web, subiendo los archivos que ya tienes en tu computadora.

## 1. Crear el repositorio en GitHub

1. Entra a [github.com](https://github.com) e inicia sesión.
2. Haz clic en el botón **"+"** (arriba a la derecha) → **"New repository"**.
3. En **Repository name** escribe: `physics-quest-kids-android`
4. Marca el repositorio como **Public** (o **Private**, como prefieras).
5. **No** marques "Add a README file" (ya tienes uno en el proyecto).
6. Haz clic en **"Create repository"**.

## 2. Subir los archivos del proyecto

1. Descomprime el archivo ZIP que te entregó Claude en tu computadora.
   Deberías tener una carpeta `physics-quest-kids-android` con `app/`,
   `docs/`, `.github/`, `README.md`, etc. **directamente dentro**, sin una
   carpeta extra envolviendo todo.
2. En la página de tu nuevo repositorio vacío, haz clic en el enlace
   **"uploading an existing file"**.
3. Arrastra **todo el contenido** de la carpeta del proyecto (todos los
   archivos y subcarpetas) a la zona de subida de GitHub.
   - ⚠️ Si tu navegador no permite subir carpetas completas de una vez,
     sube primero los archivos sueltos de la raíz (`README.md`,
     `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`) y
     luego repite el proceso para cada subcarpeta (`app`, `docs`,
     `.github`, `gradle`), arrastrando el contenido de cada una.
4. Escribe un mensaje de commit, por ejemplo: `Primera versión de Física Quest`.
5. Haz clic en **"Commit changes"**.

## 3. Verificar que la compilación automática funciona

1. Ve a la pestaña **"Actions"** de tu repositorio.
2. Deberías ver el flujo **"CI - Física Quest"** ejecutándose (o a punto de
   ejecutarse) tras tu primer *commit*.
3. Espera a que termine. Si algo falla, haz clic en la ejecución para ver
   el error exacto — puedes copiar y pegar ese error para pedir ayuda.
4. Si todo sale en verde ✅, tu proyecto compila correctamente y las
   pruebas automáticas pasaron.

## 4. Publicar la primera Release (versión 1.0.0)

Hay dos formas de hacerlo, ambas desde el navegador:

### Opción A: crear una etiqueta (tag) desde GitHub
1. Ve a la pestaña **"Code"** de tu repositorio.
2. Haz clic en el menú desplegable de ramas (donde dice "main") y luego en
   la pestaña **"Tags"** → **"Create a new release"** (o ve directamente a
   la pestaña **"Releases"** → **"Draft a new release"**).
3. En **"Choose a tag"**, escribe `v1.0.0` y confirma que se cree como una
   etiqueta nueva a partir de `main`.
4. Haz clic en **"Publish release"** (puedes dejar los demás campos vacíos:
   el flujo automático se encargará de compilar y adjuntar el APK).

### Opción B: ejecutar el flujo manualmente
1. Ve a la pestaña **"Actions"** → selecciona **"Release - Física Quest"**
   en la lista de la izquierda.
2. Haz clic en **"Run workflow"**, escribe `1.0.0` en el campo de versión y
   confirma.

En ambos casos, GitHub Actions compilará el APK y creará automáticamente
la Release **"Física Quest v1.0.0"** con el archivo
**`FisicaQuest-v1.0.0.apk`** adjunto, listo para descargar e instalar.

## 5. Si algo falla

Copia el mensaje de error completo de la pestaña **"Actions"** (haz clic
en el paso que aparece en rojo ❌ para expandirlo) y compártelo en tu
próxima conversación con Claude para corregirlo.
