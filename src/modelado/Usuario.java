// Declara el paquete al que pertenece esta clase
package modelado;

/**
 * Clase abstracta que define los atributos y métodos comunes a todas las
 * categorías
 * de usuarios del sistema SPP (Estudiantes, Tutores Académicos, Asesores
 * Pedagógicos).
 * 
 * Esta clase utiliza herencia para evitar duplicación de código y garantizar
 * consistencia
 * en todos los tipos de usuarios del sistema. El modificador 'abstract'
 * previene su
 * instanciación directa y obliga a que las subclases implementen el método
 * toString().
 * 
 * Atributos heredados por todas las subclases:
 * - Información de identificación (número y tipo de documento)
 * - Información personal (nombre y apellido)
 * - Información de contacto (correo institucional)
 * - Credenciales (contraseña)
 * - Estado en el sistema (activo/inactivo)
 */
public abstract class Usuario {
    // Número único que identifica al usuario (cédula, pasaporte, etc.)
    protected String numDocumento;
    // Tipo de documento del usuario (Cédula de Ciudadanía, Pasaporte, etc.)
    protected String tipoDocumento;
    // Nombre del usuario
    protected String nombre;
    // Apellido del usuario
    protected String apellido;
    // Correo electrónico institucional del usuario
    protected String correoInst;
    // Contraseña encriptada del usuario para autenticación en el sistema
    protected String contrasena;
    // Estado del usuario en el sistema (Activo/Inactivo)
    protected String estado;

    /**
     * Constructor vacío que crea un usuario con todos los atributos sin
     * inicializar.
     * Se utiliza comúnmente en combinación con setters para crear objetos
     * dinámicamente.
     */
    public Usuario() {
    }

    /**
     * Constructor parametrizado que inicializa todos los atributos del usuario.
     * 
     * @param numDocumento  Número del documento de identidad
     * @param tipoDocumento Tipo de documento (ej: Cédula de Ciudadanía)
     * @param nombre        Nombre del usuario
     * @param apellido      Apellido del usuario
     * @param correoInst    Correo electrónico institucional
     * @param contrasena    Contraseña del usuario
     * @param estado        Estado del usuario en el sistema
     */
    public Usuario(String numDocumento, String tipoDocumento, String nombre,
            String apellido, String correoInst, String contrasena,
            String estado) {
        // Asigna el número de documento
        this.numDocumento = numDocumento;
        // Asigna el tipo de documento
        this.tipoDocumento = tipoDocumento;
        // Asigna el nombre
        this.nombre = nombre;
        // Asigna el apellido
        this.apellido = apellido;
        // Asigna el correo institucional
        this.correoInst = correoInst;
        // Asigna la contraseña
        this.contrasena = contrasena;
        // Asigna el estado
        this.estado = estado;
    }

    // ───────────────────────────────────────────────────────────────────────
    // GETTERS Y SETTERS
    // Métodos para acceder y modificar los atributos privados/protegidos
    // ───────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el número de documento del usuario.
     * 
     * @return Número de documento
     */
    public String getNumDocumento() {
        return numDocumento;
    }

    /**
     * Establece el número de documento del usuario.
     * 
     * @param v Nuevo número de documento
     */
    public void setNumDocumento(String v) {
        this.numDocumento = v;
    }

    /**
     * Obtiene el tipo de documento del usuario.
     * 
     * @return Tipo de documento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Establece el tipo de documento del usuario.
     * 
     * @param v Nuevo tipo de documento
     */
    public void setTipoDocumento(String v) {
        this.tipoDocumento = v;
    }

    /**
     * Obtiene el nombre del usuario.
     * 
     * @return Nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * 
     * @param v Nuevo nombre del usuario
     */
    public void setNombre(String v) {
        this.nombre = v;
    }

    /**
     * Obtiene el apellido del usuario.
     * 
     * @return Apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario.
     * 
     * @param v Nuevo apellido del usuario
     */
    public void setApellido(String v) {
        this.apellido = v;
    }

    /**
     * Obtiene el correo electrónico institucional del usuario.
     * 
     * @return Correo institucional
     */
    public String getCorreoInst() {
        return correoInst;
    }

    /**
     * Establece el correo electrónico institucional del usuario.
     * 
     * @param v Nuevo correo institucional
     */
    public void setCorreoInst(String v) {
        this.correoInst = v;
    }

    /**
     * Obtiene la contraseña del usuario.
     * 
     * @return Contraseña del usuario
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     * 
     * @param v Nueva contraseña
     */
    public void setContrasena(String v) {
        this.contrasena = v;
    }

    /**
     * Obtiene el estado del usuario en el sistema.
     * 
     * @return Estado del usuario
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del usuario en el sistema.
     * 
     * @param v Nuevo estado
     */
    public void setEstado(String v) {
        this.estado = v;
    }

    /**
     * Método abstracto que toda subclase debe implementar.
     * Retorna una representación en texto del usuario.
     * Las subclases deben proporcionar su propia implementación específica.
     * 
     * @return Representación en texto del usuario
     */
    @Override
    public abstract String toString();
}
