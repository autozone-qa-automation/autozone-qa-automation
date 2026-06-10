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
└── cucumber/
    ├── IntegrationCucumberTestSuite.java
    └── services/ServicesIntegrationStepDefinitions.java

src/test/resources/features/integration/services/
├── services-list.feature
├── services-get.feature
├── services-test-endpoint.feature
├── services-crud.feature
└── services-validation.feature
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

---

## 📝 Notas para Colaboradores

Para mantener la integridad del framework, se deben seguir estas reglas:

* **Herencia:** Todas las clases de prueba en el paquete `tests` deben extender obligatoriamente de `BaseTest`.
* **Page Objects:** Mantener los selectores (IDs, CSS, XPath, etc.) exclusivamente dentro de las clases en el paquete `pages`. **No incluir validaciones (`Asserts`) en estas clases.**
* **Mantenimiento:** Si se requiere cambiar la URL base o el tipo de navegador, el cambio debe realizarse en el archivo de configuración dentro de `src/test/resources`.