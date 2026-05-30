package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla PRACTICA.
 * La validación de negocio y seguridad la hace el Trigger en Oracle.
 * El "Estado" se calcula de forma dinámica en Java al leer la fecha final.
 */
public class PracticaDAO {

    public PracticaDAO() {
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insertar(Practica p) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_incPractica(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, p.getIdPractica());
            cs.setDate(2, p.getFechaInicio());
            cs.setDate(3, p.getFechaFinal());
            cs.setString(4, p.getEntidad());
            cs.setString(5, p.getIdTipopractica());
            cs.setString(6, p.getNumDocTutor());
            cs.setString(7, p.getNumDocEstudiante());
            cs.setString(8, p.getNumDocAsesor());

            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Practica: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID ─────────────────────────────────────────────────────────
    public Practica buscarPorId(String idPractica) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return null;

        String sql = "{? = call PROYECTOSPP.fun_buscarPracticaId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idPractica);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Practica: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT por Estudiante ─────────────────────────────────────────────────
    public List<Practica> listarPorEstudiante(String numDocEstudiante) {
        List<Practica> lista = new ArrayList<>();
        if (numDocEstudiante == null || numDocEstudiante.isEmpty()) return lista;

        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarPracticasEst(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocEstudiante);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas: " + e.getMessage());
        }
        return lista;
    }

    // ── SELECT por Tutor ──────────────────────────────────────────────────────
    public List<Practica> listarPorTutor(String numDocTutor) {
        List<Practica> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarPracticasTut(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocTutor);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas: " + e.getMessage());
        }
        return lista;
    }

    // ── SELECT por Asesor ─────────────────────────────────────────────────────
    public List<Practica> listarPorAsesor(String numDocAsesor) {
        List<Practica> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarPracticasAse(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, numDocAsesor);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prácticas: " + e.getMessage());
        }
        return lista;
    }

    // ── SELECT Listar Todas ───────────────────────────────────────────────────
    public List<Practica> listarTodas() {
        List<Practica> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarTodasPracticas()}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar todas: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(Practica p) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_actPractica(?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setDate(1, p.getFechaInicio());
            cs.setDate(2, p.getFechaFinal());
            cs.setString(3, p.getEntidad());
            cs.setString(4, p.getIdTipopractica());
            cs.setString(5, p.getNumDocTutor());
            cs.setString(6, p.getNumDocEstudiante());
            cs.setString(7, p.getNumDocAsesor());
            cs.setString(8, p.getIdPractica());

            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Practica: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idPractica) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{call PROYECTOSPP.pro_eliPractica(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, idPractica);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Practica: " + e.getMessage());
            return false;
        }
    }

    // ── VALIDACIONES OPTIMIZADAS DE USO DE TIPO DE PRÁCTICA ───────────────────
    public boolean estaEnUsoActivo(String idTipopractica) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{? = call PROYECTOSPP.fun_usoActivoTipoPrac(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idTipopractica);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al validar uso activo: " + e.getMessage());
        }
        return false;
    }

    public boolean estaEnUsoCualquierEstado(String idTipopractica) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) return false;

        String sql = "{? = call PROYECTOSPP.fun_usoGralTipoPrac(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idTipopractica);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al validar uso general: " + e.getMessage());
        }
        return false;
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────
    private Practica mapear(ResultSet rs) throws SQLException {
        Date fechaInicio = rs.getDate("Fechainicio");
        Date fechaFinal = rs.getDate("Fechafinal");
        
        // 1. Calculamos el estado al vuelo
        String estadoCalculado = "Activo"; 
        
        if (fechaFinal != null) {
            java.time.LocalDate hoy = java.time.LocalDate.now();
            java.time.LocalDate finalPractica = fechaFinal.toLocalDate();
            
            // Si la fecha final es hoy o una fecha pasada, la marcamos como finalizada
            if (!finalPractica.isAfter(hoy)) {
                estadoCalculado = "Finalizado";
            }
        }

        // 2. Construimos el objeto inyectándole el estado que acabamos de calcular
        return new Practica(
                rs.getString("Idpractica"),
                fechaInicio,
                fechaFinal,
                rs.getString("Entidad"),
                estadoCalculado, 
                rs.getString("Idtipopractica"),
                rs.getString("Numdoctutor"),
                rs.getString("Numdocestudiante"),
                rs.getString("Numdocasesor"));
    }
}