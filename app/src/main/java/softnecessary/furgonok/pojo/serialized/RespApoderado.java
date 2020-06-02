package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class RespApoderado {

  @SerializedName("data")
  @Expose
  private Apoderado data = new Apoderado();
  @SerializedName("state")
  @Expose
  private boolean state = false;

  public final Apoderado getApoderado() {
    return data;
  }


  public final void setData(Apoderado data) {
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

    return "RespApoderado{" +
        "apoderado=" + data +
        ", state=" + state +
        '}';
  }
}

