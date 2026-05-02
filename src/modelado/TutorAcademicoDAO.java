package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla TUTOR_ACADEMICO.
 */
public class TutorAcademicoDAO {

    private Connection conn;

    public TutorAcademicoDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertar(TutorAcademico t) {
        String sql = "INSERT INTO Tutor_academico (Numdocumento, Tipodocumento, Nombre, Apellido, " +
                     "CorreoInst, Contrasena, Estado, Idprograma) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNumDocumento());
            ps.setString(2, t.getTipoDocumento());
            ps.setString(3, t.getNombre());
            ps.setString(4, t.getApellido());
            ps.setString(5, t.getCorreoInst());
            ps.setString(6, t.getContrasena());
            ps.setString(7, t.getEstado());
            ps.setString(8, t.getIdPrograma());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Tutor: " + e.getMessage());
            return false;
        }
    }

    public TutorAcademico buscarPorDocumento(String numDoc) {
        String sql = "SELECT * FROM Tutor_academico WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDoc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar Tutor: " + e.getMessage());
        }
        return null;
    }

    /** Login por correo y contraseña. */
    public TutorAcademico login(String correo, String contrasena) {
        String sql = "SELECT * FROM Tutor_academico WHERE CorreoInst = ? AND Contrasena = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error en login Tutor: " + e.getMessage());
        }
        return null;
    }

    public List<TutorAcademico> listarTodos() {
        List<TutorAcademico> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tutor_academico";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar Tutores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(TutorAcademico t) {
        String sql = "UPDATE Tutor_academico SET Tipodocumento=?, Nombre=?, Apellido=?, " +
                     "CorreoInst=?, Contrasena=?, Estado=?, Idprograma=? WHERE Numdocumento=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTipoDocumento());
            ps.setString(2, t.getNombre());
            ps.setString(3, t.getApellido());
            ps.setString(4, t.getCorreoInst());
            ps.setString(5, t.getContrasena());
            ps.setString(6, t.getEstado());
            ps.setString(7, t.getIdPrograma());
            ps.setString(8, t.getNumDocumento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Tutor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String numDoc) {
        String sql = "DELETE FROM Tutor_academico WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Tutor: " + e.getMessage());
            return false;
        }
    }

    private TutorAcademico mapear(ResultSet rs) throws SQLException {
        return new TutorAcademico(
            rs.getString("Numdocumento"), rs.getString("Tipodocumento"),
            rs.getString("Nombre"),       rs.getString("Apellido"),
            rs.getString("CorreoInst"),   rs.getString("Contrasena"),
            rs.getString("Estado"),       rs.getString("Idprograma")
        );
    }
}
