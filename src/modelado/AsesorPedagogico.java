// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase que representa a un asesor pedagógico en el sistema SIGEP.
 * Extiende la clase Usuario, heredando todos sus atributos y métodos.
 * 
 * Un asesor pedagógico es responsable de supervisar y guiar los procesos
 * académicos y pedagógicos de los estudiantes en sus prácticas.
 * 
 * Esta clase no tiene atributos adicionales específicos más allá de los
 * heredados
 * de Usuario, pero proporciona su propia representación en texto personalizada.
 * 
 * Esta clase es una entidad de dominio que mapea a la tabla ASESOR_PEDAGOGICO
 * en la BD.
 */
public class AsesorPedagogico extends Usuario {

    /**
     * Constructor vacío que crea un asesor pedagógico sin inicializar atributos.
     * Llama al constructor vacío de la clase padre Usuario.
     */
    public AsesorPedagogico() {
        // Invoca el constructor vacío de la clase padre Usuario
        super();
    }

    /**
     * Constructor parametrizado que inicializa todos los atributos del asesor
     * pedagógico.
     * Los atributos son los heredados de Usuario, ya que AsesorPedagogico no tiene
     * atributos adicionales específicos.
     * 
     * @param numDocumento  Número del documento de identidad del asesor
     * @param tipoDocumento Tipo de documento
     * @param nombre        Nombre del asesor
     * @param apellido      Apellido del asesor
     * @param correoInst    Correo electrónico institucional
     * @param contrasena    Contraseña del asesor
     * @param estado        Estado del asesor en el sistema (Activo/Inactivo)
     */
    public AsesorPedagogico(String numDocumento, String tipoDocumento, String nombre,
            String apellido, String correoInst, String contrasena,
            String estado) {
        // Invoca el constructor parametrizado de la clase padre con todos los
        // parámetros
        super(numDocumento, tipoDocumento, nombre, apellido, correoInst, contrasena, estado);
    }

    /**
     * Retorna una representación en texto del asesor pedagógico.
     * Muestra información clave: documento y nombre completo del asesor.
     * 
     * @return Cadena que representa al asesor en formato legible
     */
    @Override
    public String toString() {
        // Construye y retorna una cadena con información del asesor pedagógico
        return "AsesorPedagogico{doc='" + numDocumento + "', nombre='" + nombre + " " + apellido + "'}";
    }
}
