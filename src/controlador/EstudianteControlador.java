package controlador;

import modelado.Estudiante;
import modelado.EstudianteDAO;

import javax.swing.*;
import java.util.List;

/**
 * Controlador de Estudiante — alineado con el esquema real (sin IDPRACTICA).
 *
 * Columnas ESTUDIANTE: NUMDOCUMENTO, TIPODOCUMENTO, NOMBRE, APELLIDO,
 * CORREOINST, CONTRASENA, ESTADO, IDPROGRAMA
 */
public class EstudianteControlador {

    private final EstudianteDAO dao;

    public EstudianteControlador() {
        this.dao = new EstudianteDAO();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean registrar(String numDoc, String tipoDoc, String nombre, String apellido,
            String correo, String contrasena, String estado, String idPrograma) {
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        if (dao.buscarPorDocumento(numDoc) != null) {
            mostrarError("Ya existe un Estudiante con ese número de documento.");
            return false;
        }
        Estudiante e = new Estudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma);
        boolean ok = dao.insertar(e);
        if (ok)
            mostrarInfo("Estudiante registrado exitosamente.");
        else
            mostrarError("No se pudo registrar el Estudiante.");
        return ok;
    }

    // ── READ ──────────────────────────────────────────────────────────────────
    public Estudiante buscar(String numDoc) {
        Estudiante e = dao.buscarPorDocumento(numDoc);
        if (e == null)
            mostrarError("No se encontró ningún Estudiante con ese documento.");
        return e;
    }

    public List<Estudiante> listarTodos() {
        return dao.listarTodos();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean actualizar(String numDoc, String tipoDoc, String nombre, String apellido,
            String correo, String contrasena, String estado, String idPrograma) {
        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo no pueden estar vacíos.");
            return false;
        }
        Estudiante e = new Estudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma);

        String resultado = dao.actualizar(e);

        boolean ok = resultado.equals("OK");
        if (ok)
            mostrarInfo("Estudiante actualizado correctamente.");
        else
            mostrarError(resultado);
        return ok;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean eliminar(String numDoc, JComponent parent) {
        int confirm = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar al Estudiante con documento " + numDoc + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return false;
        boolean ok = dao.eliminar(numDoc);
        if (ok)
            mostrarInfo("Estudiante eliminado.");
        else
            mostrarError("No se pudo eliminar. Puede tener registros asociados.");
        return ok;
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SPP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
