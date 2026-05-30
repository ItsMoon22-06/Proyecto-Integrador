package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla BITACORA usando Procedimientos y Funciones de Oracle 10g.
 */
public class BitacoraDAO {

    // Constructor recomendado
    public BitacoraDAO() {
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insertar(Bitacora b) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_incBitacora(?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, b.getIdBitacora());
            cs.setDate(2, b.getFechaCreacion());

            if (b.getNotaFinal() == 0)
                cs.setNull(3, Types.NUMERIC);
            else
                cs.setDouble(3, b.getNotaFinal());

            cs.setString(4, b.getObservacionFinal());
            cs.setString(5, b.getIdPractica());
            cs.setString(6, b.getEstado() != null ? b.getEstado() : "Activa");

            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Bitacora: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID (Función) ───────────────────────────────────────────────
    public Bitacora buscarPorId(String idBitacora) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarBitacoraId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idBitacora);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next())
                    return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT por Práctica (Función) ─────────────────────────────────────────
    public Bitacora buscarPorPractica(String idPractica) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarBitacoraPrac(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idPractica);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next())
                    return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT por Estudiante y Práctica (Función) ────────────────────────────
    public Bitacora buscarPorEstudianteYPractica(String numDocEstudiante, String idPractica) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarBitEstPrac(?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocEstudiante);
            cs.setString(3, idPractica);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next())
                    return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT Listar por Estudiante (Función) ────────────────────────────────
    public List<Bitacora> listarPorEstudiante(String numDocEstudiante) {
        List<Bitacora> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarBitacoraEst(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocEstudiante);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next())
                    lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE (Cerrar bitácora) ──────────────────────────────────────────────
    public boolean cerrarBitacora(String idBitacora, double nota, String observacion) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_cerrarBitacora(?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, idBitacora);
            cs.setDouble(2, nota);
            cs.setString(3, observacion);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al cerrar bitácora: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idBitacora) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_eliBitacora(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, idBitacora);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────
    private Bitacora mapear(ResultSet rs) throws SQLException {
        Bitacora b = new Bitacora();
        b.setIdBitacora(rs.getString("Idbitacora"));
        b.setFechaCreacion(rs.getDate("Fechacreacion"));
        b.setNotaFinal(rs.getDouble("Notafinal"));
        b.setObservacionFinal(rs.getString("Observacionfinal"));
        b.setIdPractica(rs.getString("Idpractica"));
        b.setEstado(rs.getString("Estado"));
        return b;
    }
}