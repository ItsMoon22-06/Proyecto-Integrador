// Declara el paquete al que pertenece esta clase
package modelado;

// Importa la interfaz Connection para representar una conexión con la base de datos
import java.sql.Connection;
// Importa DriverManager para obtener conexiones a bases de datos mediante una URL JDBC
import java.sql.DriverManager;
// Importa SQLException para manejar excepciones relacionadas con operaciones SQL
import java.sql.SQLException;

/**
 * Clase Singleton para gestionar la conexión a Oracle DB.
 * Proyecto SIGEP
 * 
 * Esta clase implementa el patrón Singleton para garantizar que solo exista
 * una única instancia de conexión a la base de datos en toda la aplicación,
 * lo que optimiza recursos y evita conexiones duplicadas.
 */
public class ConexionBD {

    // ── Parámetros de conexión ──────────────────────────────────────────────
    // Dirección IP del servidor Oracle donde está alojada la base de datos
    private static final String HOST      = "192.168.254.215";
    // Puerto en el que Oracle escucha las conexiones (puerto por defecto 1521)
    private static final String PUERTO    = "1521";
    // Nombre del identificador del sistema (SID) de la base de datos Oracle
    private static final String SID       = "orcl";        // Cambia si tu SID es distinto
    // Nombre de usuario para autenticarse en la base de datos Oracle
    private static final String USUARIO   = "PROYECTOSIGEP";
    // Contraseña del usuario para autenticarse en la base de datos Oracle
    private static final String PASSWORD  = "PROYECTOSIGEP";

    // Construye la URL de conexión JDBC combinando los parámetros individuales
    // Formato: jdbc:oracle:thin:@HOST:PUERTO:SID
    private static final String URL =
            "jdbc:oracle:thin:@" + HOST + ":" + PUERTO + ":" + SID;

    // ── Singleton ───────────────────────────────────────────────────────────
    // Variable estática que almacena la única instancia de la clase ConexionBD
    // Se inicializa como null y se crea la primera vez que se llama getInstancia()
    private static ConexionBD instancia;
    // Variable de instancia que mantiene la conexión actual a la base de datos Oracle
    private Connection conexion;

    // Constructor privado que previene la creación directa de objetos ConexionBD
    // Esto asegura que solo se pueda obtener instancias a través del método getInstancia()
    // y garantiza el patrón Singleton
    private ConexionBD() {}

    /**
     * Devuelve la única instancia de ConexionBD (thread-safe).
     * Este método implementa el patrón Singleton, asegurando que solo exista una instancia.
     * El modificador 'synchronized' garantiza que es seguro en ambientes multi-hilo.
     * 
     * @return La única instancia de ConexionBD
     */
    public static synchronized ConexionBD getInstancia() {
        // Verifica si la instancia aún no ha sido creada
        if (instancia == null) {
            // Si es la primera vez, crea una nueva instancia de ConexionBD
            instancia = new ConexionBD();
        }
        // Retorna la instancia (nueva o existente)
        return instancia;
    }

    // ── Métodos públicos ────────────────────────────────────────────────────

    /**
     * Abre (o reutiliza) la conexión a Oracle.
     * Si la conexión estaba rota, la descarta e intenta de nuevo.
     * Este método implementa un patrón de reconexión automática.
     * 
     * @return Connection activa con la base de datos, o null si falla la conexión
     */
    public Connection getConexion() {
        // Inicia un bloque try-catch para manejar excepciones durante la conexión
        try {
            // Verifica si no hay conexión activa (null) o si la conexión está cerrada
            if (conexion == null || conexion.isClosed()) {
                // Limpia la referencia anterior si la conexión estaba rota
                conexion = null;
                // Carga el driver JDBC de Oracle en memoria
                // Esto registra el driver para que DriverManager pueda usarlo
                Class.forName("oracle.jdbc.driver.OracleDriver");
                // Establece una nueva conexión a Oracle usando la URL y credenciales
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                // Imprime mensaje de éxito en la consola estándar
                System.out.println("✔ Conexión a Oracle establecida.");
            }
        } 
        // Captura excepción si el driver Oracle no está disponible en el classpath
        catch (ClassNotFoundException e) {
            // Imprime mensaje de error indicando que falta el jar ojdbc
            System.err.println("✘ Driver Oracle no encontrado. Agrega ojdbc.jar al proyecto.");
            // Establece conexión como nula para indicar fallo
            conexion = null;
        } 
        // Captura excepciones relacionadas con operaciones SQL y conexión a BD
        catch (SQLException e) {
            // Imprime el mensaje de error detallado de la excepción SQL
            System.err.println("✘ Error SQL al conectar: " + e.getMessage());
            // Establece conexión como nula para indicar fallo
            conexion = null;
        }
        // Retorna la conexión (válida si tuvo éxito, o null si falló)
        return conexion;
    }

    /**
     * Cierra la conexión de forma segura y permite reconectar en el siguiente getConexion().
     * Este método es importante para liberar recursos cuando la aplicación termina
     * o cuando se necesita cerrar la conexión sin terminar la aplicación.
     */
    public void cerrar() {
        // Inicia un bloque try para capturar excepciones al cerrar
        try {
            // Verifica que la conexión exista (no sea null) y esté abierta
            if (conexion != null && !conexion.isClosed()) {
                // Cierra la conexión liberando los recursos asociados
                conexion.close();
                // Imprime mensaje de éxito en la consola estándar
                System.out.println("✔ Conexión cerrada.");
            }
        } 
        // Captura excepciones SQL que pueden ocurrir al cerrar la conexión
        catch (SQLException e) {
            // Imprime el mensaje de error detallado de la excepción
            System.err.println("✘ Error al cerrar conexión: " + e.getMessage());
        } 
        // El bloque finally siempre se ejecuta, independientemente de excepciones
        finally {
            // Establece conexión como nula para permitir que getConexion() cree una nueva
            // cuando sea llamado nuevamente
            conexion = null;
        }
    }
}
