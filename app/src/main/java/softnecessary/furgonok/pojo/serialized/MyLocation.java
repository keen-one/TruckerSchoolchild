package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class MyLocation {

  @SerializedName("nombreUsuario")
  @Expose
  private String nombreUsuario = "";
  @SerializedName("latitud")
  @Expose
  private String latitud = "";
  @SerializedName("longitud")
  @Expose
  private String longitud = "";

  public final String getNombreUsuario() {
    return nombreUsuario;
  }

  public final void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
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


  @NonNull
  @Override
  public String toString() {
    return "MyLocation{" +
        "nombreUsuario='" + nombreUsuario + '\'' +
        ", latitud='" + latitud + '\'' +
        ", longitud='" + longitud + '\'' +
        '}';
  }
}
