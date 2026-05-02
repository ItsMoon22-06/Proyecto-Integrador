package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla RETROALIMENTACION.
 */
public class RetroalimentacionDAO {

    private final Connection conn;

    public RetroalimentacionDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertar(Retroalimentacion r) {
        String sql = "INSERT INTO Retroalimentacion (Idretroalimentacion, Comentario, Fecha, " +
                     "Idbitacora, NumdocTutor, NumdocAsesor) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getIdRetroalimentacion());
            ps.setString(2, r.getComentario());
            ps.setDate(3, r.getFecha());
            ps.setString(4, r.getIdBitacora());
            // Tutor o asesor pueden ser null (solo uno retroalimenta)
            if (r.getNumDocTutor() != null) ps.setString(5, r.getNumDocTutor());
            else ps.setNull(5, Types.VARCHAR);
            if (r.getNumDocAsesor() != null) ps.setString(6, r.getNumDocAsesor());
            else ps.setNull(6, Types.VARCHAR);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Retroalimentacion: " + e.getMessage());
            return false;
        }
    }

    public Retroalimentacion buscarPorId(String id) {
        String sql = "SELECT * FROM Retroalimentacion WHERE Idretroalimentacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar Retroalimentacion: " + e.getMessage());
        }
        return null;
    }

    /** Lista todas las retroalimentaciones de una bitácora. */
    public List<Retroalimentacion> listarPorBitacora(String idBitacora) {
        List<Retroalimentacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Retroalimentacion WHERE Idbitacora = ? ORDER BY Fecha";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idBitacora);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar Retroalimentaciones: " + e.getMessage());
        }
        return lista;
    }

    /** Lista las retroalimentaciones hechas por un tutor. */
    public List<Retroalimentacion> listarPorTutor(String numDocTutor) {
        List<Retroalimentacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Retroalimentacion WHERE NumdocTutor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDocTutor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar por tutor: " + e.getMessage());
        }
        return lista;
    }

    /** Lista las retroalimentaciones hechas por un asesor. */
    public List<Retroalimentacion> listarPorAsesor(String numDocAsesor) {
        List<Retroalimentacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM Retroalimentacion WHERE NumdocAsesor = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDocAsesor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar por asesor: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminar(String id) {
        String sql = "DELETE FROM Retroalimentacion WHERE Idretroalimentacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Retroalimentacion: " + e.getMessage());
            return false;
        }
    }

    private Retroalimentacion mapear(ResultSet rs) throws SQLException {
        return new Retroalimentacion(
            rs.getString("Idretroalimentacion"),
            rs.getString("Comentario"),
            rs.getDate("Fecha"),
            rs.getString("Idbitacora"),
            rs.getString("NumdocTutor"),
            rs.getString("NumdocAsesor")
        );
    }
}
