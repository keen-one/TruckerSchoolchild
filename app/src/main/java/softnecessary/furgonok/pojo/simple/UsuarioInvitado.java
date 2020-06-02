package softnecessary.furgonok.pojo.simple;


import androidx.annotation.NonNull;

public final class UsuarioInvitado {

  private String nombreUsuario = "";


  private String apodo = "";


  public final String getApodo() {
    return apodo;
  }

  public final void setApodo(String apodo) {
    this.apodo = apodo;
    if (this.apodo == null) {
      this.apodo = "";
    }
  }

  @NonNull
  @Override
  public String toString() {
    return "UsuarioInvitado{" +
        "nombreUsuario='" + nombreUsuario + '\'' +
        ", apodo='" + apodo + '\'' +
        '}';
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

}
