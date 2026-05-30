package controlador;

import modelado.Practica;
import modelado.PracticaDAO;

import javax.swing.*;
import java.sql.Date;
import java.util.List;

/**
 * Controlador de Práctica — alineado con el esquema real de BD.
 *
 * Columnas PRACTICA: IDPRACTICA, FECHAINICIO, FECHAFINAL, ENTIDAD, ESTADO,
 * IDTIPOPRACTICA, NUMDOCTUTOR, NUMDOCESTUDIANTE, NUMDOCASESOR
 */
public class PracticaControlador {

    private final PracticaDAO dao;

    public PracticaControlador() {
        this.dao = new PracticaDAO();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean registrar(String idPractica, Date fechaInicio, Date fechaFinal,
            String entidad, String estado, String idTipopractica,
            String numDocTutor, String numDocEstudiante, String numDocAsesor) {
        if (idPractica.isEmpty() || entidad.isEmpty()) {
            mostrarError("ID y entidad son obligatorios.");
            return false;
        }
        if (dao.buscarPorId(idPractica) != null) {
            mostrarError("Ya existe una práctica con ese ID.");
            return false;
        }
        Practica p = new Practica(idPractica, fechaInicio, fechaFinal,
                entidad, estado, idTipopractica,
                numDocTutor, numDocEstudiante, numDocAsesor);
        boolean ok = dao.insertar(p);
        if (ok)
            mostrarInfo("Práctica registrada exitosamente.");
        else
            mostrarError("No se pudo registrar la práctica.");
        return ok;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    public Practica buscar(String idPractica) {
        Practica p = dao.buscarPorId(idPractica);
        if (p == null)
            mostrarError("No se encontró una práctica con ese ID.");
        return p;
    }

    /** Prácticas de un estudiante (por NUMDOCESTUDIANTE). */
    public List<Practica> listarPorEstudiante(String numDocEstudiante) {
        return dao.listarPorEstudiante(numDocEstudiante);
    }

    public List<Practica> listarPorTutor(String numDocTutor) {
        return dao.listarPorTutor(numDocTutor);
    }

    public List<Practica> listarPorAsesor(String numDocAsesor) {
        return dao.listarPorAsesor(numDocAsesor);
    }

    public List<Practica> listarTodas() {
        return dao.listarTodas();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(String idPractica, Date fechaInicio, Date fechaFinal,
            String entidad, String estado, String idTipopractica,
            String numDocTutor, String numDocEstudiante, String numDocAsesor) {
        if (entidad.isEmpty()) {
            mostrarError("La entidad no puede estar vacía.");
            return false;
        }
        Practica p = new Practica(idPractica, fechaInicio, fechaFinal,
                entidad, estado, idTipopractica,
                numDocTutor, numDocEstudiante, numDocAsesor);
        boolean ok = dao.actualizar(p);
        if (ok)
            mostrarInfo("Práctica actualizada correctamente.");
        else
            mostrarError("No se pudo actualizar la práctica.");
        return ok;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idPractica, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar la práctica " + idPractica + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;
        boolean ok = dao.eliminar(idPractica);
        if (ok)
            mostrarInfo("Práctica eliminada.");
        else
            mostrarError("No se pudo eliminar la práctica.");
        return ok;
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SPP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
