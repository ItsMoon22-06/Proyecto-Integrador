package modelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

/**
 * DAO para la tabla EVIDENCIABITACORA utilizando Procedimientos y Funciones de
 * Oracle.
 * Capaz de procesar archivos adjuntos (BLOB) de manera segura y eficiente.
 */
public class EvidenciaDAO {

    // Constructor recomendado para arquitectura desacoplada
    public EvidenciaDAO() {
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insertar(Evidencia e) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_incEvidencia(?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, e.getIdEvidencia());
            cs.setString(2, e.getIdBitacora());
            cs.setString(3, e.getNombreArchivo());
            cs.setString(4, e.getDescripcion());
            cs.setBytes(5, e.getArchivo() != null ? e.getArchivo() : new byte[0]);

            cs.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al insertar evidencia por procedimiento: " + ex.getMessage());
            return false;
        }
    }

    // ── UPDATE (modificar archivo, nombre y descripción) ─────────────────────
    public boolean modificarEvidencia(Evidencia e) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_modEvidencia(?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, e.getIdBitacora());
            cs.setString(2, e.getNombreArchivo());
            cs.setString(3, e.getDescripcion());
            cs.setBytes(4, e.getArchivo() != null ? e.getArchivo() : new byte[0]);
            cs.setString(5, e.getIdEvidencia());

            cs.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al modificar evidencia por procedimiento: " + ex.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idEvidencia) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{call PROYECTOSPP.pro_eliEvidencia(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, idEvidencia);
            cs.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar evidencia por procedimiento: " + ex.getMessage());
            return false;
        }
    }

    // ── SELECT por bitácora (Usa Función con Cursor) ──────────────────────────
    public List<Evidencia> listarPorBitacora(String idBitacora) {
        List<Evidencia> lista = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return lista;

        String sql = "{? = call PROYECTOSPP.fun_listarEvidenciasBit(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idBitacora);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Evidencia e = new Evidencia();
                    e.setIdEvidencia(rs.getString("IDEVIDENCIA"));
                    e.setIdBitacora(rs.getString("IDBITACORA"));
                    e.setNombreArchivo(rs.getString("NombreArchivo"));
                    e.setDescripcion(rs.getString("Descripcion"));
                    e.setArchivo(rs.getBytes("EVIDENCIANOMBRE"));
                    lista.add(e);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al listar evidencias por función: " + ex.getMessage());
        }
        return lista;
    }

    // ── Descargar BLOB a disco (Usa Función con Cursor) ───────────────────────
    public boolean descargarEvidenciaBlob(String idEvidencia, java.io.File directorioDestino) {
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null)
            return false;

        String sql = "{? = call PROYECTOSPP.fun_buscarEvidenciaId(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, idEvidencia);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) {
                    String nombre = rs.getString("NombreArchivo");
                    if (nombre == null || nombre.trim().isEmpty()) {
                        nombre = "evidencia_descargada.bin";
                    }

                    java.io.File destino = new java.io.File(directorioDestino, nombre);

                    // Extrae el flujo del BLOB devuelto en el cursor y lo escribe en el disco
                    try (java.io.InputStream in = rs.getBinaryStream("EVIDENCIANOMBRE");
                            java.io.FileOutputStream out = new java.io.FileOutputStream(destino)) {

                        if (in == null)
                            return false;

                        byte[] buf = new byte[8192];
                        int n;
                        while ((n = in.read(buf)) != -1) {
                            out.write(buf, 0, n);
                        }
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al descargar BLOB desde función: " + ex.getMessage());
        }
        return false;
    }
}