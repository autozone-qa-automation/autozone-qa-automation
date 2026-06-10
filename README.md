# Autozone-QA Automation Framework

Este repositorio contiene el framework de automatización **End-to-End (E2E)** para el proyecto **Autozone-QA**. El sistema está diseñado bajo el patrón de diseño **Page Object Model (POM)** para asegurar que las pruebas sean mantenibles, escalables y fáciles de leer.

---

## 🛠 Tecnologías y Librerías

El framework utiliza las siguientes herramientas y versiones:

* **Java 17:** Lenguaje de programación base.
* **Maven:** Gestión de dependencias y ciclo de vida del proyecto.
* **Selenium WebDriver (4.20.0):** Motor para la interacción con el navegador.
* **TestNG (7.8.0):** Framework para la organización y ejecución de casos de prueba.
* **WebDriverManager (5.8.0):** Gestión automática de binarios de Chrome (elimina la necesidad de descargar `chromedriver` manualmente).

---

## 🏗 Estructura del Proyecto

Siguiendo las convenciones estándar de Maven, el código se organiza de la siguiente manera:

```text
src/
├── main/java/com/autozone/pages/     # Page Objects: encapsulan locators y acciones de la UI
├── main/java/com/autozone/utils/     # Utilidades: helpers reutilizables (driver, waits, lectura de config)
├── main/java/com/autozone/config/    # Configuración: constantes y parámetros globales del framework
├── main/resources/                   # Recursos de la app: configs base, archivos estáticos o datos compartidos
├── test/java/com/autozone/base/      # BaseTest: setup y teardown de las pruebas
├── test/java/com/autozone/tests/     # Tests: flujos de negocio y validaciones (asserts)
├── test/java/com/autozone/integration/  # Suite de integración API (Cucumber + java.net.http.HttpClient)
└── test/resources/                   # Recursos: config.properties, env.properties y datos de prueba
```

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado en tu equipo local:

1.  **Java JDK 17**
2.  **Apache Maven 3.x**
3.  **Google Chrome** (Navegador actualizado)

---

## 🚀 Guía de Comandos Rápidos

### 1. Clonar el repositorio
```bash
git clone https://github.com/autozone-qa-automation/autozone-qa-automation.git
cd autozone-qa-automation
```

### 2. Descargar dependencias y compilar
Este comando limpia ejecuciones previas y descarga automáticamente las librerías configuradas (Selenium, TestNG, etc.).
```bash
mvn clean compile
```

### 3. Ejecutar las pruebas
Para ejecutar **todos** los tests del proyecto:
```bash
mvn test
```

Para ejecutar una **suite específica** mediante el archivo XML:
```bash
mvn test -DsuiteXmlFile=testng.xml
```

---

## 📊 Reportes de Prueba

Al finalizar la ejecución, TestNG genera reportes automáticos en formato HTML. Puedes consultar los resultados detallados abriendo el siguiente archivo en tu navegador:

`target/surefire-reports/index.html`

---

## 🔌 Pruebas de Integración API (Cucumber)

Además de las pruebas E2E con Selenium, el proyecto incluye una suite de
**integración a nivel API** contra el backend (`autozone-qa-be`), implementada
con **Cucumber + JUnit Platform** y `java.net.http.HttpClient` (sin
RestAssured). Las peticiones/respuestas se serializan con Jackson
`ObjectMapper`, ya que `ServicesVO` incluye un arreglo anidado `urls: UrlVO[]`.

```text
src/test/java/com/autozone/integration/
├── config/IntegrationConfig.java       # Resolución de configuración (ver abajo)
├── client/ApiResponse.java             # statusCode + contentType + body
├── client/ServicesApiClient.java       # Cliente HTTP para /api/v1/services
├── client/FeaturesApiClient.java       # Cliente HTTP para /api/v1/features
├── client/TestCasesApiClient.java      # Cliente HTTP para /api/v1/test-cases
├── client/ReleasesApiClient.java       # Cliente HTTP para /api/v1/releases
├── client/ReportsApiClient.java        # Cliente HTTP para /api/v1/reports (incluye export CSV)
└── cucumber/
    ├── IntegrationCucumberTestSuite.java
    ├── support/BackgroundStepDefinitions.java   # Steps compartidos (login, dispatchers genéricos, asserts)
    ├── support/IntegrationContext.java          # Holder estático: token, último ApiResponse, fixtures compartidas
    ├── services/ServicesIntegrationStepDefinitions.java
    ├── features/FeaturesIntegrationStepDefinitions.java
    ├── testcases/TestCasesIntegrationStepDefinitions.java
    ├── releases/ReleasesIntegrationStepDefinitions.java
    ├── reports/ReportsIntegrationStepDefinitions.java
    └── lifecycle/LifecycleIntegrationStepDefinitions.java

src/test/resources/features/integration/
├── services/
│   ├── services-list.feature
│   ├── services-get.feature
│   ├── services-test-endpoint.feature
│   ├── services-crud.feature
│   └── services-validation.feature
├── features/features.feature
├── test-cases/test-cases.feature
├── releases/releases.feature
├── reports/reports.feature
└── lifecycle/lifecycle.feature
```

### Configuración (`IntegrationConfig`)

Cada propiedad se resuelve en este orden (la primera que exista gana):

1. **System property de la JVM**: `-Dapi.base.url=...`
2. **Variable de entorno**: `API_BASE_URL`
3. **Archivo `src/test/resources/env.properties`** (no se versiona)
4. **Valor por defecto** (solo aplica a `api.base.url` y `api.login.path`)

| Propiedad        | Variable de entorno | Por defecto                | Notas                                   |
|-------------------|----------------------|------------------------------|------------------------------------------|
| `api.base.url`    | `API_BASE_URL`       | `http://localhost:8080`      | Sin `/` final                            |
| `api.login.path`  | `API_LOGIN_PATH`     | `/api/v1/authentify`         |                                            |
| `api.username`    | `API_USERNAME`       | *(requerido, sin default)*   | Email del usuario ADMIN sembrado en BD   |
| `api.password`    | `API_PASSWORD`       | *(requerido, sin default)*   | Password en texto plano del usuario ADMIN |

Para configurar localmente:

```bash
cp src/test/resources/env.properties.example src/test/resources/env.properties
# edita src/test/resources/env.properties con tus valores
```

### ⚠️ Setup obligatorio: usuario ADMIN de pruebas

El backend **no expone una vía de API para crear el primer usuario ADMIN**:
`POST /api/v1/users` requiere autenticación previa y guarda la contraseña
sin hashear, por lo que ese endpoint no sirve para crear un usuario con el
que luego se pueda iniciar sesión.

Por eso, antes de correr la suite de integración hay que insertar un usuario
ADMIN directamente en la base de datos del backend, con su password ya
hasheado en BCrypt:

1. Levanta la base de datos del backend (`autozone-qa-be`).
2. Ejecuta el script `db/seed_admin.sql` de este repo contra esa base:

   ```bash
   mysql -u <usuario> -p <nombre_de_la_bd> < db/seed_admin.sql
   ```

   El script es idempotente (puede correrse varias veces sin duplicar filas)
   e inserta:
   - El rol `ADMIN` (`idRole = 1`) si no existe.
   - El usuario `admin@autozone.com` con password `Qa!Admin2026` (hash BCrypt,
     cost factor 4 — el mismo que usa `BCryptPasswordEncoder` en el backend).

3. Asegúrate de que `src/test/resources/env.properties` tenga
   `api.username` / `api.password` con **los mismos valores sembrados** en el
   paso anterior (o las credenciales que tú hayas elegido si modificaste el
   script y regeneraste el hash).

### Ejecutar la suite de integración

```bash
mvn -Dtest=IntegrationCucumberTestSuite test
```

(o `./mvnw -Dtest=IntegrationCucumberTestSuite test` si usas el wrapper de Maven).

Esto requiere que el backend (`autozone-qa-be`) esté corriendo y accesible en
`api.base.url`, y que el usuario ADMIN de la sección anterior ya exista en su
base de datos.

### Áreas cubiertas actualmente (Integration API Test Areas)

- **Services API**: list / get / test-endpoint / create / update / delete
  - `services-list.feature`: `GET /api/v1/services` devuelve 200 y un arreglo
    de servicios con `id`, `name`, `isActive` y `urls`.
  - `services-get.feature`: `GET /api/v1/services/{id}` devuelve 200 para un
    id existente y 404 para uno inexistente.
  - `services-test-endpoint.feature`: `GET /api/v1/services/test/{id}`
    devuelve siempre 3 urls (Produccion, QA, Dev).
  - `services-crud.feature`: ciclo de vida completo (create → get → update →
    delete → get 404).
  - `services-validation.feature`: `POST /api/v1/services` devuelve 400 con
    `name = null` y 409 con un nombre duplicado.

- **Features API**: list / create / get / filter por servicio / update / deactivate
  - `features.feature`:
    - `GET /api/v1/features` devuelve 200 y un arreglo de features, cada uno
      con `id`, `featureName`, `featureDescription` e `idService`.
    - Ciclo de vida completo: create (echo de `featureName`/`featureDescription`/
      `idService`, incluye `serviceName` e `id` generado) → get by id → filtrar
      por `idService` vía `GET /api/v1/features/filtered/{serviceId}` → update
      de nombre/descripción (el `idService` no cambia).
    - ⚠️ **Known quirk**: actualizar una feature reutilizando el `featureName`
      de otra feature existente devuelve **500** (no 409).
    - Desactivar una feature devuelve **200 con body vacío**, y un `GET`
      posterior por ese id devuelve 404.
    - `GET`, filtrar por servicio y desactivar con ids inexistentes devuelven
      404 (`/api/v1/features/999999999`,
      `/api/v1/features/filtered/999999999`,
      deactivate `999999999`).
    - Crear una feature con `featureName = null` devuelve 400.
    - Crear una feature para un `idService` inexistente devuelve 404.

- **Test Cases API**: list / create / get / filter por feature / update / deactivate
  - `test-cases.feature`:
    - `GET /api/v1/test-cases` devuelve 200 y un arreglo de test cases, cada
      uno con `id`, `title`, `relatedFeature`, `type`, `steps`,
      `expectedOutput` y la bandera `active`.
    - Ciclo de vida completo: create (echo de `title`/`type`/`steps`/
      `expectedOutput`/`relatedFeature`, `active = true` e `id` generado) →
      get by id (`featureName` es `null` en el detalle individual) → listar
      por feature → update de `title`/`steps`/`expectedOutput`.
    - Listar todos los test cases sí incluye `featureName` poblado para los
      test cases creados.
    - Crear un test case con `title = null` devuelve 400.
    - Crear un test case con un `title` duplicado (misma feature) devuelve
      409.
    - Actualizar un test case enviando un `id` distinto en el body devuelve
      400.
    - Actualizar un test case reutilizando el `title` de otro test case de la
      misma feature devuelve 409.
    - Desactivar un test case devuelve **204 con body vacío**, y un `GET`
      posterior por ese id devuelve 404.
    - Desactivar y obtener un test case con id inexistente devuelven 404.

- **Releases API**: list / "last releases" / create / get / filtrar por status
  o tag / transiciones de estado / delete
  - `releases.feature`:
    - `GET /api/v1/releases` devuelve 200 y un arreglo de releases, cada uno
      con `releaseId`, `releaseName`, `releaseDescription`, `releaseVersion`,
      `releaseStatus`, `releaseTags`, `releaseCreationDate` y la bandera
      `releaseIsActive`.
    - `GET /api/v1/releases/last` devuelve 200 y un arreglo.
    - Ciclo de vida completo de un release `Draft`: create (echo de
      `releaseName`/`releaseDescription`/`releaseVersion`/`releaseStatus`/
      `releaseTags`, `releaseIsActive = true` e `id` generado) → get by id →
      filtrar por `status=Draft`.
    - Filtrar por `tag` devuelve los releases que tengan ese tag.
    - `GET /api/v1/releases/999999999` devuelve **404 con body vacío**.
    - Transiciones de estado válidas (`Draft → Progress → Active`) devuelven
      200 con `releaseStatus` actualizado, pero un release que no está en
      `Draft` **no puede eliminarse** (`DELETE` devuelve 400 con un mensaje
      que contiene `"DRAFT"`).
    - ⚠️ **Known quirk**: una transición de estado inválida (p. ej.
      `Active → Draft`) devuelve **400 con body en texto plano** que contiene
      `"Invalid status transition"`.
    - ⚠️ **Known quirk**: enviar un valor de `releaseStatus` que no es un
      enum válido (p. ej. `"NotARealStatus"`) devuelve **400 con body JSON**
      que contiene el valor inválido.
    - Actualizar el status de un release inexistente devuelve **404 con body
      en texto plano** que contiene el id solicitado.
    - Eliminar un release inexistente devuelve **404 con body JSON**.
    - Crear un release con `releaseName = null` devuelve 400.

- **Reports API** (solo lectura: list + export CSV)
  - `reports.feature`:
    - `GET /api/v1/reports` devuelve 200 y un arreglo de reportes, cada uno
      con `releaseId`, `releaseName`, `releaseDescription`, `releaseVersion`,
      `releaseStatus`, `releaseTags`, `releaseCreationDate`,
      `releaseLaunchDate` y un arreglo `services` (cada `service` contiene
      `serviceName` y un arreglo `features`, cada `feature` contiene
      `featureName` y un arreglo `testCases` con los títulos de los test
      cases activos).
    - Filtrar por `serviceId` o `tagName` inexistentes devuelve 200 con un
      arreglo vacío (`[]`).
    - `GET /api/v1/reports/export?releaseIds=1,2,3` devuelve 200,
      `Content-Type: text/csv` y un CSV (con BOM UTF-8) cuya primera fila es
      `Versión del release;Nombre del release;...`.
    - ⚠️ **Known quirk**: exportar con `releaseIds=` (parámetro presente pero
      vacío) devuelve **200 con body vacío** (no es un error).
    - ⚠️ **Known quirk**: exportar **sin** el parámetro `releaseIds` devuelve
      **500 con body JSON** (`"An unexpected error occurred"`), porque el
      `GlobalExceptionHandler` captura la
      `MissingServletRequestParameterException` con su handler genérico de
      `Exception`.

- **Lifecycle** (`@lifecycle`): flujo completo end-to-end
  - `lifecycle.feature`: crea un Service → Feature → Test Case → release
    `Draft` (asociando la feature vía `releaseFeatureIds`) → transiciona el
    release a `Progress` y luego a `Active` → confirma que
    `GET /api/v1/reports?tagName=...` devuelve ese release con el service, la
    feature y el test case en su jerarquía → confirma que el export CSV de
    ese release contiene los nombres de las cuatro entidades creadas.
    Limpia los recursos creados al final (deactivate test case → deactivate
    feature → soft-delete del service).

---

## 📝 Notas para Colaboradores

Para mantener la integridad del framework, se deben seguir estas reglas:

* **Herencia:** Todas las clases de prueba en el paquete `tests` deben extender obligatoriamente de `BaseTest`.
* **Page Objects:** Mantener los selectores (IDs, CSS, XPath, etc.) exclusivamente dentro de las clases en el paquete `pages`. **No incluir validaciones (`Asserts`) en estas clases.**
* **Mantenimiento:** Si se requiere cambiar la URL base o el tipo de navegador, el cambio debe realizarse en el archivo de configuración dentro de `src/test/resources`.