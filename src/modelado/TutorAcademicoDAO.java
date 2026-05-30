package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;


public class TutorAcademicoDAO {

    public TutorAcademicoDAO() {
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(TutorAcademico t) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incTutor(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, t.getNumDocumento());
            cs.setString(2, t.getTipoDocumento());
            cs.setString(3, t.getNombre());
            cs.setString(4, t.getApellido());
            cs.setString(5, t.getCorreoInst());
            cs.setString(6, t.getContrasena());
            cs.setString(7, t.getEstado());
            cs.setString(8, t.getIdPrograma()); // Dato obligatorio, se pasa directamente

            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Tutor: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por Documento ──────────────────────────────────────────────────
    public TutorAcademico buscarPorDocumento(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarTutorDoc(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDoc);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Tutor: " + e.getMessage());
        }
        return null;
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public TutorAcademico login(String correo, String contrasena) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_loginTutor(?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, correo);
            cs.setString(3, contrasena);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en login Tutor: " + e.getMessage());
        }
        return null; // Retorna null si las credenciales fallan o si el estado no es "Activo"
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    public List<TutorAcademico> listarTodos() {
        List<TutorAcademico> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarTutores()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Tutores: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public String actualizar(TutorAcademico t) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return "Error: No hay conexión a la base de datos.";

        String sql = "{call PROYECTOSPP.pro_actTutor(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, t.getTipoDocumento());
            cs.setString(2, t.getNombre());
            cs.setString(3, t.getApellido());
            cs.setString(4, t.getCorreoInst());
            cs.setString(5, t.getContrasena());
            cs.setString(6, t.getEstado());
            cs.setString(7, t.getIdPrograma()); // Dato obligatorio, se pasa directamente
            cs.setString(8, t.getNumDocumento());

            cs.execute();
            return "OK"; // Retorna OK si todo fue exitoso
            
        } catch (SQLException e) {
            // El código 20031 es el que configuramos en el Trigger de Oracle para el Tutor
            if (e.getErrorCode() == 20031) {
                return "No se puede cambiar el programa académico porque el tutor tiene prácticas en curso.";
            }
            // Si es otro error de base de datos
            return "Error en la base de datos: " + e.getMessage();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliTutor(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, numDoc);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Tutor: " + e.getMessage());
            return false;
        }
    }

    // ── MAPEO ─────────────────────────────────────────────────────────────────
    private TutorAcademico mapear(ResultSet rs) throws SQLException {
        return new TutorAcademico(
                rs.getString("Numdocumento"), 
                rs.getString("Tipodocumento"),
                rs.getString("Nombre"), 
                rs.getString("Apellido"),
                rs.getString("CorreoInst"), 
                rs.getString("Contrasena"),
                rs.getString("Estado"), 
                rs.getString("Idprograma"));
    }
}