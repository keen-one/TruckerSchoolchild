package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class FieldUpdatesApoderado {

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
  @SerializedName("password")
  @Expose
  private String password = "";
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
    return "FieldUpdatesApoderado{" +
        "latitudInicial='" + latitudInicial + '\'' +
        ", longitudInicial='" + longitudInicial + '\'' +
        ", latitudFinal='" + latitudFinal + '\'' +
        ", longitudFinal='" + longitudFinal + '\'' +
        ", password='" + password + '\'' +
        ", lugar='" + lugar + '\'' +
        '}';
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

  public final String getPassword() {
    return password;
  }

  public final void setPassword(String password) {
    this.password = password;
  }


}
