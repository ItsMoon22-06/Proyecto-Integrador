
package controlador;

import modelado.AsesorPedagogico;
import modelado.AsesorPedagogicoDAO;

import javax.swing.*;
import java.util.List;

/**
 * Controlador de Asesor Pedagógico.
 * Provee métodos CRUD que los paneles de vista invocan al interactuar con
 * botones.
 */
public class AsesorControlador {

    private AsesorPedagogicoDAO dao;

    public AsesorControlador() {
        this.dao = new AsesorPedagogicoDAO();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean registrar(String numDoc, String tipoDoc, String nombre, String apellido,
            String correo, String contrasena, String estado) {
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        if (dao.buscarPorDocumento(numDoc) != null) {
            mostrarError("Ya existe un Asesor con ese número de documento.");
            return false;
        }
        AsesorPedagogico a = new AsesorPedagogico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado);
        boolean ok = dao.insertar(a);
        if (ok)
            mostrarInfo("Asesor Pedagógico registrado exitosamente.");
        else
            mostrarError("No se pudo registrar el Asesor.");
        return ok;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    public AsesorPedagogico buscar(String numDoc) {
        AsesorPedagogico a = dao.buscarPorDocumento(numDoc);
        if (a == null)
            mostrarError("No se encontró ningún Asesor con ese documento.");
        return a;
    }

    public List<AsesorPedagogico> listarTodos() {
        return dao.listarTodos();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(String numDoc, String tipoDoc, String nombre, String apellido,
            String correo, String contrasena, String estado) {
        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo no pueden estar vacíos.");
            return false;
        }
        AsesorPedagogico a = new AsesorPedagogico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado);
        boolean ok = dao.actualizar(a);
        if (ok)
            mostrarInfo("Asesor Pedagógico actualizado correctamente.");
        else
            mostrarError("No se pudo actualizar.");
        return ok;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc, JComponent parent) {
        int confirm = JOptionPane.showConfirmDialog(
                parent,
                "¿Eliminar al Asesor con documento " + numDoc + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return false;
        boolean ok = dao.eliminar(numDoc);
        if (ok)
            mostrarInfo("Asesor Pedagógico eliminado.");
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
