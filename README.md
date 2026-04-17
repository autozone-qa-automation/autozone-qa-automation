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
└── test/resources/                   # Recursos: config.properties y datos de prueba
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

## 📝 Notas para Colaboradores

Para mantener la integridad del framework, se deben seguir estas reglas:

* **Herencia:** Todas las clases de prueba en el paquete `tests` deben extender obligatoriamente de `BaseTest`.
* **Page Objects:** Mantener los selectores (IDs, CSS, XPath, etc.) exclusivamente dentro de las clases en el paquete `pages`. **No incluir validaciones (`Asserts`) en estas clases.**
* **Mantenimiento:** Si se requiere cambiar la URL base o el tipo de navegador, el cambio debe realizarse en el archivo de configuración dentro de `src/test/resources`.