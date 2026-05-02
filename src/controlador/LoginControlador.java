// Declara el paquete al que pertenece esta clase
package controlador;

// Importa las clases de entidades del dominio (modelo)
import modelado.AsesorPedagogico;
import modelado.Estudiante;
import modelado.TutorAcademico;
import modelado.ConexionBD;
import modelado.AsesorPedagogicoDAO;
import modelado.EstudianteDAO;
import modelado.TutorAcademicoDAO;
// Importa las vistas (interfaz gráfica)
import vistas.LoginWindow;
import vistas.PortalAsesorPedagogico;
import vistas.PortalTutorAcademico;
import vistas.StudentDashboard;

// Importa componentes Swing para la interfaz gráfica
import javax.swing.*;
// Importa la conexión JDBC
import java.sql.Connection;

/**
 * Controlador de Login - Gestiona el proceso de autenticación
 * 
 * Este controlador implementa la lógica de autenticación del sistema.
 * Conecta la vista LoginWindow con los DAOs correspondientes para validar
 * credenciales y abrir el portal adecuado según el rol del usuario.
 * 
 * Flujo de aplicación:
 * SplashWindow → LoginWindow → LoginControlador → 
 * (StudentDashboard | PortalTutorAcademico | PortalAsesorPedagogico)
 * 
 * Roles soportados:
 * - Estudiante: Accede a StudentDashboard
 * - Tutor Académico: Accede a PortalTutorAcademico
 * - Asesor Pedagógico: Accede a PortalAsesorPedagogico
 */
public class LoginControlador {

    // Referencia a la vista de login
    private LoginWindow vista;

    // DAOs para consultar datos de los diferentes tipos de usuarios
    private EstudianteDAO estudianteDAO;
    private TutorAcademicoDAO tutorDAO;
    private AsesorPedagogicoDAO asesorDAO;

    /**
     * Constructor que inicializa el controlador.
     * Establece la conexión a la BD, crea los DAOs necesarios e inicializa los listeners.
     * 
     * @param vista La ventana de login a controlar
     */
    public LoginControlador(LoginWindow vista) {
        // Asigna la vista recibida
        this.vista = vista;
        // Si la vista es null (ej: durante logout), detiene el proceso
        if (vista == null)
            return;

        // Obtiene la conexión única a la BD (patrón Singleton)
        Connection conn = ConexionBD.getInstancia().getConexion();

        // Si la conexión falla, muestra un mensaje de error y detiene
        if (conn == null) {
            // Crea un mensaje con instrucciones para el usuario
            JOptionPane.showMessageDialog(vista, """
                                                 No se pudo conectar a la base de datos.
                                                 Verifique que Oracle está activo en 192.168.254.215:1521
                                                 Usuario: PROYECTOSIGEP | SID: orcl
                                                 y que el driver ojdbc11.jar esté en el classpath.""",
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crea instancias de los DAOs con la conexión establecida
        // Cada DAO se responsabiliza de las operaciones de su tipo de usuario
        this.estudianteDAO = new EstudianteDAO(conn);
        this.tutorDAO = new TutorAcademicoDAO(conn);
        this.asesorDAO = new AsesorPedagogicoDAO(conn);

        // Configura los listeners de eventos de la vista
        inicializarEventos();
    }

    // ────────────────────────────────────────────────────────────────────────
    // INICIALIZACIÓN DE EVENTOS
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Inicializa los listeners de eventos de la vista.
     * Adjunta el método procesarLogin() al botón "Ingresar".
     */
    private void inicializarEventos() {
        // Cuando el usuario hace clic en "Ingresar", se ejecuta procesarLogin()
        vista.getBtnIngresar().addActionListener(e -> procesarLogin());
    }

    // ────────────────────────────────────────────────────────────────────────
    // LÓGICA DE AUTENTICACIÓN
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Procesa el login del usuario.
     * Valida que los campos obligatorios estén completos,
     * verifica el rol seleccionado y delega al método de login correspondiente.
     */
    private void procesarLogin() {
        // Obtiene el correo ingresado por el usuario
        String correo = vista.getCorreo();
        // Obtiene la contraseña ingresada
        String contrasena = vista.getContrasena();
        // Obtiene el rol seleccionado en el combo
        String rol = vista.getRolSeleccionado();

        // Valida que correo y contraseña no estén vacíos
        if (correo.isEmpty() || contrasena.isEmpty()) {
            // Muestra un mensaje de advertencia
            JOptionPane.showMessageDialog(vista,
                    "Por favor ingresa correo y contraseña.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Valida que se haya seleccionado un rol válido
        if (rol.isEmpty() || rol.equalsIgnoreCase("Seleccione su rol")) {
            // Muestra un mensaje indicando que debe seleccionar un rol
            JOptionPane.showMessageDialog(vista,
                    "Selecciona un rol válido.",
                    "Rol inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Según el rol seleccionado, ejecuta el método de login correspondiente
        switch (rol) {
            case "Estudiante" -> loginEstudiante(correo, contrasena);
            case "Tutor académico", "Tutor Académico" -> loginTutor(correo, contrasena);
            case "Asesor pedagógico", "Asesor Pedagógico" -> loginAsesor(correo, contrasena);
            default -> JOptionPane.showMessageDialog(vista,
                    "Rol no reconocido: " + rol,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Realiza la autenticación de un estudiante.
     * Si tiene éxito, abre el panel StudentDashboard.
     * 
     * @param correo Correo del estudiante
     * @param contrasena Contraseña del estudiante
     */
    private void loginEstudiante(String correo, String contrasena) {
        // Consulta la BD buscando un estudiante con esas credenciales
        Estudiante estudiante = estudianteDAO.login(correo, contrasena);
        // Si el login fue exitoso
        if (estudiante != null) {
            // Crea el dashboard del estudiante pasándole el usuario autenticado
            StudentDashboard dashboard = new StudentDashboard(estudiante);
            // Copia el estado de la ventana de login (maximizado, normal, etc)
            dashboard.setExtendedState(vista.getExtendedState());
            // Si la ventana no está maximizada, copia su tamaño y posición
            if (vista.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                dashboard.setBounds(vista.getBounds());
            }
            // Cierra la ventana de login
            vista.dispose();
            // Muestra el dashboard
            dashboard.setVisible(true);
        } 
        // Si las credenciales son incorrectas
        else {
            // Muestra un mensaje de error
            JOptionPane.showMessageDialog(vista,
                    "Credenciales incorrectas para Estudiante.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Realiza la autenticación de un tutor académico.
     * Si tiene éxito, abre el portal PortalTutorAcademico.
     * 
     * @param correo Correo del tutor
     * @param contrasena Contraseña del tutor
     */
    private void loginTutor(String correo, String contrasena) {
        // Consulta la BD buscando un tutor con esas credenciales
        TutorAcademico tutor = tutorDAO.login(correo, contrasena);
        // Si el login fue exitoso
        if (tutor != null) {
            // Crea el portal del tutor pasándole el usuario autenticado
            PortalTutorAcademico portal = new PortalTutorAcademico(tutor);
            // Copia el estado de la ventana de login
            portal.setExtendedState(vista.getExtendedState());
            // Si la ventana no está maximizada, copia su tamaño y posición
            if (vista.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                portal.setBounds(vista.getBounds());
            }
            // Cierra la ventana de login
            vista.dispose();
            // Muestra el portal
            portal.setVisible(true);
        } 
        // Si las credenciales son incorrectas
        else {
            // Muestra un mensaje de error
            JOptionPane.showMessageDialog(vista,
                    "Credenciales incorrectas para Tutor Académico.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Realiza la autenticación de un asesor pedagógico.
     * Si tiene éxito, abre el portal PortalAsesorPedagogico.
     * 
     * @param correo Correo del asesor
     * @param contrasena Contraseña del asesor
     */
    private void loginAsesor(String correo, String contrasena) {
        // Consulta la BD buscando un asesor con esas credenciales
        AsesorPedagogico asesor = asesorDAO.login(correo, contrasena);
        // Si el login fue exitoso
        if (asesor != null) {
            // Crea el portal del asesor pasándole el usuario autenticado
            PortalAsesorPedagogico portal = new PortalAsesorPedagogico(asesor);
            // Copia el estado de la ventana de login
            portal.setExtendedState(vista.getExtendedState());
            // Si la ventana no está maximizada, copia su tamaño y posición
            if (vista.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                portal.setBounds(vista.getBounds());
            }
            // Cierra la ventana de login
            vista.dispose();
            // Muestra el portal
            portal.setVisible(true);
        } 
        // Si las credenciales son incorrectas
        else {
            // Muestra un mensaje de error
            JOptionPane.showMessageDialog(vista,
                    "Credenciales incorrectas para Asesor Pedagógico.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }
}
