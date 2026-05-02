// Declara el paquete al que pertenece esta clase
package modelado;

// Importa la clase Date de SQL para manejar fechas de la base de datos
import java.sql.Date;

/**
 * Clase que representa una bitácora en el sistema SIGEP.
 * 
 * Una bitácora es un registro documentado del progreso y observaciones de una práctica
 * pedagógica. Registra la evaluación final, notas y comentarios sobre el desempeño del estudiante.
 * 
 * Atributos principales:
 * - idBitacora: Identificador único de la bitácora
 * - fechaCreacion: Fecha en que se creó el registro
 * - notaFinal: Calificación final obtenida
 * - observacionFinal: Comentarios finales del tutor
 * - numDocumento: Documento del estudiante (FK → Estudiante)
 * - idPractica: Referencia a la práctica (FK → Practica)
 * 
 * Atributos adicionales:
 * - archivo: Contenido binario del archivo de la bitácora
 * - nombreArchivo: Nombre del archivo asociado
 * - descripcion: Descripción adicional de la bitácora
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla BITACORA en la BD.
 */
public class Bitacora {
    // Identificador único de la bitácora (PK)
    private String idBitacora;
    // Fecha en que se creó la bitácora
    private Date   fechaCreacion;
    // Calificación final del estudiante en la práctica
    private double notaFinal;
    // Observaciones finales del tutor sobre el desempeño
    private String observacionFinal;
    // Número de documento del estudiante (FK → Estudiante)
    private String numDocumento;
    // Identificador de la práctica (FK → Practica)
    private String idPractica;
    // Contenido binario del archivo adjunto (ej: PDF, documento)
    private byte[] archivo;
    // Nombre original del archivo
    private String nombreArchivo;
    // Descripción adicional de la bitácora
    private String descripcion;

    /**
     * Constructor vacío que crea una bitácora sin inicializar atributos.
     * Se utiliza cuando el objeto será llenado posteriormente con setters.
     */
    public Bitacora() {}

    /**
     * Constructor parametrizado que inicializa los atributos principales de la bitácora.
     * (Este constructor no inicializa los atributos adicionales de archivo)
     * 
     * @param idBitacora Identificador único de la bitácora
     * @param fechaCreacion Fecha de creación del registro
     * @param notaFinal Calificación final
     * @param observacionFinal Observaciones finales del tutor
     * @param numDocumento Documento del estudiante
     * @param idPractica ID de la práctica
     */
    public Bitacora(String idBitacora, Date fechaCreacion, double notaFinal,
                    String observacionFinal, String numDocumento, String idPractica) {
        // Asigna el identificador único de la bitácora
        this.idBitacora       = idBitacora;
        // Asigna la fecha de creación
        this.fechaCreacion    = fechaCreacion;
        // Asigna la nota final
        this.notaFinal        = notaFinal;
        // Asigna la observación final
        this.observacionFinal = observacionFinal;
        // Asigna el número de documento del estudiante
        this.numDocumento     = numDocumento;
        // Asigna el ID de la práctica
        this.idPractica       = idPractica;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador de la bitácora.
     * @return ID de la bitácora
     */
    public String getIdBitacora()           { return idBitacora; }
    /**
     * Establece el identificador de la bitácora.
     * @param v Nuevo ID de la bitácora
     */
    public void   setIdBitacora(String v)   { this.idBitacora = v; }

    /**
     * Obtiene la fecha de creación de la bitácora.
     * @return Fecha de creación
     */
    public Date   getFechaCreacion()        { return fechaCreacion; }
    /**
     * Establece la fecha de creación.
     * @param v Nueva fecha de creación
     */
    public void   setFechaCreacion(Date v)  { this.fechaCreacion = v; }

    /**
     * Obtiene la nota final del estudiante.
     * @return Nota final (valor numérico)
     */
    public double getNotaFinal()            { return notaFinal; }
    /**
     * Establece la nota final.
     * @param v Nueva nota final
     */
    public void   setNotaFinal(double v)    { this.notaFinal = v; }

    /**
     * Obtiene las observaciones finales del tutor.
     * @return Observación final
     */
    public String getObservacionFinal()     { return observacionFinal; }
    /**
     * Establece las observaciones finales.
     * @param v Nueva observación final
     */
    public void   setObservacionFinal(String v) { this.observacionFinal = v; }

    /**
     * Obtiene el número de documento del estudiante.
     * @return Número de documento
     */
    public String getNumDocumento()         { return numDocumento; }
    /**
     * Establece el número de documento.
     * @param v Nuevo número de documento
     */
    public void   setNumDocumento(String v) { this.numDocumento = v; }

    /**
     * Obtiene el identificador de la práctica.
     * @return ID de la práctica
     */
    public String getIdPractica()           { return idPractica; }
    /**
     * Establece el identificador de la práctica.
     * @param v Nuevo ID de la práctica
     */
    public void   setIdPractica(String v)   { this.idPractica = v; }

    /**
     * Obtiene el contenido binario del archivo adjunto.
     * @return Contenido del archivo en formato binario
     */
    public byte[] getArchivo()              { return archivo; }
    /**
     * Establece el contenido binario del archivo.
     * @param v Nuevo contenido del archivo
     */
    public void   setArchivo(byte[] v)      { this.archivo = v; }

    /**
     * Obtiene el nombre del archivo.
     * @return Nombre del archivo
     */
    public String getNombreArchivo()        { return nombreArchivo; }
    /**
     * Establece el nombre del archivo.
     * @param v Nuevo nombre del archivo
     */
    public void   setNombreArchivo(String v){ this.nombreArchivo = v; }

    /**
     * Obtiene la descripción de la bitácora.
     * @return Descripción
     */
    public String getDescripcion()          { return descripcion; }
    /**
     * Establece la descripción de la bitácora.
     * @param v Nueva descripción
     */
    public void   setDescripcion(String v)  { this.descripcion = v; }

    /**
     * Retorna una representación en texto de la bitácora.
     * Muestra el ID y la nota final.
     * 
     * @return Cadena que representa la bitácora en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información de la bitácora
        return "Bitacora{id='" + idBitacora + "', nota=" + notaFinal + "'}";
    }
}
