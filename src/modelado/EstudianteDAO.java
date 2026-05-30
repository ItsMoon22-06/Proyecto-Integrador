package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class EstudianteDAO {

    public EstudianteDAO() {
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insertar(Estudiante e) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_incEstudiante(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, e.getNumDocumento());
            cs.setString(2, e.getTipoDocumento());
            cs.setString(3, e.getNombre());
            cs.setString(4, e.getApellido());
            cs.setString(5, e.getCorreoInst());
            cs.setString(6, e.getContrasena());
            cs.setString(7, e.getEstado());
            cs.setString(8, e.getIdPrograma());
            cs.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al insertar Estudiante: " + ex.getMessage());
            return false;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public String actualizar(Estudiante e) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return "Error: No hay conexión a la base de datos.";

        String sql = "{call PROYECTOSPP.pro_actEstudiante(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, e.getNumDocumento());
            cs.setString(2, e.getTipoDocumento());
            cs.setString(3, e.getNombre());
            cs.setString(4, e.getApellido());
            cs.setString(5, e.getCorreoInst());
            cs.setString(6, e.getContrasena());
            cs.setString(7, e.getEstado());
            cs.setString(8, e.getIdPrograma());
            cs.execute();

            return "OK"; // Retorna OK si todo fue exitoso

        } catch (SQLException ex) {
            // El código 20030 es el que configuramos en el Trigger de Oracle
            if (ex.getErrorCode() == 20030) {
                return "No se puede cambiar el programa académico porque el estudiante tiene una práctica en curso.";
            }
            // Si es otro error de base de datos, lo mostramos también
            return "Error en la base de datos: " + ex.getMessage();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_eliEstudiante(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, numDoc);
            cs.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar Estudiante: " + ex.getMessage());
            return false;
        }
    }

    // ── SELECT por documento (Función) ────────────────────────────────────────
    public Estudiante buscarPorDocumento(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarEstudianteDoc(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDoc);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next())
                    return mapear(rs);
            }
        } catch (SQLException ex) {
            System.err.println("Error al buscar Estudiante: " + ex.getMessage());
        }
        return null;
    }

    // ── LOGIN (Función) ───────────────────────────────────────────────────────
    public Estudiante login(String correo, String contrasena) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        String sql = "{? = call PROYECTOSPP.fun_loginEstudiante(?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, correo);
            cs.setString(3, contrasena);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next())
                    return mapear(rs);
            }
        } catch (SQLException ex) {
            System.err.println("Error en login Estudiante: " + ex.getMessage());
        }
        return null;
    }

    // ── SELECT ALL (Función) ──────────────────────────────────────────────────
    public List<Estudiante> listarTodos() {
        List<Estudiante> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarEstudiantes}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next())
                    lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar Estudiantes: " + ex.getMessage());
        }
        return lista;
    }

    // ── SELECT por Programa (Función) ─────────────────────────────────────────
    public List<Estudiante> listarPorPrograma(String idPrograma) {
        List<Estudiante> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarEstudiantesProg(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idPrograma);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next())
                    lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar por programa: " + ex.getMessage());
        }
        return lista;
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────
    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
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