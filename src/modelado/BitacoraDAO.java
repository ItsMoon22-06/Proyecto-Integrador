package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {

    private Connection conn;

    public BitacoraDAO(Connection conn) { this.conn = conn; }

    public boolean insertar(Bitacora b) {
        String sql = "INSERT INTO Bitacora (IdBitacora, Fechacreacion, Notafinal, " +
                     "Observacionfinal, Numdocumento, Idpractica) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getIdBitacora());
            ps.setDate(2, b.getFechaCreacion());
            if (b.getNotaFinal() == 0) ps.setNull(3, Types.NUMERIC);
            else ps.setDouble(3, b.getNotaFinal());
            ps.setString(4, b.getObservacionFinal());
            ps.setString(5, b.getNumDocumento());
            ps.setString(6, b.getIdPractica());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Bitacora: " + e.getMessage());
            return false;
        }
    }

    public Bitacora buscarPorId(String idBitacora) {
        String sql = "SELECT * FROM Bitacora WHERE IdBitacora = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idBitacora);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    public Bitacora buscarPorEstudianteYPractica(String numDocumento, String idPractica) {
        String sql = "SELECT * FROM Bitacora WHERE Numdocumento = ? AND Idpractica = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            ps.setString(2, idPractica);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    public List<Bitacora> listarPorEstudiante(String numDocumento) {
        List<Bitacora> lista = new ArrayList<>();
        String sql = "SELECT * FROM Bitacora WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return lista;
    }

    public boolean cerrarBitacora(String idBitacora, double nota, String observacion) {
        String sql = "UPDATE Bitacora SET Notafinal = ?, Observacionfinal = ? WHERE IdBitacora = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nota);
            ps.setString(2, observacion);
            ps.setString(3, idBitacora);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); return false; }
    }

    public boolean eliminar(String idBitacora) {
        String sql = "DELETE FROM Bitacora WHERE IdBitacora = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idBitacora);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); return false; }
    }

    private Bitacora mapear(ResultSet rs) throws SQLException {
        return new Bitacora(
            rs.getString("IdBitacora"), rs.getDate("Fechacreacion"),
            rs.getDouble("Notafinal"),  rs.getString("Observacionfinal"),
            rs.getString("Numdocumento"), rs.getString("Idpractica")
        );
    }
}
