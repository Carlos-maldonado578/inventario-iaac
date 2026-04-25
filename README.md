# Guía de Uso — Sistema de Inventario Tienda Electrónica
### POO IACC — Semana 5

---

## Requisitos previos

| Herramienta | Versión mínima | Verificar con |
|-------------|----------------|---------------|
| Java JDK    | 17             | `java -version` |
| Maven       | incluido (mvnw)| `./mvnw -version` |
| VSCode      | cualquiera     | — |

No se requiere instalar ninguna base de datos externa. La aplicación usa **H2 en memoria** que se crea y destruye automáticamente al iniciar/detener el servidor.

---

## Estructura del proyecto

```
inventario/                        ← carpeta raíz del workspace
└── tienda-electronica/            ← proyecto principal (aquí se trabaja)
    ├── pom.xml
    └── src/main/java/com/tienda/electronica/
        ├── ElectronicaApplication.java   ← servidor Spring Boot (backend)
        ├── model/                        ← entidades JPA (tablas H2)
        ├── repository/                   ← acceso a datos
        ├── service/                      ← lógica de negocio
        ├── controller/                   ← endpoints REST
        └── gui/
            └── MainFrame.java            ← interfaz gráfica Swing (frontend)
```

---

## Paso 1 — Iniciar el servidor (backend)

Abrir una terminal en la carpeta `tienda-electronica` y ejecutar:

```bash
cd tienda-electronica
./mvnw spring-boot:run
```

**En Windows con cmd/PowerShell:**
```cmd
cd tienda-electronica
mvnw.cmd spring-boot:run
```

La primera ejecución descarga las dependencias de Maven (~1-2 minutos).
Las siguientes ejecuciones son inmediatas.

**El servidor está listo cuando aparece en consola:**
```
Started ElectronicaApplication in X.XXX seconds (JVM running for X.XXX)
```

> El servidor escucha en el puerto **8080**. Debe permanecer corriendo
> mientras se use la aplicación. No cerrar esta terminal.

---

## Paso 2 — Iniciar la interfaz gráfica (frontend Swing)

Con el servidor ya corriendo, abrir `MainFrame.java` en VSCode:

```
src/main/java/com/tienda/electronica/gui/MainFrame.java
```

Hacer clic derecho sobre el archivo → **Run Java**

Se abrirá la ventana del sistema con 5 pestañas:

| Pestaña | Función |
|---------|---------|
| 📦 Productos | Registrar productos y consultar inventario |
| 👥 Clientes | Registrar y listar clientes |
| 🏭 Proveedores | Registrar y listar proveedores |
| 💰 Ventas | Registrar ventas (aplica descuento automático) |
| 📊 Inventario | Reporte de stock actual |

---

## Regla de negocio — Descuento en ventas

| Condición | Descuento |
|-----------|-----------|
| Tipo `EN_LINEA` + cantidad **> 3** productos | **15%** sobre el subtotal |
| Tipo `EN_LINEA` + cantidad **≤ 3** productos | Sin descuento |
| Tipo `DIRECTO` (cualquier cantidad) | Sin descuento |

El descuento se calcula y muestra en tiempo real en el panel de Ventas al ingresar la cantidad, precio y tipo de compra.

---

## Consola H2 (base de datos en navegador)

Con el servidor corriendo, abrir en el navegador:

```
http://localhost:8080/h2-console
```

Datos de conexión:
- **JDBC URL:** `jdbc:h2:mem:tiendadb`
- **User:** `sa`
- **Password:** (dejar vacío)

Permite ver las tablas `PRODUCTOS`, `CLIENTES`, `PROVEEDORES` y `VENTAS` y ejecutar SQL directamente.

> Los datos se pierden al detener el servidor (base de datos en memoria).

---

## Endpoints REST disponibles

La API REST puede consultarse directamente desde el navegador o con Postman:

```
GET    http://localhost:8080/api/productos
POST   http://localhost:8080/api/productos
PUT    http://localhost:8080/api/productos/{id}
DELETE http://localhost:8080/api/productos/{id}

GET    http://localhost:8080/api/clientes
POST   http://localhost:8080/api/clientes
PUT    http://localhost:8080/api/clientes/{id}
DELETE http://localhost:8080/api/clientes/{id}

GET    http://localhost:8080/api/proveedores
POST   http://localhost:8080/api/proveedores
PUT    http://localhost:8080/api/proveedores/{id}
DELETE http://localhost:8080/api/proveedores/{id}

GET    http://localhost:8080/api/ventas
POST   http://localhost:8080/api/ventas
```

---

## Detener el servidor

En la terminal donde corre `./mvnw spring-boot:run`, presionar:

```
Ctrl + C
```

Los datos en H2 se eliminan al detener (comportamiento esperado con `ddl-auto=create-drop`).

---

## Solución de problemas frecuentes

**Error al abrir la GUI: "Error de Conexión"**
→ El servidor Spring Boot no está corriendo. Ejecutar `./mvnw spring-boot:run` primero.

**Puerto 8080 ocupado**
→ Otro proceso usa el puerto. Detenerlo o cambiar `server.port=8081` en `application.properties` y actualizar `BASE_URL` en `MainFrame.java`.

**`./mvnw: Permission denied` en Mac/Linux**
→ Ejecutar `chmod +x mvnw` una sola vez.

**La GUI muestra datos vacíos**
→ Hacer clic en "Consultar Todos" o "Actualizar Reporte" para cargar los datos de la API.
