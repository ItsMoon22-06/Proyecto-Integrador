package controlador;

import modelado.AsesorPedagogico;
import modelado.Director;
import modelado.Estudiante;
import modelado.TutorAcademico;
import modelado.ConexionBD;
import modelado.AsesorPedagogicoDAO;
import modelado.DirectorDAO;
import modelado.EstudianteDAO;
import modelado.TutorAcademicoDAO;
import vistas.LoginWindow;
import vistas.PortalAsesorPedagogico;
import vistas.PortalDirector;
import vistas.PortalTutorAcademico;
import vistas.StudentDashboard;

import javax.swing.*;

public class LoginControlador {

    private final LoginWindow vista;

    // Todos los DAOs usan el Singleton internamente — sin parámetro conn
    private final EstudianteDAO    estudianteDAO = new EstudianteDAO();
    private final TutorAcademicoDAO  tutorDAO    = new TutorAcademicoDAO();
    private final AsesorPedagogicoDAO asesorDAO  = new AsesorPedagogicoDAO();
    private final DirectorDAO       directorDAO  = new DirectorDAO();

    public LoginControlador(LoginWindow vista) {
        this.vista = vista;
        if (vista == null) return;

        // Conectar como administrador para validar credenciales de login
        ConexionBD.getInstancia().conectarConRol("admin");

        // Solo verificamos que la BD esté disponible — no pasamos conn a nadie
        if (ConexionBD.getInstancia().getConexion() == null) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo conectar a la base de datos.\n" +
                    "Verifique que Oracle esté activo.\n" +
                    "Usuario: PROYECTOSPP | SID: XE",
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inicializarEventos();
    }

    private void inicializarEventos() {
        vista.getBtnIngresar().addActionListener(e -> procesarLogin());
    }

    private void procesarLogin() {
        String correo    = vista.getCorreo();
        String contrasena = vista.getContrasena();
        String rol       = vista.getRolSeleccionado();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor ingresa correo y contraseña.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rol.isEmpty() || rol.equalsIgnoreCase("Seleccione su rol")) {
            JOptionPane.showMessageDialog(vista,
                    "Selecciona un rol válido.",
                    "Rol inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (rol) {
            case "Estudiante"          -> loginEstudiante(correo, contrasena);
            case "Tutor académico"     -> loginTutor(correo, contrasena);
            case "Asesor pedagógico"   -> loginAsesor(correo, contrasena);
            case "Director"            -> loginDirector(correo, contrasena);
            default -> JOptionPane.showMessageDialog(vista,
                    "Rol no reconocido: " + rol,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Métodos de login por rol ─────────────────────────────────────────────

    private void loginEstudiante(String correo, String contrasena) {
        Estudiante est = estudianteDAO.login(correo, contrasena);
        if (est == null) {
            mostrarAccesoDenegado("Estudiante"); return;
        }
        if (estaInactivo(est.getEstado())) return;

        ConexionBD.getInstancia().conectarConRol("estudiante");
        transferirVentana(new StudentDashboard(est));
    }

    private void loginTutor(String correo, String contrasena) {
        TutorAcademico t = tutorDAO.login(correo, contrasena);
        if (t == null) {
            mostrarAccesoDenegado("Tutor Académico"); return;
        }
        if (estaInactivo(t.getEstado())) return;

        ConexionBD.getInstancia().conectarConRol("tutor_academico");
        transferirVentana(new PortalTutorAcademico(t));
    }

    private void loginAsesor(String correo, String contrasena) {
        AsesorPedagogico a = asesorDAO.login(correo, contrasena);
        if (a == null) {
            mostrarAccesoDenegado("Asesor Pedagógico"); return;
        }
        if (estaInactivo(a.getEstado())) return;

        ConexionBD.getInstancia().conectarConRol("asesor_pedagogico");
        transferirVentana(new PortalAsesorPedagogico(a));
    }

    private void loginDirector(String correo, String contrasena) {
        Director d = directorDAO.login(correo, contrasena);
        if (d == null) {
            mostrarAccesoDenegado("Director"); return;
        }
        if (estaInactivo(d.getEstado())) return;

        ConexionBD.getInstancia().conectarConRol("director");
        transferirVentana(new PortalDirector(d));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean estaInactivo(String estado) {
        if ("Inactivo".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(vista,
                    "El usuario se encuentra inactivo.\n" +
                    "Comuníquese con el administrador.",
                    "Usuario inactivo", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private void mostrarAccesoDenegado(String rol) {
        JOptionPane.showMessageDialog(vista,
                "Credenciales incorrectas para " + rol + ".",
                "Acceso denegado", JOptionPane.ERROR_MESSAGE);
    }

    private void transferirVentana(JFrame destino) {
        destino.setExtendedState(vista.getExtendedState());
        if (vista.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
            destino.setBounds(vista.getBounds());
        }
        vista.dispose();
        destino.setVisible(true);
    }
}