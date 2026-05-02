
package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla ESTUDIANTE.
 */
public class EstudianteDAO {

    private Connection conn;

    public EstudianteDAO(Connection conn) {
        this.conn = conn;
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insertar(Estudiante e) {
        String sql = "INSERT INTO Estudiante (Numdocumento, Tipodocumento, Nombre, Apellido, " +
                     "CorreoInst, Contrasena, Estado, Idprograma, Idpractica) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNumDocumento());
            ps.setString(2, e.getTipoDocumento());
            ps.setString(3, e.getNombre());
            ps.setString(4, e.getApellido());
            ps.setString(5, e.getCorreoInst());
            ps.setString(6, e.getContrasena());
            ps.setString(7, e.getEstado());
            ps.setString(8, e.getIdPrograma());
            ps.setString(9, e.getIdPractica());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al insertar Estudiante: " + ex.getMessage());
            return false;
        }
    }

    // ── SELECT por documento ──────────────────────────────────────────────────
    public Estudiante buscarPorDocumento(String numDoc) {
        String sql = "SELECT * FROM Estudiante WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDoc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException ex) {
            System.err.println("Error al buscar Estudiante: " + ex.getMessage());
        }
        return null;
    }

    // ── LOGIN (correo + contraseña) ───────────────────────────────────────────
    public Estudiante login(String correo, String contrasena) {
        String sql = "SELECT * FROM Estudiante WHERE CorreoInst = ? AND Contrasena = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException ex) {
            System.err.println("Error en login Estudiante: " + ex.getMessage());
        }
        return null;
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    public List<Estudiante> listarTodos() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM Estudiante";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            System.err.println("Error al listar Estudiantes: " + ex.getMessage());
        }
        return lista;
    }

    // ── SELECT por Programa ───────────────────────────────────────────────────
    public List<Estudiante> listarPorPrograma(String idPrograma) {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM Estudiante WHERE Idprograma = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPrograma);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            System.err.println("Error al listar por programa: " + ex.getMessage());
        }
        return lista;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(Estudiante e) {
        String sql = "UPDATE Estudiante SET Tipodocumento=?, Nombre=?, Apellido=?, " +
                     "CorreoInst=?, Contrasena=?, Estado=?, Idprograma=?, Idpractica=? WHERE Numdocumento=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getTipoDocumento());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getApellido());
            ps.setString(4, e.getCorreoInst());
            ps.setString(5, e.getContrasena());
            ps.setString(6, e.getEstado());
            ps.setString(7, e.getIdPrograma());
            ps.setString(8, e.getIdPractica());
            ps.setString(9, e.getNumDocumento());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al actualizar Estudiante: " + ex.getMessage());
            return false;
        }
    }

    // ── CAMBIAR ESTADO ────────────────────────────────────────────────────────
    public boolean cambiarEstado(String numDoc, String nuevoEstado) {
        String sql = "UPDATE Estudiante SET Estado = ? WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, numDoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al cambiar estado: " + ex.getMessage());
            return false;
        }
    }

    // ── SELECT por Practica ───────────────────────────────────────────────────
    public List<Estudiante> listarPorPractica(String idPractica) {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM Estudiante WHERE Idpractica = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPractica);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException ex) {
            System.err.println("Error al listar Estudiantes por practica: " + ex.getMessage());
        }
        return lista;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc) {
        String sql = "DELETE FROM Estudiante WHERE Numdocumento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar Estudiante: " + ex.getMessage());
            return false;
        }
    }

    // ── Mapeo ─────────────────────────────────────────────────────────────────
    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
            rs.getString("Numdocumento"),
            rs.getString("Tipodocumento"),
            rs.getString("Nombre"),
            rs.getString("Apellido"),
            rs.getString("CorreoInst"),
            rs.getString("Contrasena"),
            rs.getString("Estado"),
            rs.getString("Idprograma"),
            rs.getString("Idpractica")
        );
    }
}
