
package controlador;

import modelado.TutorAcademico;
import modelado.TutorAcademicoDAO;

import javax.swing.*;
import java.util.List;

/**
 * Controlador de Tutor Académico.
 * Provee métodos CRUD que los paneles de vista invocan al interactuar con
 * botones.
 */
public class TutorControlador {

    private TutorAcademicoDAO dao;

    public TutorControlador() {
        this.dao = new TutorAcademicoDAO();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean registrar(String numDoc, String tipoDoc, String nombre, String apellido,
            String correo, String contrasena, String estado, String idPrograma) {
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        if (dao.buscarPorDocumento(numDoc) != null) {
            mostrarError("Ya existe un Tutor con ese número de documento.");
            return false;
        }
        TutorAcademico t = new TutorAcademico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                idPrograma);
        boolean ok = dao.insertar(t);
        if (ok)
            mostrarInfo("Tutor Académico registrado exitosamente.");
        else
            mostrarError("No se pudo registrar el Tutor.");
        return ok;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    public TutorAcademico buscar(String numDoc) {
        TutorAcademico t = dao.buscarPorDocumento(numDoc);
        if (t == null)
            mostrarError("No se encontró ningún Tutor con ese documento.");
        return t;
    }

    public List<TutorAcademico> listarTodos() {
        return dao.listarTodos();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(String numDoc, String tipoDoc, String nombre, String apellido,
            String correo, String contrasena, String estado, String idPrograma) {
        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo no pueden estar vacíos.");
            return false;
        }
        TutorAcademico t = new TutorAcademico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                idPrograma);
        String resultado = dao.actualizar(t);
        boolean ok = resultado.equals("OK");
        if (ok)
            mostrarInfo("Tutor Académico actualizado correctamente.");
        else
            mostrarError(resultado);
        return ok;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc, JComponent parent) {
        int confirm = JOptionPane.showConfirmDialog(
                parent,
                "¿Eliminar al Tutor con documento " + numDoc + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return false;
        boolean ok = dao.eliminar(numDoc);
        if (ok)
            mostrarInfo("Tutor Académico eliminado.");
        else
            mostrarError("No se pudo eliminar. Puede tener registros asociados.");
        return ok;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SPP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
