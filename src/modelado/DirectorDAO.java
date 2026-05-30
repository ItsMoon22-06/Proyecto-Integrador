package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class DirectorDAO {

    public DirectorDAO() {}

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insertar(Director d) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incDirector(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, d.getNumDocumento());
            cs.setString(2, d.getTipoDocumento());
            cs.setString(3, d.getNombre());
            cs.setString(4, d.getApellido());
            cs.setString(5, d.getCorreoInst());
            cs.setString(6, d.getContrasena());
            cs.setString(7, d.getEstado());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Director: " + e.getMessage());
            return false;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(Director d) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_actDirector(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, d.getNumDocumento());
            cs.setString(2, d.getTipoDocumento());
            cs.setString(3, d.getNombre());
            cs.setString(4, d.getApellido());
            cs.setString(5, d.getCorreoInst());
            cs.setString(6, d.getContrasena());
            cs.setString(7, d.getEstado());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Director: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliDirector(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, numDoc);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Director: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por documento (Función) ────────────────────────────────────────
    public Director buscarPorDocumento(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarDirectorDoc(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDoc);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Director: " + e.getMessage());
        }
        return null;
    }

    // ── LOGIN (Función) ───────────────────────────────────────────────────────
    public Director login(String correo, String contrasena) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_loginDirector(?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, correo);
            cs.setString(3, contrasena);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en login Director: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT ALL (Función) ──────────────────────────────────────────────────
    public List<Director> listarTodos() {
        List<Director> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarDirectores}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Directores: " + e.getMessage());
        }
        return lista;
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────
    private Director mapear(ResultSet rs) throws SQLException {
        return new Director(
                rs.getString("Numdocumento"),
                rs.getString("Tipodocumento"),
                rs.getString("Nombre"),
                rs.getString("Apellido"),
                rs.getString("CorreoInst"),
                rs.getString("Contrasena"),
                rs.getString("Estado"));
    }
}