package softnecessary.furgonok.pojo.simple;


import androidx.annotation.NonNull;

public final class Contact {


  private String jid = "";
  private String nombreUsuario = "";
  private int resource = 0;

  private String apodo = "";
  private String presencia = "";
  private boolean tieneMensaje = false;

  public Contact() {

  }

  public final boolean isTieneMensaje() {
    return tieneMensaje;
  }

  public final void setTieneMensaje(boolean tieneMensaje) {
    this.tieneMensaje = tieneMensaje;
  }

  public final String getPresencia() {
    return presencia;
  }

  public final void setPresencia(String presencia) {

    this.presencia = presencia;
    if (this.presencia == null) {
      this.presencia = "";
    }
  }

  @NonNull
  @Override
  public String toString() {
    return "Contact{" +
        "jid='" + jid + '\'' +
        ", nombreUsuario='" + nombreUsuario + '\'' +
        ", resource=" + resource +
        ", apodo='" + apodo + '\'' +
        ", presencia='" + presencia + '\'' +
        ", tieneMensaje=" + tieneMensaje +
        '}';
  }


  public final String getApodo() {
    return apodo;

  }

  public final void setApodo(String apodo) {
    this.apodo = apodo;
    if (this.apodo == null) {
      this.apodo = "";
    }
  }

  public final int getResource() {
    return resource;
  }

  public final void setResource(int resource) {
    this.resource = resource;
  }

  public final String getNombreUsuario() {
    return nombreUsuario;
  }

  public final void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
    if (this.nombreUsuario == null) {
      this.nombreUsuario = "";
    }
  }

  public final String getJid() {
    return jid;
  }


  public final void setJid(String jid) {
    this.jid = jid;
    if (this.jid == null) {
      this.jid = "";
    }
  }


}
