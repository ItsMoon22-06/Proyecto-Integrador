# Software de Prácticas Pedagógicas — UDI

Sistema de escritorio desarrollado en **Java** para el registro y seguimiento de las prácticas pedagógicas de la **Universidad de Investigación y Desarrollo (UDI)**. Proyecto Integrador.

---

## Descripción

Este software permite gestionar y hacer seguimiento a las prácticas pedagógicas que realizan los docentes o estudiantes en el marco de los programas académicos de la UDI. La aplicación facilita el registro de actividades, el control del avance y la consulta del historial de prácticas.

---

## Funcionalidades principales

-  **Registro de prácticas pedagógicas** (actividades, fechas, responsables)
-  **Seguimiento del estado** de cada práctica
-  **Gestión de participantes** (docentes / estudiantes)
-  **Consulta y visualización** del historial de prácticas registradas
-  Retroalimentación al usuario mediante diálogos informativos

---

##  Estructura del proyecto

```
Proyecto-Integrador/
│
├── src/
│   └── com/
│       └── mycompany/
│           └── [paquete principal]/
│               ├── modelo/         # Clases de dominio (Práctica, Usuario, etc.)
│               ├── interfaz/       # Ventanas JFrame
│               └── [Main].java     # Clase principal
│
└── README.md
```

>  La estructura puede variar según la organización del proyecto en NetBeans.

---

##  Tecnologías utilizadas

| Tecnología          | Uso                              |
|---------------------|----------------------------------|
| Java                | Lenguaje principal               |
| Java Swing (JFrame) | Interfaz gráfica de usuario      |
| JOptionPane         | Diálogos y mensajes al usuario   |
| NetBeans IDE        | Entorno de desarrollo            |

---

##  Cómo ejecutar el proyecto

### Requisitos previos

- Java JDK 8 o superior instalado
- NetBeans IDE (recomendado) o cualquier IDE compatible con Java

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/ItsMoon22-06/Proyecto-Integrador.git
   ```

2. **Abrir en NetBeans**
   - Ir a `File > Open Project`
   - Seleccionar la carpeta del repositorio clonado
   - Se requiere ojdbc11.jar para ejecutar la base de datos
   - La base ded datos conectada al Software está conectada con la Universidad

3. **Ejecutar el proyecto**
   - Clic derecho sobre el proyecto → `Run`
   - O presionar `F6`

---

##  Contexto académico

Este proyecto fue desarrollado como **Proyecto Integrador** en el marco del programa académico de la **Universidad de Investigación y Desarrollo — UDI**, con el objetivo de aplicar los conocimientos adquiridos en el desarrollo de software orientado a objetos con interfaz gráfica.

---

##  Autor

**ItsMoon22-06**  
Hellen Mancilla Alquichire
Edwin Sebastian Leal Isidro 
Maria Camila Contreras Navarro 
Universidad de Investigación y Desarrollo — UDI

---

##  Licencia

Proyecto desarrollado con fines académicos. Todos los derechos reservados © UDI.
