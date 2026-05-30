package modelado;

import java.sql.Date;

/**
 * Mapea la tabla BITACORA (esquema real del DMP).
 *
 * Columnas: IDBITACORA, FECHACREACION, NOTAFINAL, OBSERVACIONFINAL, IDPRACTICA
 *
 * NOTA: La tabla NO tiene NUMDOCUMENTO. El estudiante se accede
 * a través de PRACTICA.NUMDOCESTUDIANTE.
 */
public class Bitacora {

    private String idBitacora;
    private Date fechaCreacion;
    private double notaFinal;
    private String observacionFinal;
    private String idPractica;
    private String estado;

    // Campos transitorios (no columnas BD) — usados en la UI
    private byte[] archivo;
    private String nombreArchivo;
    private String descripcion;

    public Bitacora() {
    }

    public Bitacora(String idBitacora, Date fechaCreacion, double notaFinal,
            String observacionFinal, String idPractica, String estado) {
        this.idBitacora = idBitacora;
        this.fechaCreacion = fechaCreacion;
        this.notaFinal = notaFinal;
        this.observacionFinal = observacionFinal;
        this.idPractica = idPractica;
        this.estado = estado;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(String v) {
        this.idBitacora = v;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date v) {
        this.fechaCreacion = v;
    }

    public double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(double v) {
        this.notaFinal = v;
    }

    public String getObservacionFinal() {
        return observacionFinal;
    }

    public void setObservacionFinal(String v) {
        this.observacionFinal = v;
    }

    public String getIdPractica() {
        return idPractica;
    }

    public void setIdPractica(String v) {
        this.idPractica = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        this.estado = v;
    }

    // Transitorios UI
    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] v) {
        this.archivo = v;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String v) {
        this.nombreArchivo = v;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String v) {
        this.descripcion = v;
    }

    /** Retrocompatibilidad — la tabla no tiene NUMDOCUMENTO. */
    public String getNumDocumento() {
        return null;
    }

    @Override
    public String toString() {
        return "Bitacora{id='" + idBitacora + "', practica='" + idPractica + "'}";
    }
}
