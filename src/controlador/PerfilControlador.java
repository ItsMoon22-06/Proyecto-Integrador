
package controlador;

import modelado.AsesorPedagogico;
import modelado.Estudiante;
import modelado.TutorAcademico;
import modelado.ConexionBD;
import modelado.AsesorPedagogicoDAO;
import modelado.EstudianteDAO;
import modelado.TutorAcademicoDAO;

import javax.swing.*;
import java.sql.Connection;

/**
 * Controlador de Perfil.
 * Dado que PanelPerfil recibe todos los datos en su constructor,
 * este controlador se usa únicamente para actualizar la contraseña.
 * La carga inicial de perfil se hace al instanciar el PanelPerfil
 * directamente desde los portales (StudentDashboard, PortalTutorAcademico, etc.)
 */
public class PerfilControlador {

    private EstudianteDAO       estudianteDAO;
    private TutorAcademicoDAO   tutorDAO;
    private AsesorPedagogicoDAO asesorDAO;

    public PerfilControlador() {
        Connection conn = ConexionBD.getInstancia().getConexion();
        this.estudianteDAO = new EstudianteDAO(conn);
        this.tutorDAO      = new TutorAcademicoDAO(conn);
        this.asesorDAO     = new AsesorPedagogicoDAO(conn);
    }

    // ── Obtener datos de perfil (para uso externo si se necesita refrescar) ──

    public Estudiante obtenerEstudiante(String numDoc) {
        return estudianteDAO.buscarPorDocumento(numDoc);
    }

    public TutorAcademico obtenerTutor(String numDoc) {
        return tutorDAO.buscarPorDocumento(numDoc);
    }

    public AsesorPedagogico obtenerAsesor(String numDoc) {
        return asesorDAO.buscarPorDocumento(numDoc);
    }

    // ── Actualizar contraseña — Estudiante ────────────────────────────────────
    public boolean actualizarContrasenaEstudiante(String numDoc, String nuevaContrasena) {
        if (!validarContrasena(nuevaContrasena)) return false;
        Estudiante e = estudianteDAO.buscarPorDocumento(numDoc);
        if (e == null) { mostrarError("Estudiante no encontrado."); return false; }
        e.setContrasena(nuevaContrasena);
        boolean ok = estudianteDAO.actualizar(e);
        if (ok) mostrarInfo("Contraseña actualizada correctamente.");
        else    mostrarError("No se pudo actualizar la contraseña.");
        return ok;
    }

    // ── Actualizar contraseña — Tutor Académico ───────────────────────────────
    public boolean actualizarContrasenaTutor(String numDoc, String nuevaContrasena) {
        if (!validarContrasena(nuevaContrasena)) return false;
        TutorAcademico t = tutorDAO.buscarPorDocumento(numDoc);
        if (t == null) { mostrarError("Tutor no encontrado."); return false; }
        t.setContrasena(nuevaContrasena);
        boolean ok = tutorDAO.actualizar(t);
        if (ok) mostrarInfo("Contraseña actualizada correctamente.");
        else    mostrarError("No se pudo actualizar la contraseña.");
        return ok;
    }

    // ── Actualizar contraseña — Asesor Pedagógico ─────────────────────────────
    public boolean actualizarContrasenaAsesor(String numDoc, String nuevaContrasena) {
        if (!validarContrasena(nuevaContrasena)) return false;
        AsesorPedagogico a = asesorDAO.buscarPorDocumento(numDoc);
        if (a == null) { mostrarError("Asesor no encontrado."); return false; }
        a.setContrasena(nuevaContrasena);
        boolean ok = asesorDAO.actualizar(a);
        if (ok) mostrarInfo("Contraseña actualizada correctamente.");
        else    mostrarError("No se pudo actualizar la contraseña.");
        return ok;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private boolean validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        return true;
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SIGEP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
