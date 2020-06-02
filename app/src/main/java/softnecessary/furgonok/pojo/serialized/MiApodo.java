package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class MiApodo {

  @SerializedName("nombreUsuario")
  @Expose
  private String nombreUsuario = "";
  @SerializedName("apodo")
  @Expose
  private String apodo = "";

  public final String getNombreUsuario() {
    return nombreUsuario;
  }

  public final void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public final String getApodo() {
    return apodo;
  }

  public final void setApodo(String apodo) {
    this.apodo = apodo;
  }


  @NonNull
  @Override
  public String toString() {
    return "MiApodo{" +
        "nombreUsuario='" + nombreUsuario + '\'' +
        ", apodo='" + apodo + '\'' +
        '}';
  }
}
