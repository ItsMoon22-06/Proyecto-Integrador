package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para ASESOR_PEDAGOGICO implementando las estructuras de los
 * documentos de clase (Procedimientos Almacenados Independientes pro_).
 */
public class AsesorPedagogicoDAO {

    public AsesorPedagogicoDAO() {
    }

    public boolean insertar(AsesorPedagogico a) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_incAsesor(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, a.getNumDocumento());
            cs.setString(2, a.getTipoDocumento());
            cs.setString(3, a.getNombre());
            cs.setString(4, a.getApellido());
            cs.setString(5, a.getCorreoInst());
            cs.setString(6, a.getContrasena());
            cs.setString(7, a.getEstado());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Asesor: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(AsesorPedagogico a) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_actAsesor(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, a.getNumDocumento());
            cs.setString(2, a.getTipoDocumento());
            cs.setString(3, a.getNombre());
            cs.setString(4, a.getApellido());
            cs.setString(5, a.getCorreoInst());
            cs.setString(6, a.getContrasena());
            cs.setString(7, a.getEstado());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Asesor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_eliAsesor(?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, numDoc);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Asesor: " + e.getMessage());
            return false;
        }
    }

    public AsesorPedagogico buscarPorDocumento(String numDoc) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        // Estructura JDBC para Funciones: {? = call nombre_funcion(?)}
        String sql = "{? = call PROYECTOSPP.fun_buscarAsesorDoc(?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            // El parámetro 1 es el valor que RETORNA la función (el SYS_REFCURSOR)
            cs.registerOutParameter(1, OracleTypes.CURSOR);

            // El parámetro 2 es el primer argumento de entrada (v_numdoc)
            cs.setString(2, numDoc);

            cs.execute();

            // Obtenemos el ResultSet desde el parámetro 1 (el retorno)
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Asesor por función: " + e.getMessage());
        }
        return null;
    }

    public AsesorPedagogico login(String correo, String contrasena) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return null;

        // Parámetro 1: Retorno | Parámetro 2: Correo | Parámetro 3: Contraseña
        String sql = "{? = call PROYECTOSPP.fun_loginAsesor(?, ?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR); // Retorno
            cs.setString(2, correo); // Entrada 1
            cs.setString(3, contrasena); // Entrada 2

            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en login Asesor por función: " + e.getMessage());
        }
        return null;
    }

    public List<AsesorPedagogico> listarTodos() {
        List<AsesorPedagogico> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return lista;

        // Como esta función no pide parámetros de entrada, solo abrimos un '?' para el
        // retorno
        String sql = "{? = call PROYECTOSPP.fun_listarAsesores}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR); // El único parámetro es el retorno

            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Asesores por función: " + e.getMessage());
        }
        return lista;
    }

    private AsesorPedagogico mapear(ResultSet rs) throws SQLException {
        return new AsesorPedagogico(
                rs.getString("Numdocumento"), rs.getString("Tipodocumento"),
                rs.getString("Nombre"), rs.getString("Apellido"),
                rs.getString("CorreoInst"), rs.getString("Contrasena"),
                rs.getString("Estado"));
    }
}