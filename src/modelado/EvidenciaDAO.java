package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvidenciaDAO {
    private Connection conn;

    public EvidenciaDAO(Connection conn) { this.conn = conn; }

    public boolean insertar(Evidencia e) {
        String sql = "INSERT INTO evidenciabitacora (IDEVIDENCIA, EVIDENCIANOMBRE, IDBITACORA, NombreArchivo, Descripcion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getIdEvidencia());
            if (e.getArchivo() != null) ps.setBytes(2, e.getArchivo());
            else ps.setBytes(2, new byte[0]);
            ps.setString(3, e.getIdBitacora());
            ps.setString(4, e.getNombreArchivo());
            ps.setString(5, e.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error al insertar evidencia: " + ex.getMessage());
            return false;
        }
    }

    public List<Evidencia> listarPorBitacora(String idBitacora) {
        List<Evidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM evidenciabitacora WHERE IDBITACORA = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idBitacora);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Evidencia e = new Evidencia();
                e.setIdEvidencia(rs.getString("IDEVIDENCIA"));
                e.setArchivo(rs.getBytes("EVIDENCIANOMBRE"));
                e.setIdBitacora(rs.getString("IDBITACORA"));
                try { e.setNombreArchivo(rs.getString("NombreArchivo")); } catch (Exception ignore) {}
                try { e.setDescripcion(rs.getString("Descripcion")); } catch (Exception ignore) {}
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar evidencias: " + ex.getMessage());
        }
        return lista;
    }
}
