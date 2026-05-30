package modelado;

/**
 * Mapea la tabla ESTUDIANTE (esquema real del DMP).
 *
 * Columnas: NUMDOCUMENTO, TIPODOCUMENTO, NOMBRE, APELLIDO,
 * CORREOINST, CONTRASENA, ESTADO, IDPROGRAMA
 *
 * NOTA: La tabla NO tiene IDPRACTICA. Las prácticas del estudiante
 * se buscan en PRACTICA.NUMDOCESTUDIANTE.
 */
public class Estudiante extends Usuario {

    private String idPrograma;

    public Estudiante() {
        super();
    }

    public Estudiante(String numDocumento, String tipoDocumento, String nombre,
            String apellido, String correoInst, String contrasena,
            String estado, String idPrograma) {
        super(numDocumento, tipoDocumento, nombre, apellido, correoInst, contrasena, estado);
        this.idPrograma = idPrograma;
    }

    public String getIdPrograma() {
        return idPrograma;
    }

    public void setIdPrograma(String v) {
        this.idPrograma = v;
    }

    /** Retrocompatibilidad — la tabla no tiene IDPRACTICA. */
    public String getIdPractica() {
        return getNumDocumento();
    }

    @Override
    public String toString() {
        return "Estudiante{doc='" + numDocumento + "', nombre='" + nombre + " " + apellido + "', estado='" + estado
                + "'}";
    }
}
