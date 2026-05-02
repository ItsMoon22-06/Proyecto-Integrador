// Declara el paquete al que pertenece esta clase
package modelado;
  
// Importa la clase Date de SQL para manejar fechas de la base de datos
import java.sql.Date;

/**
 * Clase que representa una retroalimentación en el sistema SIGEP.
 * 
 * Una retroalimentación es un comentario o evaluación que proporciona un tutor académico
 * o asesor pedagógico sobre el trabajo, comportamiento o desempeño del estudiante durante
 * su práctica pedagógica. Se adjunta a una bitácora específica.
 * 
 * Atributos:
 * - idRetroalimentacion: Identificador único de la retroalimentación
 * - comentario: Texto del comentario o evaluación
 * - fecha: Fecha en que se realizó la retroalimentación
 * - idBitacora: Referencia a la bitácora (FK → Bitacora)
 * - numDocTutor: Documento del tutor académico (FK → TutorAcademico, puede ser null)
 * - numDocAsesor: Documento del asesor pedagógico (FK → AsesorPedagogico, puede ser null)
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla RETROALIMENTACION en la BD.
 */
public class Retroalimentacion {
    // Identificador único de la retroalimentación (PK)
    private String idRetroalimentacion;
    // Texto del comentario o evaluación proporcionada
    private String comentario;
    // Fecha en que se creó la retroalimentación
    private Date   fecha;
    // Identificador de la bitácora a la que pertenece (FK → Bitacora)
    private String idBitacora;
    // Número de documento del tutor académico que proporciona retroalimentación (FK → TutorAcademico, puede ser null)
    private String numDocTutor;
    // Número de documento del asesor pedagógico que proporciona retroalimentación (FK → AsesorPedagogico, puede ser null)
    private String numDocAsesor;

    /**
     * Constructor vacío que crea una retroalimentación sin inicializar atributos.
     * Se utiliza cuando el objeto será llenado posteriormente con setters.
     */
    public Retroalimentacion() {}

    /**
     * Constructor parametrizado que inicializa todos los atributos de la retroalimentación.
     * 
     * @param idRetroalimentacion Identificador único de la retroalimentación
     * @param comentario Texto del comentario
     * @param fecha Fecha de la retroalimentación
     * @param idBitacora ID de la bitácora a la que pertenece
     * @param numDocTutor Documento del tutor académico (puede ser null)
     * @param numDocAsesor Documento del asesor pedagógico (puede ser null)
     */
    public Retroalimentacion(String idRetroalimentacion, String comentario, Date fecha,
                             String idBitacora, String numDocTutor, String numDocAsesor) {
        // Asigna el identificador único de la retroalimentación
        this.idRetroalimentacion = idRetroalimentacion;
        // Asigna el comentario
        this.comentario          = comentario;
        // Asigna la fecha
        this.fecha               = fecha;
        // Asigna el ID de la bitácora
        this.idBitacora          = idBitacora;
        // Asigna el documento del tutor (puede ser null si es proporcionada por el asesor)
        this.numDocTutor         = numDocTutor;
        // Asigna el documento del asesor (puede ser null si es proporcionada por el tutor)
        this.numDocAsesor        = numDocAsesor;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador de la retroalimentación.
     * @return ID de la retroalimentación
     */
    public String getIdRetroalimentacion() { return idRetroalimentacion; }
    /**
     * Establece el identificador de la retroalimentación.
     * @param v Nuevo ID de la retroalimentación
     */
    public void   setIdRetroalimentacion(String v) { this.idRetroalimentacion = v; }

    /**
     * Obtiene el comentario de la retroalimentación.
     * @return Texto del comentario
     */
    public String getComentario() { return comentario; }
    /**
     * Establece el comentario de la retroalimentación.
     * @param v Nuevo comentario
     */
    public void   setComentario(String v) { this.comentario = v; }

    /**
     * Obtiene la fecha de la retroalimentación.
     * @return Fecha de la retroalimentación
     */
    public Date   getFecha() { return fecha; }
    /**
     * Establece la fecha de la retroalimentación.
     * @param v Nueva fecha
     */
    public void   setFecha(Date v) { this.fecha = v; }

    /**
     * Obtiene el ID de la bitácora a la que pertenece.
     * @return ID de la bitácora
     */
    public String getIdBitacora() { return idBitacora; }
    /**
     * Establece el ID de la bitácora.
     * @param v Nuevo ID de la bitácora
     */
    public void   setIdBitacora(String v) { this.idBitacora = v; }

    /**
     * Obtiene el documento del tutor académico.
     * @return Número de documento del tutor (puede ser null)
     */
    public String getNumDocTutor() { return numDocTutor; }
    /**
     * Establece el documento del tutor académico.
     * @param v Nuevo número de documento del tutor
     */
    public void   setNumDocTutor(String v) { this.numDocTutor = v; }

    /**
     * Obtiene el documento del asesor pedagógico.
     * @return Número de documento del asesor (puede ser null)
     */
    public String getNumDocAsesor() { return numDocAsesor; }
    /**
     * Establece el documento del asesor pedagógico.
     * @param v Nuevo número de documento del asesor
     */
    public void   setNumDocAsesor(String v) { this.numDocAsesor = v; }

    /**
     * Retorna una representación en texto de la retroalimentación.
     * Muestra el ID de la retroalimentación y el ID de la bitácora asociada.
     * 
     * @return Cadena que representa la retroalimentación en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información de la retroalimentación
        return "Retroalimentacion{id='" + idRetroalimentacion + "', bitacora='" + idBitacora + "'}";
    }
}
