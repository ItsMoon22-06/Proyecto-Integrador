// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase que representa a un tutor académico en el sistema SIGEP.
 * Extiende la clase Usuario, heredando todos sus atributos y métodos.
 * 
 * Un tutor académico es un profesor que guía y supervisa a los estudiantes
 * durante sus prácticas pedagógicas, asignado a un programa académico específico.
 * 
 * Atributo específico del tutor:
 * - idPrograma: Identificador del programa académico al que el tutor está asociado
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla TUTOR_ACADEMICO en la BD.
 */
public class TutorAcademico extends Usuario {
    // Identificador del programa académico al que pertenece el tutor (FK → Programa)
    private String idPrograma;

    /**
     * Constructor vacío que crea un tutor académico sin inicializar atributos.
     * Llama al constructor vacío de la clase padre Usuario.
     */
    public TutorAcademico() {
        // Invoca el constructor vacío de la clase padre
        super();
    }

    /**
     * Constructor parametrizado que inicializa todos los atributos del tutor académico.
     * Hereda los atributos comunes de Usuario e inicializa el programa específico.
     * 
     * @param numDocumento Número del documento de identidad del tutor
     * @param tipoDocumento Tipo de documento
     * @param nombre Nombre del tutor
     * @param apellido Apellido del tutor
     * @param correoInst Correo electrónico institucional
     * @param contrasena Contraseña del tutor
     * @param estado Estado del tutor en el sistema (Activo/Inactivo)
     * @param idPrograma Identificador del programa académico asignado
     */
    public TutorAcademico(String numDocumento, String tipoDocumento, String nombre,
                          String apellido, String correoInst, String contrasena,
                          String estado, String idPrograma) {
        // Invoca el constructor parametrizado de la clase padre con los atributos comunes
        super(numDocumento, tipoDocumento, nombre, apellido, correoInst, contrasena, estado);
        // Asigna el identificador del programa académico
        this.idPrograma = idPrograma;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS ESPECÍFICOS DEL TUTOR
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el identificador del programa académico del tutor.
     * @return ID del programa
     */
    public String getIdPrograma() { return idPrograma; }
    /**
     * Establece el identificador del programa académico del tutor.
     * @param v Nuevo ID del programa
     */
    public void setIdPrograma(String v) { this.idPrograma = v; }

    /**
     * Retorna una representación en texto del tutor académico.
     * Muestra información clave: documento y nombre completo del tutor.
     * 
     * @return Cadena que representa al tutor en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información del tutor académico
        return "TutorAcademico{doc='" + numDocumento + "', nombre='" + nombre + " " + apellido + "'}";
    }
}
