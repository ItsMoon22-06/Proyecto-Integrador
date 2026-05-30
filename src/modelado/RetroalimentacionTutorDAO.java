package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla RETROALIMENTACION_T (Tutor Académico).
 * Las transacciones y consultas son manejadas por Oracle a través de Procedimientos y Funciones.
 */
public class RetroalimentacionTutorDAO {

    public RetroalimentacionTutorDAO() {
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(RetroalimentacionTutor r) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incRetroTutor(?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, r.getIdRetroalimentacion());
            cs.setString(2, r.getComentario());
            cs.setDate(3, r.getFecha());
            cs.setString(4, r.getIdBitacora());
            cs.setString(5, r.getNumDocTutor());
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar RetroalimentacionTutor: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID ─────────────────────────────────────────────────────────
    public RetroalimentacionTutor buscarPorId(String id) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarRetroTutorId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, id);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar RetroalimentacionTutor: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT por Bitácora ───────────────────────────────────────────────────
    public List<RetroalimentacionTutor> listarPorBitacora(String idBitacora) {
        List<RetroalimentacionTutor> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarRetroTutorBitacora(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idBitacora);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Retroalimentaciones Tutor: " + e.getMessage());
        }
        return lista;
    }

    // ── SELECT por Tutor ──────────────────────────────────────────────────────
    public List<RetroalimentacionTutor> listarPorTutor(String numDocTutor) {
        List<RetroalimentacionTutor> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarRetroTutores(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocTutor);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar por tutor: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizarComentario(String id, String nuevoComentario) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_actRetroTutor(?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, nuevoComentario);
            cs.setString(2, id);
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar RetroalimentacionTutor: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String id) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliRetroTutor(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, id);
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar RetroalimentacionTutor: " + e.getMessage());
            return false;
        }
    }

    // ── MAPEO ─────────────────────────────────────────────────────────────────
    private RetroalimentacionTutor mapear(ResultSet rs) throws SQLException {
        return new RetroalimentacionTutor(
                rs.getString("Idretroalimentacion"),
                rs.getString("Comentario"),
                rs.getDate("Fecha"),
                rs.getString("Idbitacora"),
                rs.getString("NumdocTutor"));
    }
}