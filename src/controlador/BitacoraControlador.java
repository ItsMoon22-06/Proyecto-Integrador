package controlador;

import modelado.Evidencia;
import modelado.Bitacora;
import modelado.BitacoraDAO;
import modelado.EvidenciaDAO;
import modelado.RetroalimentacionTutorDAO;
import modelado.RetroalimentacionAsesorDAO;

import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.UUID;

/**
 * Controlador de Bitácora — alineado con el esquema real de BD.
 *
 * BITACORA: IDBITACORA, FECHACREACION, NOTAFINAL, OBSERVACIONFINAL, IDPRACTICA
 * (sin NUMDOCUMENTO — el estudiante se obtiene por PRACTICA.NUMDOCESTUDIANTE)
 */
public class BitacoraControlador {

    private final BitacoraDAO bitacoraDAO;
    private final RetroalimentacionTutorDAO retroTutorDAO;
    private final RetroalimentacionAsesorDAO retroAsesorDAO;

    public BitacoraControlador() {
        this.bitacoraDAO = new BitacoraDAO();
        this.retroTutorDAO = new RetroalimentacionTutorDAO();
        this.retroAsesorDAO = new RetroalimentacionAsesorDAO();
    }

    // ── Bitacora ──────────────────────────────────────────────────────────────

    /**
     * Crea una nueva bitácora para la práctica dada.
     * numDocEstudiante se usa solo para validar (no se almacena en BITACORA).
     */
    public boolean crearBitacora(String numDocEstudiante, String idPractica) {
        if (bitacoraDAO.buscarPorPractica(idPractica) != null) {
            mostrarError("Ya existe una bitácora para esta práctica.");
            return false;
        }
        String id = "BIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Date hoy = new Date(System.currentTimeMillis());
        Bitacora b = new Bitacora(id, hoy, 0, null, idPractica, "Activa");
        boolean ok = bitacoraDAO.insertar(b);
        if (!ok) {
            mostrarError("No se pudo crear la bitácora.");
        }
        return ok;
    }

    public Bitacora buscarPorEstudianteYPractica(String numDocEstudiante, String idPractica) {
        return bitacoraDAO.buscarPorEstudianteYPractica(numDocEstudiante, idPractica);
    }

    public Bitacora buscarPorPractica(String idPractica) {
        return bitacoraDAO.buscarPorPractica(idPractica);
    }

    public List<Bitacora> listarPorEstudiante(String numDocEstudiante) {
        return bitacoraDAO.listarPorEstudiante(numDocEstudiante);
    }

    public boolean cerrarBitacora(String idBitacora, String notaTexto, String observacion) {
        if (notaTexto.trim().isEmpty() || observacion.trim().isEmpty()) {
            mostrarError("Nota y observación son obligatorias.");
            return false;
        }
        double nota;
        try {
            nota = Double.parseDouble(notaTexto.trim());
            if (nota < 0 || nota > 5) {
                mostrarError("La nota debe estar entre 0 y 5.");
                return false;
            }
        } catch (NumberFormatException ex) {
            mostrarError("La nota debe ser un número (ej: 4.5).");
            return false;
        }
        boolean ok = bitacoraDAO.cerrarBitacora(idBitacora, nota, observacion);
        if (ok)
            mostrarInfo("Bitácora cerrada con nota: " + nota);
        else
            mostrarError("No se pudo cerrar la bitácora.");
        return ok;
    }

    public boolean guardarEvidencia(String idBitacora, byte[] bytes, String nombreArchivo, String descripcion) {
        Evidencia e = new Evidencia();
        e.setIdEvidencia("EVI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        e.setIdBitacora(idBitacora);
        e.setArchivo(bytes);
        e.setNombreArchivo(nombreArchivo);
        e.setDescripcion(descripcion);
        EvidenciaDAO dao = new EvidenciaDAO();
        boolean ok = dao.insertar(e);
        if (ok)
            mostrarInfo("Evidencia subida correctamente.");
        else
            mostrarError("No se pudo subir la evidencia.");
        return ok;
    }

    public boolean modificarEvidencia(String idEvidencia, String idBitacora, byte[] bytes, String nombreArchivo, String descripcion) {
        Evidencia e = new Evidencia();
        e.setIdEvidencia(idEvidencia);
        e.setIdBitacora(idBitacora);
        e.setArchivo(bytes);
        e.setNombreArchivo(nombreArchivo);
        e.setDescripcion(descripcion);
        EvidenciaDAO dao = new EvidenciaDAO();
        boolean ok = dao.modificarEvidencia(e);
        if (ok)
            mostrarInfo("Evidencia modificada correctamente.");
        else
            mostrarError("No se pudo modificar la evidencia.");
        return ok;
    }

    public List<Evidencia> listarEvidencias(String idBitacora) {
        EvidenciaDAO dao = new EvidenciaDAO();
        return dao.listarPorBitacora(idBitacora);
    }

    public boolean descargarEvidenciaBlob(String idEvidencia, java.io.File directorioDestino) {
        EvidenciaDAO dao = new EvidenciaDAO();
        return dao.descargarEvidenciaBlob(idEvidencia, directorioDestino);
    }

    public boolean eliminarEvidenciaSilencioso(String idEvidencia) {
        EvidenciaDAO dao = new EvidenciaDAO();
        return dao.eliminar(idEvidencia);
    }

    public boolean eliminarEvidencia(String idEvidencia, JComponent parent) {
        EvidenciaDAO dao = new EvidenciaDAO();
        boolean ok = dao.eliminar(idEvidencia);
        if (ok)
            mostrarInfo("Evidencia eliminada.");
        else
            mostrarError("No se pudo eliminar la evidencia.");
        return ok;
    }

    public boolean eliminarBitacora(String idBitacora, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar la bitácora " + idBitacora + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;
        boolean ok = bitacoraDAO.eliminar(idBitacora);
        if (ok)
            mostrarInfo("Bitácora eliminada.");
        else
            mostrarError("No se pudo eliminar.");
        return ok;
    }

    // ── Retroalimentación ─────────────────────────────────────────────────────

    public boolean agregarRetroalimentacionTutor(String idBitacora, String comentario, String numDocTutor) {
        if (comentario.trim().isEmpty()) {
            mostrarError("El comentario no puede estar vacío.");
            return false;
        }
        String id = "RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Date hoy = new Date(System.currentTimeMillis());
        modelado.RetroalimentacionTutor r = new modelado.RetroalimentacionTutor(id, comentario.trim(), hoy, idBitacora, numDocTutor);
        boolean ok = retroTutorDAO.insertar(r);
        if (!ok)
            mostrarError("No se pudo registrar el comentario.");
        return ok;
    }

    public boolean agregarRetroalimentacionAsesor(String idBitacora, String comentario, String numDocAsesor) {
        if (comentario.trim().isEmpty()) {
            mostrarError("El comentario no puede estar vacío.");
            return false;
        }
        String id = "RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Date hoy = new Date(System.currentTimeMillis());
        modelado.RetroalimentacionAsesor r = new modelado.RetroalimentacionAsesor(id, comentario.trim(), hoy, idBitacora, numDocAsesor);
        boolean ok = retroAsesorDAO.insertar(r);
        if (!ok)
            mostrarError("No se pudo registrar el comentario.");
        return ok;
    }

    public List<modelado.RetroalimentacionTutor> listarRetroalimentacionesTutor(String idBitacora) {
        return retroTutorDAO.listarPorBitacora(idBitacora);
    }

    public List<modelado.RetroalimentacionAsesor> listarRetroalimentacionesAsesor(String idBitacora) {
        return retroAsesorDAO.listarPorBitacora(idBitacora);
    }

    public boolean actualizarRetroalimentacionTutor(String idRetro, String nuevoComentario) {
        if (nuevoComentario.trim().isEmpty())
            return false;
        boolean ok = retroTutorDAO.actualizarComentario(idRetro, nuevoComentario.trim());
        if (ok)
            mostrarInfo("Comentario actualizado.");
        else
            mostrarError("No se pudo actualizar el comentario.");
        return ok;
    }

    public boolean actualizarRetroalimentacionAsesor(String idRetro, String nuevoComentario) {
        if (nuevoComentario.trim().isEmpty())
            return false;
        boolean ok = retroAsesorDAO.actualizarComentario(idRetro, nuevoComentario.trim());
        if (ok)
            mostrarInfo("Comentario actualizado.");
        else
            mostrarError("No se pudo actualizar el comentario.");
        return ok;
    }

    public boolean eliminarRetroalimentacionTutor(String idRetro, JComponent parent) {
        int res = JOptionPane.showConfirmDialog(parent, "¿Eliminar este comentario?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            boolean ok = retroTutorDAO.eliminar(idRetro);
            if (ok)
                mostrarInfo("Comentario eliminado.");
            else
                mostrarError("No se pudo eliminar el comentario.");
            return ok;
        }
        return false;
    }

    public boolean eliminarRetroalimentacionAsesor(String idRetro, JComponent parent) {
        int res = JOptionPane.showConfirmDialog(parent, "¿Eliminar este comentario?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            boolean ok = retroAsesorDAO.eliminar(idRetro);
            if (ok)
                mostrarInfo("Comentario eliminado.");
            else
                mostrarError("No se pudo eliminar el comentario.");
            return ok;
        }
        return false;
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SPP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
