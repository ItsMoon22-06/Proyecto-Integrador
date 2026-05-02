package modelado;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla ASESOR_PEDAGOGICO.
 */
public class AsesorPedagogicoDAO {

    private final Connection conn;

    public AsesorPedagogicoDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertar(AsesorPedagogico a) {
        String sql = "INSERT INTO Asesor_pedagogico (Numdocumento, Tipodocumento, Nombre, Apellido, " +
                     "CorreoInst, Contrasena, Estado) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNumDocumento());
            ps.setString(2, a.getTipoDocumento());
            ps.setString(3, a.getNombre());
            ps.setString(4, a.getApellido());
            ps.setString(5, a.getCorreoInst());
            ps.setString(6, a.getContrasena());
            ps.setString(7, a.getEstado());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Asesor: " + e.getMessage());
            return false;
        }
    }

    public AsesorPedagogico buscarPorDocumento(String numDoc) {
        String sql = "SELECT * FROM Asesor_pedagogico WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDoc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar Asesor: " + e.getMessage());
        }
        return null;
    }

    /** Login por correo y contraseña. */
    public AsesorPedagogico login(String correo, String contrasena) {
        String sql = "SELECT * FROM Asesor_pedagogico WHERE CorreoInst = ? AND Contrasena = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error en login Asesor: " + e.getMessage());
        }
        return null;
    }

    public List<AsesorPedagogico> listarTodos() {
        List<AsesorPedagogico> lista = new ArrayList<>();
        String sql = "SELECT * FROM Asesor_pedagogico";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar Asesores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(AsesorPedagogico a) {
        String sql = "UPDATE Asesor_pedagogico SET Tipodocumento=?, Nombre=?, Apellido=?, " +
                     "CorreoInst=?, Contrasena=?, Estado=? WHERE Numdocumento=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTipoDocumento());
            ps.setString(2, a.getNombre());
            ps.setString(3, a.getApellido());
            ps.setString(4, a.getCorreoInst());
            ps.setString(5, a.getContrasena());
            ps.setString(6, a.getEstado());
            ps.setString(7, a.getNumDocumento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Asesor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String numDoc) {
        String sql = "DELETE FROM Asesor_pedagogico WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Asesor: " + e.getMessage());
            return false;
        }
    }

    private AsesorPedagogico mapear(ResultSet rs) throws SQLException {
        return new AsesorPedagogico(
            rs.getString("Numdocumento"), rs.getString("Tipodocumento"),
            rs.getString("Nombre"),       rs.getString("Apellido"),
            rs.getString("CorreoInst"),   rs.getString("Contrasena"),
            rs.getString("Estado")
        );
    }
}
