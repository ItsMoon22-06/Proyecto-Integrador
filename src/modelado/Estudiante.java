// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase que representa a un estudiante en el sistema SIGEP.
 * Extiende la clase Usuario, heredando todos sus atributos y métodos.
 * 
 * Atributos específicos del estudiante:
 * - idPrograma: Identificador del programa académico al que pertenece
 * - idPractica: Identificador de la práctica que está realizando
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla ESTUDIANTE en la BD.
 */
public class Estudiante extends Usuario {
    // Identificador del programa académico al que pertenece el estudiante (FK → Programa)
    private String idPrograma;
    // Identificador de la práctica que el estudiante está realizando (FK → Practica)
    private String idPractica;

    /**
     * Constructor vacío que crea un estudiante sin inicializar atributos específicos.
     * Llama al constructor vacío de la clase padre Usuario.
     */
    public Estudiante() {
        // Invoca el constructor vacío de la clase padre
        super();
    }

    /**
     * Constructor parametrizado que inicializa todos los atributos del estudiante.
     * Hereda los atributos comunes de Usuario e inicializa los específicos del estudiante.
     * 
     * @param numDocumento Número del documento de identidad del estudiante
     * @param tipoDocumento Tipo de documento
     * @param nombre Nombre del estudiante
     * @param apellido Apellido del estudiante
     * @param correoInst Correo electrónico institucional
     * @param contrasena Contraseña del estudiante
     * @param estado Estado del estudiante (Activo/Inactivo)
     * @param idPrograma Identificador del programa académico
     * @param idPractica Identificador de la práctica asignada
     */
    public Estudiante(String numDocumento, String tipoDocumento, String nombre,
                      String apellido, String correoInst, String contrasena,
                      String estado, String idPrograma, String idPractica) {
        // Invoca el constructor parametrizado de la clase padre con los atributos comunes
        super(numDocumento, tipoDocumento, nombre, apellido, correoInst, contrasena, estado);
        // Asigna el identificador del programa
        this.idPrograma = idPrograma;
        // Asigna el identificador de la práctica
        this.idPractica = idPractica;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS ESPECÍFICOS DEL ESTUDIANTE
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador del programa académico del estudiante.
     * @return ID del programa
     */
    public String getIdPrograma() { return idPrograma; }
    /**
     * Establece el identificador del programa académico del estudiante.
     * @param v Nuevo ID del programa
     */
    public void setIdPrograma(String v) { this.idPrograma = v; }

    /**
     * Obtiene el identificador de la práctica asignada al estudiante.
     * @return ID de la práctica
     */
    public String getIdPractica() { return idPractica; }
    /**
     * Establece el identificador de la práctica del estudiante.
     * @param v Nuevo ID de la práctica
     */
    public void setIdPractica(String v) { this.idPractica = v; }

    /**
     * Retorna una representación en texto del estudiante.
     * Muestra información clave del estudiante: documento, nombre completo y estado.
     * 
     * @return Cadena que representa el estudiante en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información del estudiante
        return "Estudiante{doc='" + numDocumento + "', nombre='" + nombre + " " + apellido + "', estado='" + estado + "'}";
    }
}
