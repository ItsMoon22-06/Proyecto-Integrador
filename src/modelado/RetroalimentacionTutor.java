package modelado;

import java.sql.Date;

/**
 * Clase que representa una retroalimentación de un tutor en el sistema SIGEP.
 * 
 * Atributos:
 * - idRetroalimentacion: Identificador único de la retroalimentación
 * - comentario: Texto del comentario o evaluación
 * - fecha: Fecha en que se realizó la retroalimentación
 * - idBitacora: Referencia a la bitácora (FK → Bitacora)
 * - numDocTutor: Documento del tutor académico (FK → TutorAcademico)
 */
public class RetroalimentacionTutor {
    private String idRetroalimentacion;
    private String comentario;
    private Date fecha;
    private String idBitacora;
    private String numDocTutor;

    public RetroalimentacionTutor() {
    }

    public RetroalimentacionTutor(String idRetroalimentacion, String comentario, Date fecha,
            String idBitacora, String numDocTutor) {
        this.idRetroalimentacion = idRetroalimentacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.idBitacora = idBitacora;
        this.numDocTutor = numDocTutor;
    }

    public String getIdRetroalimentacion() {
        return idRetroalimentacion;
    }

    public void setIdRetroalimentacion(String idRetroalimentacion) {
        this.idRetroalimentacion = idRetroalimentacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(String idBitacora) {
        this.idBitacora = idBitacora;
    }

    public String getNumDocTutor() {
        return numDocTutor;
    }

    public void setNumDocTutor(String numDocTutor) {
        this.numDocTutor = numDocTutor;
    }

    @Override
    public String toString() {
        return "RetroalimentacionTutor{id='" + idRetroalimentacion + "', bitacora='" + idBitacora + "'}";
    }
}
