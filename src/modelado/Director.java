package modelado;

/**
 * Clase que representa a un Director de Programa en el sistema SIGEP.
 * Mapea a la tabla DIRECTOR en la BD.
 * Estructura BD: NUMDOCUMENTO, TIPODOCUMENTO, NOMBRE, APELLIDO,
 * CORREOINST, CONTRASENA, ESTADO
 */
public class Director extends Usuario {

    public Director() {
        super();
    }

    public Director(String numDocumento, String tipoDocumento, String nombre,
            String apellido, String correoInst, String contrasena, String estado) {
        super(numDocumento, tipoDocumento, nombre, apellido, correoInst, contrasena, estado);
    }

    @Override
    public String toString() {
        return "Director{doc='" + numDocumento + "', nombre='" + nombre + " " + apellido + "', estado='" + estado
                + "'}";
    }
}
