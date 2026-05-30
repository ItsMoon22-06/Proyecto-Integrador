package controlador;

import modelado.*;

import javax.swing.*;
import java.sql.Date;
import java.util.List;

/**
 * Controlador del Director del Programa.
 * Gestiona el CRUD de: Estudiantes, Tutores, Asesores y Prácticas.
 */
public class DirectorControlador {

    private final DirectorDAO directorDAO;
    private final EstudianteDAO estudianteDAO;
    private final TutorAcademicoDAO tutorDAO;
    private final AsesorPedagogicoDAO asesorDAO;
    private final PracticaDAO practicaDAO;

    public DirectorControlador() {
        this.directorDAO = new DirectorDAO();
        this.estudianteDAO = new EstudianteDAO();
        this.tutorDAO = new TutorAcademicoDAO();
        this.asesorDAO = new AsesorPedagogicoDAO();
        this.practicaDAO = new PracticaDAO();
    }

    // ════════════════════════════════════════════════════════════════════
    // CRUD ESTUDIANTES
    // ════════════════════════════════════════════════════════════════════

    public boolean registrarEstudiante(String numDoc, String tipoDoc, String nombre,
            String apellido, String correo, String contrasena,
            String estado, String idPrograma) {
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        if (estudianteDAO.buscarPorDocumento(numDoc) != null) {
            mostrarError("Ya existe un Estudiante con ese documento.");
            return false;
        }
        Estudiante e = new Estudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma);
        boolean ok = estudianteDAO.insertar(e);
        if (ok)
            mostrarInfo("Estudiante registrado correctamente.");
        else
            mostrarError("No se pudo registrar el Estudiante.");
        return ok;
    }

    public Estudiante buscarEstudiante(String numDoc) {
        return estudianteDAO.buscarPorDocumento(numDoc);
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteDAO.listarTodos();
    }

    public boolean actualizarEstudiante(String numDoc, String tipoDoc, String nombre,
            String apellido, String correo, String contrasena,
            String estado, String idPrograma) {
        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo son obligatorios.");
            return false;
        }
        Estudiante e = new Estudiante(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado, idPrograma);
        String resultado = estudianteDAO.actualizar(e);

        boolean ok = resultado.equals("OK");
        if (ok)
            mostrarInfo("Estudiante actualizado correctamente.");
        else
            mostrarError(resultado);
        return ok;
    }

    public boolean eliminarEstudiante(String numDoc, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar permanentemente al Estudiante con documento " + numDoc + "?\n" +
                        "Los datos y el acceso a su cuenta se perderán por completo.",
                "¿Eliminar Usuario?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;
        boolean ok = estudianteDAO.eliminar(numDoc);
        if (ok)
            mostrarInfo("Estudiante eliminado.");
        else
            mostrarError("No se pudo eliminar. Puede tener registros asociados.");
        return ok;
    }

    // ════════════════════════════════════════════════════════════════════
    // CRUD TUTORES ACADÉMICOS
    // ════════════════════════════════════════════════════════════════════

    public boolean registrarTutor(String numDoc, String tipoDoc, String nombre,
            String apellido, String correo, String contrasena,
            String estado, String idPrograma) {
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        if (tutorDAO.buscarPorDocumento(numDoc) != null) {
            mostrarError("Ya existe un Tutor con ese documento.");
            return false;
        }
        TutorAcademico t = new TutorAcademico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                idPrograma);
        boolean ok = tutorDAO.insertar(t);
        if (ok)
            mostrarInfo("Tutor Académico registrado correctamente.");
        else
            mostrarError("No se pudo registrar el Tutor.");
        return ok;
    }

    public TutorAcademico buscarTutor(String numDoc) {
        return tutorDAO.buscarPorDocumento(numDoc);
    }

    public List<TutorAcademico> listarTutores() {
        return tutorDAO.listarTodos();
    }

    public boolean actualizarTutor(String numDoc, String tipoDoc, String nombre,
            String apellido, String correo, String contrasena,
            String estado, String idPrograma) {
        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo son obligatorios.");
            return false;
        }
        TutorAcademico t = new TutorAcademico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado,
                idPrograma);
        String resultado = tutorDAO.actualizar(t);
        boolean ok = resultado.equals("OK");
        if (ok)
            mostrarInfo("Tutor Académico actualizado correctamente.");
        else
            mostrarError(resultado);
        return ok;
    }

    public boolean eliminarTutor(String numDoc, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar permanentemente al Tutor con documento " + numDoc + "?",
                "¿Eliminar Usuario?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;
        boolean ok = tutorDAO.eliminar(numDoc);
        if (ok)
            mostrarInfo("Tutor Académico eliminado.");
        else
            mostrarError("No se pudo eliminar. Puede tener prácticas asociadas.");
        return ok;
    }

    // ════════════════════════════════════════════════════════════════════
    // CRUD ASESORES PEDAGÓGICOS
    // ════════════════════════════════════════════════════════════════════

    public boolean registrarAsesor(String numDoc, String tipoDoc, String nombre,
            String apellido, String correo, String contrasena,
            String estado) {
        if (numDoc.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Todos los campos obligatorios deben estar completos.");
            return false;
        }
        if (asesorDAO.buscarPorDocumento(numDoc) != null) {
            mostrarError("Ya existe un Asesor con ese documento.");
            return false;
        }
        AsesorPedagogico a = new AsesorPedagogico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado);
        boolean ok = asesorDAO.insertar(a);
        if (ok)
            mostrarInfo("Asesor Pedagógico registrado correctamente.");
        else
            mostrarError("No se pudo registrar el Asesor.");
        return ok;
    }

    public AsesorPedagogico buscarAsesor(String numDoc) {
        return asesorDAO.buscarPorDocumento(numDoc);
    }

    public List<AsesorPedagogico> listarAsesores() {
        return asesorDAO.listarTodos();
    }

    public boolean actualizarAsesor(String numDoc, String tipoDoc, String nombre,
            String apellido, String correo, String contrasena,
            String estado) {
        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo son obligatorios.");
            return false;
        }
        AsesorPedagogico a = new AsesorPedagogico(numDoc, tipoDoc, nombre, apellido, correo, contrasena, estado);
        boolean ok = asesorDAO.actualizar(a);
        if (ok)
            mostrarInfo("Asesor Pedagógico actualizado correctamente.");
        else
            mostrarError("No se pudo actualizar el Asesor.");
        return ok;
    }

    public boolean eliminarAsesor(String numDoc, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar permanentemente al Asesor con documento " + numDoc + "?",
                "¿Eliminar Usuario?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;
        boolean ok = asesorDAO.eliminar(numDoc);
        if (ok)
            mostrarInfo("Asesor Pedagógico eliminado.");
        else
            mostrarError("No se pudo eliminar. Puede tener prácticas asociadas.");
        return ok;
    }

    // ════════════════════════════════════════════════════════════════════
    // CRUD PRÁCTICAS
    // ════════════════════════════════════════════════════════════════════

    public boolean registrarPractica(String idPractica, Date fechaInicio, Date fechaFinal,
            String entidad, String estado, String idTipopractica,
            String numDocTutor, String numDocEstudiante, String numDocAsesor) {
        if (idPractica.isEmpty() || entidad.isEmpty()) {
            mostrarError("ID y entidad son obligatorios.");
            return false;
        }
        if (practicaDAO.buscarPorId(idPractica) != null) {
            mostrarError("Ya existe una práctica con ese ID.");
            return false;
        }
        Practica p = new Practica(idPractica, fechaInicio, fechaFinal,
                entidad, estado, idTipopractica,
                numDocTutor, numDocEstudiante, numDocAsesor);
        boolean ok = practicaDAO.insertar(p);
        if (ok)
            mostrarInfo("Práctica creada exitosamente.");
        else
            mostrarError("No se pudo crear la práctica.");
        return ok;
    }

    public Practica buscarPractica(String idPractica) {
        return practicaDAO.buscarPorId(idPractica);
    }

    public List<Practica> listarPracticas() {
        return practicaDAO.listarTodas();
    }

    public boolean actualizarPractica(String idPractica, Date fechaInicio, Date fechaFinal,
            String entidad, String estado, String idTipopractica,
            String numDocTutor, String numDocEstudiante, String numDocAsesor) {
        if (entidad.isEmpty()) {
            mostrarError("La entidad no puede estar vacía.");
            return false;
        }
        Practica p = new Practica(idPractica, fechaInicio, fechaFinal,
                entidad, estado, idTipopractica,
                numDocTutor, numDocEstudiante, numDocAsesor);
        boolean ok = practicaDAO.actualizar(p);
        if (ok)
            mostrarInfo("Práctica actualizada correctamente.");
        else
            mostrarError("No se pudo actualizar la práctica.");
        return ok;
    }

    public boolean eliminarPractica(String idPractica, JComponent parent) {
        int c = JOptionPane.showConfirmDialog(parent,
                "<html>¿Eliminar la práctica <b>" + idPractica + "</b>?</html>",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;
        boolean ok = practicaDAO.eliminar(idPractica);
        if (ok)
            mostrarInfo("Práctica eliminada.");
        else
            mostrarError("No se pudo eliminar la práctica.");
        return ok;
    }

    // ════════════════════════════════════════════════════════════════════
    // CRUD TIPOS DE PRÁCTICA
    // ════════════════════════════════════════════════════════════════════

    public boolean registrarTipoPractica(String id, String nombre, int numSemestre, int horas) {
        TipopracticaDAO tpDAO = new TipopracticaDAO();
        if (id.trim().isEmpty() || nombre.trim().isEmpty()) {
            mostrarError("El ID y el nombre son obligatorios.");
            return false;
        }
        if (tpDAO.buscarPorId(id.trim()) != null) {
            mostrarError("Ya existe un Tipo de Práctica con ese ID.");
            return false;
        }
        Tipopractica t = new Tipopractica(id.trim(), nombre.trim(), numSemestre, horas);
        boolean ok = tpDAO.insertar(t);
        if (ok)
            mostrarInfo("Tipo de Práctica registrado.");
        else
            mostrarError("No se pudo registrar el Tipo de Práctica.");
        return ok;
    }

    public List<Tipopractica> listarTiposPractica() {
        TipopracticaDAO tpDAO = new TipopracticaDAO();
        return tpDAO.listarTodos();
    }

    public boolean actualizarTipoPractica(String id, String nombre, int numSemestre, int horas) {
        if (nombre.trim().isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return false;
        }
        if (practicaDAO.estaEnUsoActivo(id)) {
            mostrarError("No se puede modificar porque está en uso en una práctica ACTIVA.");
            return false;
        }
        TipopracticaDAO tpDAO = new TipopracticaDAO();
        Tipopractica t = new Tipopractica(id, nombre.trim(), numSemestre, horas);
        boolean ok = tpDAO.actualizar(t);
        if (ok)
            mostrarInfo("Tipo de Práctica actualizado.");
        else
            mostrarError("No se pudo actualizar el Tipo de Práctica.");
        return ok;
    }

    public boolean eliminarTipoPractica(String id, JComponent parent) {
        if (practicaDAO.estaEnUsoCualquierEstado(id)) {
            mostrarError(
                    "No se puede eliminar porque este tipo de práctica ya ha sido asignado a una o más prácticas.");
            return false;
        }
        int c = JOptionPane.showConfirmDialog(parent,
                "¿Eliminar permanentemente el Tipo de Práctica " + id + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION)
            return false;

        TipopracticaDAO tpDAO = new TipopracticaDAO();
        boolean ok = tpDAO.eliminar(id);
        if (ok)
            mostrarInfo("Tipo de Práctica eliminado.");
        else
            mostrarError("No se pudo eliminar el Tipo de Práctica.");
        return ok;
    }

    public boolean tipoPracticaEnUsoActivo(String id) {
        return practicaDAO.estaEnUsoActivo(id);
    }

    public boolean tipoPracticaEnUsoCualquierEstado(String id) {
        return practicaDAO.estaEnUsoCualquierEstado(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "SPP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public Director buscarDirector(String numDoc) {
        return directorDAO.buscarPorDocumento(numDoc);
    }

    public List<Director> listarDirectores() {
        return directorDAO.listarTodos();
    }

    public List<Programa> listarProgramas() {
        ProgramaDAO dao = new ProgramaDAO();
        return dao.listarTodos();
    }
}
