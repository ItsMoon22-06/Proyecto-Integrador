// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase que representa una evidencia en el sistema SPP.
 * 
 * Una evidencia es un archivo o documento que comprueba y respalda el trabajo
 * y logros del estudiante durante su práctica pedagógica. Las evidencias se
 * adjuntan a una bitácora específica.
 * 
 * Atributos:
 * - idEvidencia: Identificador único de la evidencia
 * - archivo: Contenido binario del archivo de evidencia
 * - idBitacora: Referencia a la bitácora a la que pertenece (FK → Bitacora)
 * - nombreArchivo: Nombre original del archivo
 * - descripcion: Descripción del contenido o propósito de la evidencia
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla EVIDENCIA en la BD.
 */
public class Evidencia {
    // Identificador único de la evidencia (PK)
    private String idEvidencia;
    // Contenido binario del archivo de evidencia
    private byte[] archivo;
    // Identificador de la bitácora a la que pertenece la evidencia (FK → Bitacora)
    private String idBitacora;
    // Nombre original del archivo
    private String nombreArchivo;
    // Descripción de la evidencia
    private String descripcion;

    /**
     * Constructor vacío que crea una evidencia sin inicializar atributos.
     * Se utiliza cuando el objeto será llenado posteriormente con setters.
     */
    public Evidencia() {
    }

    /**
     * Constructor parametrizado que inicializa todos los atributos de la evidencia.
     * 
     * @param idEvidencia   Identificador único de la evidencia
     * @param archivo       Contenido binario del archivo
     * @param idBitacora    ID de la bitácora a la que pertenece
     * @param nombreArchivo Nombre del archivo
     * @param descripcion   Descripción de la evidencia
     */
    public Evidencia(String idEvidencia, byte[] archivo, String idBitacora, String nombreArchivo, String descripcion) {
        // Asigna el identificador único de la evidencia
        this.idEvidencia = idEvidencia;
        // Asigna el contenido binario del archivo
        this.archivo = archivo;
        // Asigna el ID de la bitácora
        this.idBitacora = idBitacora;
        // Asigna el nombre del archivo
        this.nombreArchivo = nombreArchivo;
        // Asigna la descripción
        this.descripcion = descripcion;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador de la evidencia.
     * 
     * @return ID de la evidencia
     */
    public String getIdEvidencia() {
        return idEvidencia;
    }

    /**
     * Establece el identificador de la evidencia.
     * 
     * @param idEvidencia Nuevo ID de la evidencia
     */
    public void setIdEvidencia(String idEvidencia) {
        this.idEvidencia = idEvidencia;
    }

    /**
     * Obtiene el contenido binario del archivo de evidencia.
     * 
     * @return Contenido del archivo en formato binario
     */
    public byte[] getArchivo() {
        return archivo;
    }

    /**
     * Establece el contenido binario del archivo.
     * 
     * @param archivo Nuevo contenido del archivo
     */
    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    /**
     * Obtiene el identificador de la bitácora a la que pertenece.
     * 
     * @return ID de la bitácora
     */
    public String getIdBitacora() {
        return idBitacora;
    }

    /**
     * Establece el identificador de la bitácora.
     * 
     * @param idBitacora Nuevo ID de la bitácora
     */
    public void setIdBitacora(String idBitacora) {
        this.idBitacora = idBitacora;
    }

    /**
     * Obtiene el nombre original del archivo.
     * 
     * @return Nombre del archivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Establece el nombre del archivo.
     * 
     * @param nombreArchivo Nuevo nombre del archivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Obtiene la descripción de la evidencia.
     * 
     * @return Descripción de la evidencia
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la evidencia.
     * 
     * @param descripcion Nueva descripción
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
