// Declara el paquete al que pertenece esta clase
package main;

// Importa la clase SplashWindow que muestra una pantalla de presentación al iniciar
import vistas.SplashWindow;

// Importa la clase UIManager para configurar la apariencia de la interfaz gráfica
import javax.swing.*;

/**
 * 
 * SPP — Software de Prácticas Pedagógicas
 * Pedagógicas
 * Arquitectura MVC
 * • main → Punto de entrada (este archivo)
 * • vistas → Vistas Swing (interfaz gráfica)
 * • modelado → Conexión + DAOs + Clases de dominio
 * • controlador → Lógica CRUD que une vista ↔ modelo
 * Requisitos
 * • Oracle DB corriendo en 192.168.254.215:1521, SID=orcl
 * • Usuario: PROYECTOSPP / Password: PROYECTOSPP
 * • ojdbc11.jar en el classpath (dependencia Maven ojdbc11)
 * • Base de datos restaurada desde PROYECTOSPPBD.DMP
 * 
 * Clase principal que sirve como punto de entrada de la aplicación SPP.
 * Inicializa la interfaz gráfica y muestra la pantalla de bienvenida.
 */
public class Main {

    /**
     * Método main que inicia la aplicación.
     * Este es el método de entrada de la máquina virtual de Java.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {

        // Configura la apariencia de la interfaz para usar el Look & Feel nativo del SO
        // Esto hace que la aplicación se vea como una aplicación nativa del sistema
        // operativo
        try {
            // Obtiene el Look & Feel predeterminado del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla la configuración, continúa con el Look & Feel por defecto de Java
            // Esto asegura que la aplicación pueda ejecutarse aunque falle la configuración
            // visual
        }

        // Ejecuta el código de interfaz gráfica en el Event Dispatch Thread (EDT)
        // Swing no es thread-safe, por lo que toda operación de GUI debe ejecutarse en
        // el EDT
        // invokeLater() garantiza que el código se ejecuta de forma segura en el EDT
        SwingUtilities.invokeLater(() -> {

            // Opción A: Muestra una pantalla de presentación (Splash Screen) al iniciar
            // Esta ventana mostrará un logo durante 3 segundos y luego abrirá el login
            SplashWindow splash = new SplashWindow();
            // Hace visible la ventana Splash en la pantalla
            splash.setVisible(true);

            /*
             * Opción B (alternativa sin Splash Screen):
             * Si descomenta estas líneas y comenta las dos anteriores,
             * la aplicación mostrará directamente la ventana de login sin mostrar el
             * splash.
             *
             * LoginWindow login = new LoginWindow();
             * new LoginControlador(login);
             * login.setVisible(true);
             */
        });
    }
}
