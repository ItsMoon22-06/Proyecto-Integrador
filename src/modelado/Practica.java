package modelado;

import java.sql.Date;

/**
 * Mapea la tabla PRACTICA de la BD (PROYECTOSPP.DMP).
 *
 * Columnas reales:
 * IDPRACTICA, FECHAINICIO, FECHAFINAL, ENTIDAD, ESTADO,
 * IDTIPOPRACTICA, NUMDOCTUTOR, NUMDOCESTUDIANTE, NUMDOCASESOR
 */
public class Practica {

    private String idPractica;
    private Date fechaInicio;
    private Date fechaFinal;
    private String entidad;
    private String estado;
    private String idTipopractica;
    private String numDocTutor;
    private String numDocEstudiante;
    private String numDocAsesor;

    public Practica() {
    }

    public Practica(String idPractica, Date fechaInicio, Date fechaFinal,
            String entidad, String estado, String idTipopractica,
            String numDocTutor, String numDocEstudiante, String numDocAsesor) {
        this.idPractica = idPractica;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.entidad = entidad;
        this.estado = estado;
        this.idTipopractica = idTipopractica;
        this.numDocTutor = numDocTutor;
        this.numDocEstudiante = numDocEstudiante;
        this.numDocAsesor = numDocAsesor;
    }

    public String getIdPractica() {
        return idPractica;
    }

    public void setIdPractica(String v) {
        this.idPractica = v;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date v) {
        this.fechaInicio = v;
    }

    /** Alias retrocompatible — apunta a fechaInicio. */
    public Date getFecha() {
        return fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date v) {
        this.fechaFinal = v;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String v) {
        this.entidad = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        this.estado = v;
    }

    public String getIdTipopractica() {
        return idTipopractica;
    }

    public void setIdTipopractica(String v) {
        this.idTipopractica = v;
    }

    public String getNumDocTutor() {
        return numDocTutor;
    }

    public void setNumDocTutor(String v) {
        this.numDocTutor = v;
    }

    public String getNumDocEstudiante() {
        return numDocEstudiante;
    }

    public void setNumDocEstudiante(String v) {
        this.numDocEstudiante = v;
    }

    public String getNumDocAsesor() {
        return numDocAsesor;
    }

    public void setNumDocAsesor(String v) {
        this.numDocAsesor = v;
    }

    /**
     * La tabla PRACTICA no tiene IDPROGRAMA — retrocompatibilidad con vistas
     * existentes.
     */
    public String getIdPrograma() {
        return null;
    }

    @Override
    public String toString() {
        return "Practica{id='" + idPractica + "', entidad='" + entidad + "', estado='" + estado + "'}";
    }
}
