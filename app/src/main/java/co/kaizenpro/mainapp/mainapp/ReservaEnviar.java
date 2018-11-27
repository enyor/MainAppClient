package co.kaizenpro.mainapp.mainapp;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Map;


public class ReservaEnviar extends Reserva {
    private String hora;

    public ReservaEnviar() {
    }

    public ReservaEnviar(String hora) {
        this.hora = hora;
    }

    public ReservaEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, String clientid, String hora) {
        super(mensaje, nombre, fotoPerfil, type_mensaje, clientid);
        this.hora = hora;
    }

    public ReservaEnviar(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, String clientid, String hora) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje, clientid);
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
