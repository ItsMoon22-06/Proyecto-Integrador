package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla RETROALIMENTACION_A (Asesor Pedagógico).
 * Las transacciones y consultas son manejadas por Oracle a través de Procedimientos y Funciones.
 */
public class RetroalimentacionAsesorDAO {

    public RetroalimentacionAsesorDAO() {
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(RetroalimentacionAsesor r) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incRetroAsesor(?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, r.getIdRetroalimentacion());
            cs.setString(2, r.getComentario());
            cs.setDate(3, r.getFecha());
            cs.setString(4, r.getIdBitacora());
            cs.setString(5, r.getNumDocAsesor());
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar RetroalimentacionAsesor: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID ─────────────────────────────────────────────────────────
    public RetroalimentacionAsesor buscarPorId(String id) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarRetroAsesorId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, id);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar RetroalimentacionAsesor: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT por Bitácora ───────────────────────────────────────────────────
    public List<RetroalimentacionAsesor> listarPorBitacora(String idBitacora) {
        List<RetroalimentacionAsesor> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarRetroAsesorBitacora(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idBitacora);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Retroalimentaciones Asesor: " + e.getMessage());
        }
        return lista;
    }

    // ── SELECT por Asesor ─────────────────────────────────────────────────────
    public List<RetroalimentacionAsesor> listarPorAsesor(String numDocAsesor) {
        List<RetroalimentacionAsesor> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarRetroAsesores(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocAsesor);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar por asesor: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizarComentario(String id, String nuevoComentario) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_actRetroAsesor(?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, nuevoComentario);
            cs.setString(2, id);
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar RetroalimentacionAsesor: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String id) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliRetroAsesor(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, id);
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar RetroalimentacionAsesor: " + e.getMessage());
            return false;
        }
    }

    // ── MAPEO ─────────────────────────────────────────────────────────────────
    private RetroalimentacionAsesor mapear(ResultSet rs) throws SQLException {
        return new RetroalimentacionAsesor(
                rs.getString("Idretroalimentacion"),
                rs.getString("Comentario"),
                rs.getDate("Fecha"),
                rs.getString("Idbitacora"),
                rs.getString("NumdocAsesor"));
    }
}