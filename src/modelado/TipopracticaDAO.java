package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla TIPOPRACTICA.
 */
public class TipopracticaDAO {

    private Connection conn;

    public TipopracticaDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertar(Tipopractica t) {
        String sql = "INSERT INTO Tipopractica (IdTipopractica, Nombre, Numsemestre, Horasrequeridas) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getIdTipopractica());
            ps.setString(2, t.getNombre());
            ps.setInt(3, t.getNumSemestre());
            ps.setInt(4, t.getHorasRequeridas());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Tipopractica: " + e.getMessage());
            return false;
        }
    }

    public Tipopractica buscarPorId(String id) {
        String sql = "SELECT * FROM Tipopractica WHERE IdTipopractica = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar Tipopractica: " + e.getMessage());
        }
        return null;
    }

    public List<Tipopractica> listarTodos() {
        List<Tipopractica> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tipopractica";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar Tipopracticas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Tipopractica t) {
        String sql = "UPDATE Tipopractica SET Nombre=?, Numsemestre=?, Horasrequeridas=? WHERE IdTipopractica=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setInt(2, t.getNumSemestre());
            ps.setInt(3, t.getHorasRequeridas());
            ps.setString(4, t.getIdTipopractica());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Tipopractica: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String id) {
        String sql = "DELETE FROM Tipopractica WHERE IdTipopractica = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Tipopractica: " + e.getMessage());
            return false;
        }
    }

    private Tipopractica mapear(ResultSet rs) throws SQLException {
        return new Tipopractica(
            rs.getString("IdTipopractica"),
            rs.getString("Nombre"),
            rs.getInt("Numsemestre"),
            rs.getInt("Horasrequeridas")
        );
    }
}
