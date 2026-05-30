package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla PROGRAMA.
 * Utiliza Procedimientos Almacenados y Funciones de Oracle para todas las operaciones.
 */
public class ProgramaDAO {

    // Constructor limpio recomendado
    public ProgramaDAO() {
    }


    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(Programa p) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incPrograma(?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, p.getIdPrograma());
            cs.setString(2, p.getNombre());
            cs.setString(3, p.getFacultad());
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Programa por procedimiento: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID (Función con Cursor) ────────────────────────────────────
    public Programa buscarPorId(String idPrograma) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarProgramaId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idPrograma);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Programa por función: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT ALL (Función con Cursor) ───────────────────────────────────────
    public List<Programa> listarTodos() {
        List<Programa> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarProgramas()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Programas por función: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(Programa p) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_actPrograma(?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, p.getNombre());
            cs.setString(2, p.getFacultad());
            cs.setString(3, p.getIdPrograma());
            
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Programa por procedimiento: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idPrograma) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliPrograma(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, idPrograma);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Programa por procedimiento: " + e.getMessage());
            return false;
        }
    }

    // ── Mapeo ResultSet → Objeto ───────────────────────────────────────────────
    private Programa mapear(ResultSet rs) throws SQLException {
        return new Programa(
                rs.getString("Idprograma"),
                rs.getString("Nombre"),
                rs.getString("Facultad"));
    }
}