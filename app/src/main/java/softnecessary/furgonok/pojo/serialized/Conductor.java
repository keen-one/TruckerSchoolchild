package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class Conductor {

  @SerializedName("apodo")
  @Expose
  private String apodo = "";
  @SerializedName("correo")
  @Expose
  private String correo = "";
  @SerializedName("password")
  @Expose
  private String password = "";
  @SerializedName("passwordChat")
  @Expose
  private String passwordChat = "";
  @SerializedName("nombreUsuario")
  @Expose
  private String nombreUsuario = "";
  @SerializedName("fechaNac")
  @Expose
  private String fechaNac = "";
  @SerializedName("auth_token")
  @Expose
  private String authToken = "";
  @SerializedName("id")
  @Expose
  private Integer id = -1;


  @SerializedName("latitud")
  @Expose
  private String latitud = "";
  @SerializedName("longitud")
  @Expose
  private String longitud = "";

  public final String getPasswordChat() {
    return passwordChat;
  }

  public final void setPasswordChat(String passwordChat) {
    this.passwordChat = passwordChat;
  }

  @NonNull
  @Override
  public String toString() {
    if (id == null) {
      id = -1;
    }
    return "Conductor{" +
        "apodo='" + apodo + '\'' +
        ", correo='" + correo + '\'' +
        ", password='" + password + '\'' +
        ", passwordChat='" + passwordChat + '\'' +
        ", nombreUsuario='" + nombreUsuario + '\'' +
        ", fechaNac='" + fechaNac + '\'' +
        ", authToken='" + authToken + '\'' +
        ", id=" + id +
        ", latitud='" + latitud + '\'' +
        ", longitud='" + longitud + '\'' +
        '}';
  }

  public final String getApodo() {
    return apodo;
  }

  public final void setApodo(String apodo) {
    this.apodo = apodo;
  }

  public final String getCorreo() {
    return correo;
  }

  public final void setCorreo(String correo) {
    this.correo = correo;
  }

  public final String getPassword() {
    return password;
  }

  public final void setPassword(String password) {
    this.password = password;
  }

  public final String getNombreUsuario() {
    return nombreUsuario;
  }

  public final void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public final String getFechaNac() {
    return fechaNac;
  }

  public final void setFechaNac(String fechaNac) {
    this.fechaNac = fechaNac;
  }

  public final String getAuthToken() {
    return authToken;
  }

  public final void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public final Integer getId() {
    return id;
  }

  public final void setId(Integer id) {
    this.id = id;
  }

  public final String getLatitud() {
    return latitud;
  }

  public final void setLatitud(String latitud) {
    this.latitud = latitud;
  }

  public final String getLongitud() {
    return longitud;
  }

  public final void setLongitud(String longitud) {
    this.longitud = longitud;
  }
}