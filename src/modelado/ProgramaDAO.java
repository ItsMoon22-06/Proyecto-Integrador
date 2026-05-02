
package modelado;

/**
 *
 * @author EdwinPruebas
 */


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla PROGRAMA.
 * Operaciones: insertar, buscar, listar, actualizar, eliminar.
 */
public class ProgramaDAO {

    private Connection conn;

    public ProgramaDAO(Connection conn) {
        this.conn = conn;
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(Programa p) {
        String sql = "INSERT INTO Programa (Idprograma, Nombre, Facultad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getIdPrograma());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getFacultad());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar Programa: " + e.getMessage());
            return false;
        }
    }

    // ── SELECT por ID ─────────────────────────────────────────────────────────
    public Programa buscarPorId(String idPrograma) {
        String sql = "SELECT * FROM Programa WHERE Idprograma = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPrograma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Programa: " + e.getMessage());
        }
        return null;
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    public List<Programa> listarTodos() {
        List<Programa> lista = new ArrayList<>();
        String sql = "SELECT * FROM Programa";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Programas: " + e.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(Programa p) {
        String sql = "UPDATE Programa SET Nombre = ?, Facultad = ? WHERE Idprograma = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getFacultad());
            ps.setString(3, p.getIdPrograma());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Programa: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idPrograma) {
        String sql = "DELETE FROM Programa WHERE Idprograma = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPrograma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Programa: " + e.getMessage());
            return false;
        }
    }

    // ── Mapeo ResultSet → Objeto ───────────────────────────────────────────────
    private Programa mapear(ResultSet rs) throws SQLException {
        return new Programa(
            rs.getString("Idprograma"),
            rs.getString("Nombre"),
            rs.getString("Facultad")
        );
    }
}
