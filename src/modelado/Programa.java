// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase que representa un programa académico en el sistema SIGEP.
 * 
 * Un programa académico es un conjunto de estudios organizados que un
 * estudiante
 * realiza en la institución educativa (ej: Ingeniería en Sistemas,
 * Administración, etc.).
 * 
 * Atributos:
 * - idPrograma: Identificador único del programa
 * - nombre: Nombre del programa académico
 * - facultad: Nombre de la facultad a la que pertenece el programa
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla PROGRAMA en la BD.
 */
public class Programa {
    // Identificador único del programa académico (PK)
    private String idPrograma;
    // Nombre descriptivo del programa académico
    private String nombre;
    // Nombre de la facultad a la que pertenece el programa
    private String facultad;

    /**
     * Constructor vacío que crea un programa sin inicializar atributos.
     * Se utiliza cuando el objeto será llenado posteriormente con setters.
     */
    public Programa() {
    }

    /**
     * Constructor parametrizado que inicializa todos los atributos del programa.
     * 
     * @param idPrograma Identificador único del programa
     * @param nombre     Nombre del programa académico
     * @param facultad   Nombre de la facultad
     */
    public Programa(String idPrograma, String nombre, String facultad) {
        // Asigna el identificador único del programa
        this.idPrograma = idPrograma;
        // Asigna el nombre del programa
        this.nombre = nombre;
        // Asigna el nombre de la facultad
        this.facultad = facultad;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador del programa.
     * 
     * @return ID del programa
     */
    public String getIdPrograma() {
        return idPrograma;
    }

    /**
     * Establece el identificador del programa.
     * 
     * @param idPrograma Nuevo ID del programa
     */
    public void setIdPrograma(String idPrograma) {
        this.idPrograma = idPrograma;
    }

    /**
     * Obtiene el nombre del programa académico.
     * 
     * @return Nombre del programa
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del programa académico.
     * 
     * @param nombre Nuevo nombre del programa
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el nombre de la facultad.
     * 
     * @return Nombre de la facultad
     */
    public String getFacultad() {
        return facultad;
    }

    /**
     * Establece el nombre de la facultad.
     * 
     * @param facultad Nuevo nombre de la facultad
     */
    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    /**
     * Retorna una representación en texto del programa.
     * Muestra el ID, nombre y facultad del programa.
     * 
     * @return Cadena que representa el programa en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información del programa
        return "Programa{id='" + idPrograma + "', nombre='" + nombre + "', facultad='" + facultad + "'}";
    }
}
