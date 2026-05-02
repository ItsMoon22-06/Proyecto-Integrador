// Declara el paquete al que pertenece esta clase
package modelado;

// Importa la clase Date de SQL para manejar fechas de la base de datos
import java.sql.Date;

/**
 * Clase que representa una práctica pedagógica en el sistema SIGEP.
 * 
 * Una práctica pedagógica es una experiencia educativa donde el estudiante aplica
 * conocimientos teóricos en un entorno real, supervisado por un tutor académico.
 * 
 * Atributos:
 * - idPractica: Identificador único de la práctica
 * - fecha: Fecha en que se realizó la práctica
 * - entidad: Nombre de la entidad/empresa donde se realizó
 * - estado: Estado actual de la práctica (En curso, Completada, Pendiente)
 * - idTipopractica: Referencia al tipo de práctica (FK)
 * - idPrograma: Referencia al programa académico (FK)
 * - numDocTutor: Documento del tutor académico asignado (FK)
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla PRACTICA en la BD.
 */
public class Practica {
    // Identificador único de la práctica (PK)
    private String idPractica;
    // Fecha en que se realizó la práctica
    private Date   fecha;
    // Nombre de la entidad/empresa donde se desarrolló la práctica
    private String entidad;
    // Estado actual de la práctica
    private String estado;
    // Identificador del tipo de práctica (FK → Tipopractica)
    private String idTipopractica;
    // Identificador del programa académico (FK → Programa)
    private String idPrograma;
    // Número de documento del tutor académico asignado (FK → TutorAcademico)
    private String numDocTutor;

    /**
     * Constructor vacío que crea una práctica sin inicializar atributos.
     * Se utiliza cuando el objeto será llenado posteriormente con setters.
     */
    public Practica() {}

    /**
     * Constructor parametrizado que inicializa todos los atributos de la práctica.
     * 
     * @param idPractica Identificador único de la práctica
     * @param fecha Fecha de la práctica
     * @param entidad Nombre de la entidad donde se realizó
     * @param estado Estado actual de la práctica
     * @param idTipopractica ID del tipo de práctica
     * @param idPrograma ID del programa académico
     * @param numDocTutor Documento del tutor asignado
     */
    public Practica(String idPractica, Date fecha, String entidad, String estado,
                    String idTipopractica, String idPrograma, String numDocTutor) {
        // Asigna el identificador único de la práctica
        this.idPractica     = idPractica;
        // Asigna la fecha de la práctica
        this.fecha          = fecha;
        // Asigna el nombre de la entidad
        this.entidad        = entidad;
        // Asigna el estado de la práctica
        this.estado         = estado;
        // Asigna el tipo de práctica
        this.idTipopractica = idTipopractica;
        // Asigna el programa académico
        this.idPrograma     = idPrograma;
        // Asigna el documento del tutor
        this.numDocTutor    = numDocTutor;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador de la práctica.
     * @return ID de la práctica
     */
    public String getIdPractica()  { return idPractica; }
    /**
     * Establece el identificador de la práctica.
     * @param v Nuevo ID de la práctica
     */
    public void   setIdPractica(String v)  { this.idPractica = v; }

    /**
     * Obtiene la fecha de la práctica.
     * @return Fecha de la práctica
     */
    public Date   getFecha() { return fecha; }
    /**
     * Establece la fecha de la práctica.
     * @param v Nueva fecha
     */
    public void   setFecha(Date v) { this.fecha = v; }

    /**
     * Obtiene el nombre de la entidad donde se realizó la práctica.
     * @return Nombre de la entidad
     */
    public String getEntidad() { return entidad; }
    /**
     * Establece el nombre de la entidad.
     * @param v Nuevo nombre de entidad
     */
    public void   setEntidad(String v) { this.entidad = v; }

    /**
     * Obtiene el estado de la práctica.
     * @return Estado de la práctica
     */
    public String getEstado() { return estado; }
    /**
     * Establece el estado de la práctica.
     * @param v Nuevo estado
     */
    public void   setEstado(String v) { this.estado = v; }

    /**
     * Obtiene el tipo de práctica.
     * @return ID del tipo de práctica
     */
    public String getIdTipopractica() { return idTipopractica; }
    /**
     * Establece el tipo de práctica.
     * @param v Nuevo ID de tipo
     */
    public void   setIdTipopractica(String v) { this.idTipopractica = v; }

    /**
     * Obtiene el programa académico de la práctica.
     * @return ID del programa
     */
    public String getIdPrograma() { return idPrograma; }
    /**
     * Establece el programa académico.
     * @param v Nuevo ID de programa
     */
    public void   setIdPrograma(String v) { this.idPrograma = v; }

    /**
     * Obtiene el documento del tutor académico asignado.
     * @return Documento del tutor
     */
    public String getNumDocTutor() { return numDocTutor; }
    /**
     * Establece el documento del tutor académico.
     * @param v Nuevo documento del tutor
     */
    public void   setNumDocTutor(String v) { this.numDocTutor = v; }

    /**
     * Retorna una representación en texto de la práctica.
     * Muestra el ID, entidad y estado de la práctica.
     * 
     * @return Cadena que representa la práctica en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información de la práctica
        return "Practica{id='" + idPractica + "', entidad='" + entidad + "', estado='" + estado + "'}";
    }
}
