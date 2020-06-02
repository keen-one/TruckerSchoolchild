package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class FieldUpdatesConductor {

  @SerializedName("latitud")
  @Expose
  private String latitud = "";
  @SerializedName("longitud")
  @Expose
  private String longitud = "";
  @SerializedName("password")
  @Expose
  private String password = "";


  @NonNull
  @Override
  public String toString() {
    return "FieldUpdatesConductor{" +
        "latitud='" + latitud + '\'' +
        ", longitud='" + longitud + '\'' +
        ", password='" + password + '\'' +
        '}';
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

  public final String getPassword() {
    return password;
  }

  public final void setPassword(String password) {
    this.password = password;
  }
}
