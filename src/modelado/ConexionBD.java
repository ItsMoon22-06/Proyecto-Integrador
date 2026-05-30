package modelado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST    = "192.168.254.215";
    private static final String PUERTO  = "1521";
    private static final String SID     = "orcl";

    private static String usuarioActual  = "PROYECTOSPP";
    private static String passwordActual = "PROYECTOSPP";

    private static final String URL =
            "jdbc:oracle:thin:@" + HOST + ":" + PUERTO + ":" + SID;

    // ── Singleton ───────────────────────────────────────────────────────────
    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() {}

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // ── Gestión de Roles ────────────────────────────────────────────────────
    public synchronized void conectarConRol(String rol) {
        cerrar(); // Cerrar conexión anterior antes de cambiar credenciales

        switch (rol.toLowerCase()) {
            case "director":
                usuarioActual  = "DIRECTOR";
                passwordActual = "DIRECTOR";
                break;
            case "estudiante":
                usuarioActual  = "ESTUDIANTE";
                passwordActual = "ESTUDIANTE";
                break;
            case "tutor_academico":
                usuarioActual  = "TUTOR_ACADEMICO";
                passwordActual = "TUTOR_ACADEMICO";
                break;
            case "asesor_pedagogico":
                usuarioActual  = "ASESOR_PEDAGOGICO";
                passwordActual = "ASESOR_PEDAGOGICO";
                break;
            default: // "admin" o cualquier valor desconocido
                usuarioActual  = "PROYECTOSPP";
                passwordActual = "PROYECTOSPP";
                break;
        }
        System.out.println("🔄 Perfil configurado para: " + usuarioActual);
    }

    // ── Conexión ────────────────────────────────────────────────────────────
    public synchronized Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("oracle.jdbc.OracleDriver");
                conexion = DriverManager.getConnection(URL, usuarioActual, passwordActual);
                System.out.println("✔ Conexión establecida como: " + usuarioActual);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✘ Driver Oracle no encontrado.");
            conexion = null;
        } catch (SQLException e) {
            System.err.println("✘ Error SQL al conectar: " + e.getMessage());
            conexion = null;
        }
        return conexion;
    }

    public synchronized void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✔ Conexión de " + usuarioActual + " cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("✘ Error al cerrar: " + e.getMessage());
        } finally {
            conexion = null;
        }
    }
}