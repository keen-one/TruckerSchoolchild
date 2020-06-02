package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class RespPuntoPartidaApoderado {

  @SerializedName("data")
  @Expose
  private MiPartidaApoderado data = new MiPartidaApoderado();
  @SerializedName("state")
  @Expose
  private boolean state = false;

  public final MiPartidaApoderado getData() {
    return data;
  }

  public final void setData(MiPartidaApoderado data) {
    this.data = data;
  }

  public final boolean isState() {
    return state;
  }

  public final void setState(boolean state) {
    this.state = state;
  }

  @NonNull
  @Override
  public String toString() {

    return "RespPuntoPartidaApoderado{" +
        "data=" + data +
        ", state=" + state +
        '}';
  }
}

