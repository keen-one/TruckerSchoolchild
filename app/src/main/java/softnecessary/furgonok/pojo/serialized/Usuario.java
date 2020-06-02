package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class Usuario {

  @SerializedName("apodo")
  @Expose
  private String apodo = "";
  @SerializedName("nombreUsuario")
  @Expose
  private String nombreUsuario = "";
  @SerializedName("latitudInicial")
  @Expose
  private String latitudInicial = "";
  @SerializedName("longitudInicial")
  @Expose
  private String longitudInicial = "";
  @SerializedName("latitudFinal")
  @Expose
  private String latitudFinal = "";
  @SerializedName("longitudFinal")
  @Expose
  private String longitudFinal = "";

  @SerializedName("distancia")
  @Expose
  private String distancia = "";
  @SerializedName("lugar")
  @Expose
  private String lugar = "";

  @NonNull
  @Override
  public String toString() {
    return "Usuario{" +
        "apodo='" + apodo + '\'' +
        ", nombreUsuario='" + nombreUsuario + '\'' +
        ", latitudInicial='" + latitudInicial + '\'' +
        ", longitudInicial='" + longitudInicial + '\'' +
        ", latitudFinal='" + latitudFinal + '\'' +
        ", longitudFinal='" + longitudFinal + '\'' +
        ", distancia='" + distancia + '\'' +
        ", lugar='" + lugar + '\'' +
        '}';
  }

  public final String getLugar() {
    return lugar;
  }

  public final void setLugar(String lugar) {
    this.lugar = lugar;
  }

  public final String getApodo() {
    return apodo;
  }

  public final void setApodo(String apodo) {
    this.apodo = apodo;
  }

  public final String getNombreUsuario() {
    return nombreUsuario;
  }

  public final void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public final String getLatitudInicial() {
    return latitudInicial;
  }

  public final void setLatitudInicial(String latitudInicial) {
    this.latitudInicial = latitudInicial;
  }

  public final String getLongitudInicial() {
    return longitudInicial;
  }

  public final void setLongitudInicial(String longitudInicial) {
    this.longitudInicial = longitudInicial;
  }

  public final String getLatitudFinal() {
    return latitudFinal;
  }

  public final void setLatitudFinal(String latitudFinal) {
    this.latitudFinal = latitudFinal;
  }

  public final String getLongitudFinal() {
    return longitudFinal;
  }

  public final void setLongitudFinal(String longitudFinal) {
    this.longitudFinal = longitudFinal;
  }


  public final String getDistancia() {
    return distancia;
  }

  public final void setDistancia(String distancia) {
    this.distancia = distancia;
  }
}
