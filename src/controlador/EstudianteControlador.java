// Declara el paquete al que pertenece esta clase
package controlador;

// Importa la entidad Estudiante del dominio
import modelado.Estudiante;
// Importa la clase de conexión y el DAO de Estudiante
import modelado.ConexionBD;
import modelado.EstudianteDAO;

// Importa componentes Swing para diálogos
import javax.swing.*;
// Importa Connection para JDBC
import java.sql.Connection;
// Importa List para retornar colecciones
import java.util.List;

/**
 * Controlador de Estudiante - Gestiona operaciones CRUD sobre estudiantes
 * 
 * Este controlador proporciona métodos para realizar operaciones de creación,
 * lectura, actualización y eliminación (CRUD) de registros de estudiantes.
 * Sirve como intermediario entre la vista y el DAO, validando datos y mostrando
 * mensajes al usuario.
 * 
 * Uso desde la vista:
 *   EstudianteControlador ctrl = new EstudianteControlador();
 *   ctrl.registrar(datos...);
 *   ctrl.actualizar(datos...);
 *   ctrl.buscar(numDoc);
 *   ctrl.eliminar(numDoc);
 * 
 * El controlador maneja validaciones básicas y muestra diálogos informativos.
 */
public class EstudianteControlador {

    // DAO que realiza las operaciones en base de datos
    private EstudianteDAO dao;

    /**
     * Constructor que inicializa el controlador.
     * Obtiene la conexión a la BD y crea instancia del DAO.
     */
    public EstudianteControlador() {
        // Obtiene la conexión única a la BD usando el patrón Singleton
        Connection conn = ConexionBD.getInstancia().getConexion();
        // Crea el DAO pasándole la conexión
        this.dao = new EstudianteDAO(conn);
    }

    // ────────────────────────────────────────────────────────────────────────
    // OPERACIÓN CREATE - Insertar nuevo estudiante
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo estudiante en la base de datos.
     * Valida que los campos obligatorios no estén vacíos
     * y que no exista ya un estudiante con ese documento.
     * 
     * @param numDoc Número de documento del estudiante (PK)
     * @param tipoDoc Tipo de documento (Cédula, Pasaporte, etc.)
     * @param nombre Nombre del estudiante
     * @param apellido Apellido del estudiante
     * @param correo Correo electrónico institucional
     * @param contrasena Contraseña del estudiante
     * @param estado Estado (Activo/Inactivo)
     * @param idPrograma ID del programa académico
     * @param idpractica ID de la práctica asignada
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrar(String numDoc, String tipoDoc, String nombre, String apellido,
                             String correo, String contrasena, String estado, String idPrograma, String idpractica) {
        // Valida que los campos obligatorios no estén vacíos
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            // Muestra un mensaje de error
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        // Verifica que no exista ya un estudiante con ese número de documento
        if (dao.buscarPorDocumento(numDoc) != null) {
            // Muestra un mensaje indicando que ya existe
            mostrarError("Ya existe un estudiante con ese número de documento.");
            return false;
        }
        // Crea una nueva instancia de Estudiante con los datos proporcionados
        Estudiante e = new Estudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma, idpractica);
        // Intenta insertar el estudiante en la BD
        boolean ok = dao.insertar(e);
        // Muestra un mensaje de éxito o error según el resultado
        if (ok) mostrarInfo("Estudiante registrado exitosamente.");
        else    mostrarError("No se pudo registrar el estudiante. Verifica los datos.");
        return ok;
    }

    // ────────────────────────────────────────────────────────────────────────
    // OPERACIÓN READ - Consultar estudiantes
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Busca un estudiante por su número de documento.
     * 
     * @param numDoc Número de documento del estudiante
     * @return El objeto Estudiante si se encuentra, null si no existe
     */
    public Estudiante buscar(String numDoc) {
        // Consulta el DAO para obtener el estudiante
        Estudiante e = dao.buscarPorDocumento(numDoc);
        // Si no se encuentra, muestra un mensaje de error
        if (e == null) mostrarError("No se encontró ningún estudiante con ese documento.");
        return e;
    }

    /**
     * Retorna todos los estudiantes registrados en la BD.
     * 
     * @return Lista de todos los estudiantes
     */
    public List<Estudiante> listarTodos() {
        // Consulta el DAO para obtener todos los estudiantes
        return dao.listarTodos();
    }

    /**
     * Retorna los estudiantes de un programa académico específico.
     * 
     * @param idPrograma ID del programa académico
     * @return Lista de estudiantes del programa especificado
     */
    public List<Estudiante> listarPorPrograma(String idPrograma) {
        // Consulta el DAO para obtener estudiantes del programa
        return dao.listarPorPrograma(idPrograma);
    }

    // ────────────────────────────────────────────────────────────────────────
    // OPERACIÓN UPDATE - Actualizar estudiante
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos de un estudiante existente.
     * Valida que los campos obligatorios no estén vacíos.
     * 
     * @param numDoc Número de documento del estudiante a actualizar
     * @param tipoDoc Tipo de documento
     * @param nombre Nuevo nombre
     * @param apellido Nuevo apellido
     * @param correo Nuevo correo
     * @param contrasena Nueva contraseña
     * @param estado Nuevo estado
     * @param idPrograma Nuevo programa
     * @param idpractica Nueva práctica asignada
     * @return true si se actualizó correctamente
     */
    public boolean actualizar(String numDoc, String tipoDoc, String nombre, String apellido,
                              String correo, String contrasena, String estado, String idPrograma, String idpractica) {
        // Valida que nombre y correo no estén vacíos
        if (nombre.isEmpty() || correo.isEmpty()) {
            // Muestra un mensaje de error
            mostrarError("Nombre y correo no pueden estar vacíos.");
            return false;
        }
        // Crea una nueva instancia de Estudiante con los datos actualizados
        Estudiante e = new Estudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma, idpractica);
        // Intenta actualizar el estudiante en la BD
        boolean ok = dao.actualizar(e);
        // Muestra un mensaje de éxito o error según el resultado
        if (ok) mostrarInfo("Estudiante actualizado correctamente.");
        else    mostrarError("No se pudo actualizar. Verifica que el estudiante exista.");
        return ok;
    }

    /**
     * Cambia el estado de un estudiante (Activo a Inactivo o viceversa).
     * 
     * @param numDoc Número de documento del estudiante
     * @param nuevoEstado Nuevo estado (Activo/Inactivo)
     * @return true si se cambió el estado correctamente
     */
    public boolean cambiarEstado(String numDoc, String nuevoEstado) {
        // Intenta cambiar el estado en el DAO
        boolean ok = dao.cambiarEstado(numDoc, nuevoEstado);
        // Muestra un mensaje de éxito o error según el resultado
        if (ok) mostrarInfo("Estado actualizado a: " + nuevoEstado);
        else    mostrarError("No se pudo cambiar el estado.");
        return ok;
    }

    // ────────────────────────────────────────────────────────────────────────
    // OPERACIÓN DELETE - Eliminar estudiante
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Elimina un estudiante después de solicitar confirmación al usuario.
     * Esta acción no se puede deshacer, por lo que se pide confirmación explícita.
     * 
     * @param numDoc Número de documento del estudiante a eliminar
     * @param parent Componente padre para el diálogo de confirmación
     * @return true si se eliminó correctamente, false si se canceló o falló
     */
    public boolean eliminar(String numDoc, JComponent parent) {
        // Muestra un diálogo de confirmación al usuario
        int confirm = JOptionPane.showConfirmDialog(
            parent,
            // Mensaje de confirmación
            "¿Estás seguro de eliminar al estudiante con documento " + numDoc + "?\nEsta acción no se puede deshacer.",
            // Título del diálogo
            "Confirmar eliminación",
            // Botones: Sí y No
            JOptionPane.YES_NO_OPTION,
            // Icono de advertencia
            JOptionPane.WARNING_MESSAGE
        );
        // Si el usuario no confirmó, retorna false
        if (confirm != JOptionPane.YES_OPTION) return false;

        // Intenta eliminar el estudiante en la BD
        boolean ok = dao.eliminar(numDoc);
        // Muestra un mensaje de éxito o error según el resultado
        if (ok) mostrarInfo("Estudiante eliminado.");
        else    mostrarError("No se pudo eliminar. Puede tener registros asociados.");
        return ok;
    }

    // ────────────────────────────────────────────────────────────────────────
    // MÉTODOS AUXILIARES - Mostrar diálogos
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Muestra un diálogo informativo al usuario.
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarInfo(String mensaje) {
        // Crea un diálogo informativo con ícono de información
        JOptionPane.showMessageDialog(null, mensaje, "SIGEP", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de error al usuario.
     * 
     * @param mensaje Mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        // Crea un diálogo de error con ícono de error
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
