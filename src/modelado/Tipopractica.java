// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase que representa un tipo de práctica pedagógica en el sistema SIGEP.
 * 
 * Los tipos de prácticas definen categorías de experiencias educativas organizadas
 * por semestre académico, con requisitos específicos de horas de dedicación.
 * 
 * Atributos:
 * - idTipopractica: Identificador único del tipo de práctica
 * - nombre: Nombre descriptivo del tipo de práctica
 * - numSemestre: Semestre académico en el que se realiza
 * - horasRequeridas: Cantidad de horas que debe cumplir el estudiante
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla TIPOPRACTICA en la BD.
 */
public class Tipopractica {
    // Identificador único del tipo de práctica (PK)
    private String idTipopractica;
    // Nombre descriptivo del tipo de práctica
    private String nombre;
    // Número del semestre académico en que se realiza la práctica
    private int    numSemestre;
    // Cantidad de horas que debe dedicar el estudiante a este tipo de práctica
    private int    horasRequeridas;

    /**
     * Constructor vacío que crea un tipo de práctica sin inicializar atributos.
     * Se utiliza cuando el objeto será llenado posteriormente con setters.
     */
    public Tipopractica() {}

    /**
     * Constructor parametrizado que inicializa todos los atributos del tipo de práctica.
     * 
     * @param idTipopractica Identificador único del tipo de práctica
     * @param nombre Nombre descriptivo del tipo de práctica
     * @param numSemestre Número del semestre académico
     * @param horasRequeridas Horas requeridas para completar la práctica
     */
    public Tipopractica(String idTipopractica, String nombre, int numSemestre, int horasRequeridas) {
        // Asigna el identificador único del tipo de práctica
        this.idTipopractica  = idTipopractica;
        // Asigna el nombre del tipo de práctica
        this.nombre          = nombre;
        // Asigna el número de semestre
        this.numSemestre     = numSemestre;
        // Asigna el número de horas requeridas
        this.horasRequeridas = horasRequeridas;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador del tipo de práctica.
     * @return ID del tipo de práctica
     */
    public String getIdTipopractica()  { return idTipopractica; }
    /**
     * Establece el identificador del tipo de práctica.
     * @param v Nuevo ID del tipo de práctica
     */
    public void   setIdTipopractica(String v)  { this.idTipopractica = v; }

    /**
     * Obtiene el nombre del tipo de práctica.
     * @return Nombre del tipo de práctica
     */
    public String getNombre()  { return nombre; }
    /**
     * Establece el nombre del tipo de práctica.
     * @param v Nuevo nombre del tipo de práctica
     */
    public void   setNombre(String v)  { this.nombre = v; }

    /**
     * Obtiene el número del semestre académico.
     * @return Número del semestre
     */
    public int    getNumSemestre() { return numSemestre; }
    /**
     * Establece el número del semestre académico.
     * @param v Nuevo número de semestre
     */
    public void   setNumSemestre(int v) { this.numSemestre = v; }

    /**
     * Obtiene las horas requeridas para completar el tipo de práctica.
     * @return Horas requeridas
     */
    public int    getHorasRequeridas() { return horasRequeridas; }
    /**
     * Establece las horas requeridas.
     * @param v Nuevo número de horas requeridas
     */
    public void   setHorasRequeridas(int v) { this.horasRequeridas = v; }

    /**
     * Retorna una representación en texto del tipo de práctica.
     * Muestra el ID, nombre y semestre del tipo de práctica.
     * 
     * @return Cadena que representa el tipo de práctica en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información del tipo de práctica
        return "Tipopractica{id='" + idTipopractica + "', nombre='" + nombre + "', semestre=" + numSemestre + "}";
    }
}
