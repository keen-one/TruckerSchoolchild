package softnecessary.furgonok.pojo.simple;

import androidx.annotation.NonNull;

public final class MensajeChat {

  private String mensaje = "";
  private long timestamp = 0;
  private String tipo = "";
  private String sender = "";

  public final String getMensaje() {
    return mensaje;
  }

  public final void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }


  public final String getTipo() {
    return tipo;
  }

  public final void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public final String getSender() {
    return sender;
  }

  public final void setSender(String sender) {
    this.sender = sender;
  }

  public final long getTimestamp() {
    return timestamp;
  }

  public final void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @NonNull
  @Override
  public String toString() {
    return "MensajeChat{" +
        "mensaje='" + mensaje + '\'' +
        ", timestamp=" + timestamp +
        ", tipo='" + tipo + '\'' +
        ", sender='" + sender + '\'' +
        '}';
  }
}
