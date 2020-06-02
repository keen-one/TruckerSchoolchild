package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public final class RespUsernameApodo {

  @SerializedName("state")
  @Expose
  boolean state = false;
  @SerializedName("data")
  @Expose
  private MiApodo data = new MiApodo();

  public final MiApodo getData() {
    return data;
  }

  public final void setData(MiApodo data) {
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

    return "RespUsernameApodo{" +
        "data=" + data +
        ", state=" + state +
        '}';
  }
}

