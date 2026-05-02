package controlador;

import modelado.Evidencia;
import modelado.Bitacora;
import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import modelado.BitacoraDAO;
import modelado.ConexionBD;
import modelado.EvidenciaDAO;
import modelado.Retroalimentacion;
import modelado.RetroalimentacionDAO;

public class BitacoraControlador {

    private final BitacoraDAO   bitacoraDAO;
    private final RetroalimentacionDAO retroDAO;

    public BitacoraControlador() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        this.bitacoraDAO = new BitacoraDAO(conn);
        this.retroDAO    = new RetroalimentacionDAO(conn);
    }

    // ── Bitacora ──────────────────────────────────────────────────────────────

    public boolean crearBitacora(String numDocEstudiante, String idPractica) {
        if (bitacoraDAO.buscarPorEstudianteYPractica(numDocEstudiante, idPractica) != null) {
            mostrarError("Ya existe una bitácora para esta práctica y estudiante.");
            return false;
        }
        String id = "BIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Date hoy = new Date(System.currentTimeMillis());
        Bitacora b = new Bitacora(id, hoy, 0, null, numDocEstudiante, idPractica);
        boolean ok = bitacoraDAO.insertar(b);
        if (ok) mostrarInfo("Bitácora creada: " + id);
        else    mostrarError("No se pudo crear la bitácora.");
        return ok;
    }

    public Bitacora buscarPorEstudianteYPractica(String numDocumento, String idPractica) {
        return bitacoraDAO.buscarPorEstudianteYPractica(numDocumento, idPractica);
    }

    public List<Bitacora> listarPorEstudiante(String numDocumento) {
        return bitacoraDAO.listarPorEstudiante(numDocumento);
    }

    public boolean cerrarBitacora(String idBitacora, String notaTexto, String observacion) {
        if (notaTexto.trim().isEmpty() || observacion.trim().isEmpty()) {
            mostrarError("Nota y observación son obligatorias."); return false;
        }
        double nota;
        try {
            nota = Double.parseDouble(notaTexto.trim());
            if (nota < 0 || nota > 5) { mostrarError("La nota debe estar entre 0 y 5."); return false; }
        } catch (NumberFormatException ex) {
            mostrarError("La nota debe ser un número (ej: 4.5)."); return false;
        }
        boolean ok = bitacoraDAO.cerrarBitacora(idBitacora, nota, observacion);
        if (ok) mostrarInfo("Bitácora cerrada con nota: " + nota);
        else    mostrarError("No se pudo cerrar la bitácora.");
        return ok;
    }

    public boolean guardarEvidencia(String idBitacora, byte[] bytes, String nombreArchivo, String descripcion) {
        Evidencia e = new Evidencia();
        e.setIdEvidencia("EVI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        e.setIdBitacora(idBitacora);
        e.setArchivo(bytes);
        e.setNombreArchivo(nombreArchivo);
        e.setDescripcion(descripcion);
        EvidenciaDAO dao = new EvidenciaDAO(ConexionBD.getInstancia().getConexion());
        boolean ok = dao.insertar(e);
        if (ok) mostrarInfo("Evidencia subida correctamente.");
        else mostrarError("No se pudo subir la evidencia.");
        return ok;
    }

    public List<Evidencia> listarEvidencias(String idBitacora) {
        EvidenciaDAO dao = new EvidenciaDAO(ConexionBD.getInstancia().getConexion());
        return dao.listarPorBitacora(idBitacora);
    }

    public boolean eliminarBitacora(String idBitacora, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
            "¿Eliminar la bitácora " + idBitacora + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return false;
        boolean ok = bitacoraDAO.eliminar(idBitacora);
        if (ok) mostrarInfo("Bitácora eliminada."); else mostrarError("No se pudo eliminar.");
        return ok;
    }

    // ── Retroalimentacion ─────────────────────────────────────────────────────

    public boolean agregarRetroalimentacionTutor(String idBitacora, String comentario, String numDocTutor) {
        return agregarRetro(idBitacora, comentario, numDocTutor, null);
    }

    public boolean agregarRetroalimentacionAsesor(String idBitacora, String comentario, String numDocAsesor) {
        return agregarRetro(idBitacora, comentario, null, numDocAsesor);
    }

    private boolean agregarRetro(String idBitacora, String comentario, String numDocTutor, String numDocAsesor) {
        if (comentario.trim().isEmpty()) { mostrarError("El comentario no puede estar vacío."); return false; }
        String id = "RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Date hoy  = new Date(System.currentTimeMillis());
        Retroalimentacion r = new Retroalimentacion(id, comentario.trim(), hoy, idBitacora, numDocTutor, numDocAsesor);
        boolean ok = retroDAO.insertar(r);
        if (!ok) mostrarError("No se pudo registrar el comentario.");
        return ok;
    }

    public List<Retroalimentacion> listarRetroalimentaciones(String idBitacora) {
        return retroDAO.listarPorBitacora(idBitacora);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void mostrarInfo(String msg)  { JOptionPane.showMessageDialog(null, msg, "SIGEP", JOptionPane.INFORMATION_MESSAGE); }
    private void mostrarError(String msg) { JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
