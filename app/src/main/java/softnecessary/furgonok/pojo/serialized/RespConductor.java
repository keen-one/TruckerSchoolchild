package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class RespConductor {

  @SerializedName("data")
  @Expose
  private Conductor data = new Conductor();
  @SerializedName("state")
  private boolean state = false;

  public final Conductor getConductor() {
    return data;
  }


  public final void setData(Conductor data) {
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

    return "RespConductor{" +
        "data=" + data +
        ", state=" + state +
        '}';
  }
}

