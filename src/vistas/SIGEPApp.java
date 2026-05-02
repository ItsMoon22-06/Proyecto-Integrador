
package vistas;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación SIGEP (en package vista).
 * Lanza el SplashScreen; el Main en el package main es el que
 * el IDE/JVM ejecuta directamente.
 */
public class SIGEPApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashWindow splash = new SplashWindow();
            splash.setVisible(true);
        });
    }
}
