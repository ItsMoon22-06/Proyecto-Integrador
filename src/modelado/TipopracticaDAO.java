package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla TIPOPRACTICA.
 * La validación del número de semestre (1-8) y la auditoría 
 * las realiza automáticamente el Trigger en Oracle.
 */
public class TipopracticaDAO {

    public TipopracticaDAO() {
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(Tipopractica t) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incTipoPrac(?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, t.getIdTipopractica());
            cs.setString(2, t.getNombre());
            cs.setInt(3, t.getNumSemestre());
            cs.setInt(4, t.getHorasRequeridas());
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            // Si el semestre es <1 o >8, el error de Oracle (-20010) se imprimirá aquí.
            System.err.println("Error al insertar Tipopractica: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID ─────────────────────────────────────────────────────────
    public Tipopractica buscarPorId(String id) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarTipoPracId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, id);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Tipopractica: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    public List<Tipopractica> listarTodos() {
        List<Tipopractica> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarTiposPrac()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Tipopracticas: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(Tipopractica t) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_actTipoPrac(?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, t.getNombre());
            cs.setInt(2, t.getNumSemestre());
            cs.setInt(3, t.getHorasRequeridas());
            cs.setString(4, t.getIdTipopractica());
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Tipopractica: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String id) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliTipoPrac(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Tipopractica: " + e.getMessage());
            return false;
        }
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────
    private Tipopractica mapear(ResultSet rs) throws SQLException {
        return new Tipopractica(
                rs.getString("IdTipopractica"),
                rs.getString("Nombre"),
                rs.getInt("Numsemestre"),
                rs.getInt("Horasrequeridas"));
    }
}