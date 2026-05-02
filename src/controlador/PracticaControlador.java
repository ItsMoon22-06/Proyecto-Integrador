package controlador;

import modelado.Practica;
import modelado.ConexionBD;
import modelado.PracticaDAO;

import javax.swing.*;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * Controlador de Práctica.
 * Provee métodos CRUD + cambio de estado que los paneles de vista invocan.
 */
public class PracticaControlador {

    private PracticaDAO dao;

    public PracticaControlador() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        this.dao = new PracticaDAO(conn);
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean registrar(String idPractica, Date fecha, String entidad, String estado,
                             String idTipopractica, String idPrograma, String numDocTutor) {
        if (idPractica.isEmpty() || entidad.isEmpty()) {
            mostrarError("ID y entidad son obligatorios.");
            return false;
        }
        if (dao.buscarPorId(idPractica) != null) {
            mostrarError("Ya existe una práctica con ese ID.");
            return false;
        }
        Practica p = new Practica(idPractica, fecha, entidad, estado, idTipopractica, idPrograma, numDocTutor);
        boolean ok = dao.insertar(p);
        if (ok) mostrarInfo("Práctica registrada exitosamente.");
        else    mostrarError("No se pudo registrar la práctica.");
        return ok;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    public Practica buscar(String idPractica) {
        Practica p = dao.buscarPorId(idPractica);
        if (p == null) mostrarError("No se encontró una práctica con ese ID.");
        return p;
    }

    public List<Practica> listarPorEstudiante(String idPractica) {
        return dao.listarPorEstudiante(idPractica);
    }

    public List<Practica> listarPorPrograma(String idPrograma) {
        return dao.listarPorPrograma(idPrograma);
    }

    public List<Practica> listarPorTutor(String numDocTutor) {
        return dao.listarPorTutor(numDocTutor);
    }

    public List<Practica> listarTodas() {
        return dao.listarTodas();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(String idPractica, Date fecha, String entidad, String estado,
                              String idTipopractica, String idPrograma, String numDocTutor) {
        if (entidad.isEmpty()) {
            mostrarError("La entidad no puede estar vacía.");
            return false;
        }
        Practica p = new Practica(idPractica, fecha, entidad, estado, idTipopractica, idPrograma, numDocTutor);
        boolean ok = dao.actualizar(p);
        if (ok) mostrarInfo("Práctica actualizada correctamente.");
        else    mostrarError("No se pudo actualizar la práctica.");
        return ok;
    }

    /** Cambia el estado de la práctica (ej: Pendiente → En curso → Finalizada). */
    public boolean cambiarEstado(String idPractica, String nuevoEstado) {
        boolean ok = dao.cambiarEstado(idPractica, nuevoEstado);
        if (ok) mostrarInfo("Estado de práctica actualizado a: " + nuevoEstado);
        else    mostrarError("No se pudo cambiar el estado.");
        return ok;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String idPractica, JComponent parent) {
        int confirm = JOptionPane.showConfirmDialog(
            parent,
            "¿Eliminar la práctica " + idPractica + "?\nSe eliminarán también su bitácora y retroalimentaciones.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return false;
        boolean ok = dao.eliminar(idPractica);
        if (ok) mostrarInfo("Práctica eliminada.");
        else    mostrarError("No se pudo eliminar la práctica.");
        return ok;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SIGEP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
