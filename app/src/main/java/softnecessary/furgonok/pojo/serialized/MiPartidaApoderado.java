package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class MiPartidaApoderado {

  @SerializedName("nombreUsuario")
  @Expose
  private String nombreUsuario = "";
  @SerializedName("latitudInicial")
  @Expose
  private String latitudInicial = "";
  @SerializedName("apodo")
  @Expose
  private String apodo = "";
  @SerializedName("longitudInicial")
  @Expose
  private String longitudInicial = "";
  @SerializedName("latitudFinal")
  @Expose
  private String latitudFinal = "";
  @SerializedName("longitudFinal")
  @Expose
  private String longitudFinal = "";
  @SerializedName("lugar")
  @Expose
  private String lugar = "";

  public final String getLugar() {
    return lugar;
  }

  public final void setLugar(String lugar) {
    this.lugar = lugar;
  }

  @NonNull
  @Override
  public String toString() {
    return "MiPartidaApoderado{" +
        "nombreUsuario='" + nombreUsuario + '\'' +
        ", latitudInicial='" + latitudInicial + '\'' +
        ", apodo='" + apodo + '\'' +
        ", longitudInicial='" + longitudInicial + '\'' +
        ", latitudFinal='" + latitudFinal + '\'' +
        ", longitudFinal='" + longitudFinal + '\'' +
        ", lugar='" + lugar + '\'' +
        '}';
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

  public final String getApodo() {
    return apodo;
  }

  public final void setApodo(String apodo) {
    this.apodo = apodo;
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
}
