package com.perfulandia.UsuariosService.dto;

public class MensajeRespuesta {
    private String mensaje;

    public MensajeRespuesta() {}

    public MensajeRespuesta(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
