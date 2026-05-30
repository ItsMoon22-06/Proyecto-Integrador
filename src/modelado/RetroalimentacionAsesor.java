package modelado;

import java.sql.Date;

/**
 * Clase que representa una retroalimentación de un asesor en el sistema SIGEP.
 * 
 * Atributos:
 * - idRetroalimentacion: Identificador único de la retroalimentación
 * - comentario: Texto del comentario o evaluación
 * - fecha: Fecha en que se realizó la retroalimentación
 * - idBitacora: Referencia a la bitácora (FK → Bitacora)
 * - numDocAsesor: Documento del asesor pedagógico (FK → AsesorPedagogico)
 */
public class RetroalimentacionAsesor {
    private String idRetroalimentacion;
    private String comentario;
    private Date fecha;
    private String idBitacora;
    private String numDocAsesor;

    public RetroalimentacionAsesor() {
    }

    public RetroalimentacionAsesor(String idRetroalimentacion, String comentario, Date fecha,
            String idBitacora, String numDocAsesor) {
        this.idRetroalimentacion = idRetroalimentacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.idBitacora = idBitacora;
        this.numDocAsesor = numDocAsesor;
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

    public String getNumDocAsesor() {
        return numDocAsesor;
    }

    public void setNumDocAsesor(String numDocAsesor) {
        this.numDocAsesor = numDocAsesor;
    }

    @Override
    public String toString() {
        return "RetroalimentacionAsesor{id='" + idRetroalimentacion + "', bitacora='" + idBitacora + "'}";
    }
}
