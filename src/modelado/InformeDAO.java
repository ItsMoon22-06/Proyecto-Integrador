package modelado;

import java.sql.*;
import java.util.*;
import oracle.jdbc.OracleTypes;

/**
 * DAO de consultas analíticas para el módulo de Informes del Director.
 * Integrado 100% con las funciones analíticas de Oracle.
 */
public class InformeDAO {

    public InformeDAO() {
    }

    // ════════════════════════════════════════════════════════════════════
    // 1. INFORME GENERAL DE PRÁCTICAS
    // ════════════════════════════════════════════════════════════════════

    public Map<String, Integer> contarPorEstado() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return mapa;

        String sql = "{? = call PROYECTOSPP.fun_inf_estadoPracticas()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) mapa.put(rs.getString("ESTADO"), rs.getInt("TOTAL"));
            }
        } catch (SQLException e) {
            System.err.println("Error contarPorEstado: " + e.getMessage());
        }
        return mapa;
    }

    public int totalPracticas() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return 0;

        String sql = "{? = call PROYECTOSPP.fun_inf_totalPracticas()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            System.err.println("Error totalPracticas: " + e.getMessage());
        }
        return 0;
    }

    public List<Object[]> topEntidades(int limite) {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_topEntidades(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, limite);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("ENTIDAD"), rs.getInt("TOTAL")});
            }
        } catch (SQLException e) {
            System.err.println("Error topEntidades: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> topTutores(int limite) {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_topTutores(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, limite);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("NOMBRE"), rs.getInt("TOTAL")});
            }
        } catch (SQLException e) {
            System.err.println("Error topTutores: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> topAsesores(int limite) {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_topAsesores(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, limite);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("NOMBRE"), rs.getInt("TOTAL")});
            }
        } catch (SQLException e) {
            System.err.println("Error topAsesores: " + e.getMessage());
        }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════════
    // 2. INFORME POR TIPO DE PRÁCTICA
    // ════════════════════════════════════════════════════════════════════

    public List<Object[]> estudiantesPorTipo() {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_estudiantesTipo()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("TIPO"), rs.getInt("TOTAL")});
            }
        } catch (SQLException e) {
            System.err.println("Error estudiantesPorTipo: " + e.getMessage());
        }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════════
    // 3. INFORME DE BITÁCORAS
    // ════════════════════════════════════════════════════════════════════

    public int totalBitacoras() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return 0;

        String sql = "{? = call PROYECTOSPP.fun_inf_totalBitacoras()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            System.err.println("Error totalBitacoras: " + e.getMessage());
        }
        return 0;
    }

    public double promedioEvidencias() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return 0.0;

        String sql = "{? = call PROYECTOSPP.fun_inf_promEvidencias()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error promedioEvidencias: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Object[]> practicasSinEvidencias() {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_pracSinEvidencias()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("IDPRACTICA"), rs.getString("ENTIDAD"), rs.getString("ESTUDIANTE")});
            }
        } catch (SQLException e) {
            System.err.println("Error practicasSinEvidencias: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Integer> bitacorasPorEstado() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return mapa;

        String sql = "{? = call PROYECTOSPP.fun_inf_bitacorasEstado()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) mapa.put(rs.getString("ESTADO"), rs.getInt("TOTAL"));
            }
        } catch (SQLException e) {
            System.err.println("Error bitacorasPorEstado: " + e.getMessage());
        }
        return mapa;
    }

    // ════════════════════════════════════════════════════════════════════
    // 4. INFORME DE RETROALIMENTACIONES
    // ════════════════════════════════════════════════════════════════════

    public int totalRetroTutor() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return 0;
        String sql = "{? = call PROYECTOSPP.fun_inf_totalRetroTutor()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            System.err.println("Error totalRetroTutor: " + e.getMessage());
        }
        return 0;
    }

    public int totalRetroAsesor() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return 0;
        String sql = "{? = call PROYECTOSPP.fun_inf_totalRetroAsesor()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            System.err.println("Error totalRetroAsesor: " + e.getMessage());
        }
        return 0;
    }

    public List<Object[]> topTutoresPorRetro(int limite) {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_topTutoresRetro(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, limite);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("NOMBRE"), rs.getInt("TOTAL")});
            }
        } catch (SQLException e) {
            System.err.println("Error topTutoresPorRetro: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> topAsesoresPorRetro(int limite) {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_topAsesoresRet(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, limite);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("NOMBRE"), rs.getInt("TOTAL")});
            }
        } catch (SQLException e) {
            System.err.println("Error topAsesoresPorRetro: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> bitacorasSinRetroTutor() {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_bitacorasSinRetro()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("IDPRACTICA"), rs.getString("ENTIDAD"), rs.getString("ESTUDIANTE")});
            }
        } catch (SQLException e) {
            System.err.println("Error bitacorasSinRetroTutor: " + e.getMessage());
        }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════════
    // 5. INFORME DE ENTIDADES RECEPTORAS
    // ════════════════════════════════════════════════════════════════════

    public List<Object[]> estadisticasEntidades() {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_estadisticasEnt()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("ENTIDAD"), rs.getInt("TOTAL"), rs.getInt("ACTIVAS"), rs.getInt("FINALIZADAS")});
            }
        } catch (SQLException e) {
            System.err.println("Error estadisticasEntidades: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> historialEstudiantesPorEntidad(String entidad) {
        List<Object[]> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_historialEntidad(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, entidad);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(new Object[]{rs.getString("ESTUDIANTE"), rs.getString("TIPO"), rs.getDate("FECHAINICIO"), rs.getDate("FECHAFINAL"), rs.getString("ESTADO")});
            }
        } catch (SQLException e) {
            System.err.println("Error historialEstudiantesPorEntidad: " + e.getMessage());
        }
        return lista;
    }

    public List<String> tiposPorEntidad(String entidad) {
        List<String> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_inf_tiposEntidad(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, entidad);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Error tiposPorEntidad: " + e.getMessage());
        }
        return lista;
    }

    public int totalEntidades() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return 0;

        String sql = "{? = call PROYECTOSPP.fun_inf_totalEntidades()}";
        try (CallableStatement cs = prepareCallCursor(conn, sql)) {
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error totalEntidades: " + e.getMessage());
        }
        return 0;
    }

    // ── Helper interno para no repetir OracleTypes.CURSOR ─────────────
    private CallableStatement prepareCallCursor(Connection conn, String sql) throws SQLException {
        CallableStatement cs = conn.prepareCall(sql);
        cs.registerOutParameter(1, OracleTypes.CURSOR);
        return cs;
    }
}