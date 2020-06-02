package softnecessary.furgonok.pojo.serialized;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import softnecessary.furgonok.managedb.ApiClient;
import softnecessary.furgonok.utils.Utilidades;


public final class Mensaje {

  @SerializedName("msg")
  @Expose
  private String msg = "";
  @SerializedName("state")
  @Expose
  private Boolean state = false;

  public final String getMsg() {
    return msg;
  }

  public final void setMsg(String msg) {
    if (msg != null) {

      String msgSinIp = msg.replaceAll(ApiClient.miIp, "server");
      this.msg = Utilidades.replaceIpWithServer(msgSinIp);
    } else {
      this.msg = "";
    }
  }


  public final Boolean getState() {
    return state;
  }

  public final void setState(Boolean state) {
    this.state = state;
  }


  @NonNull
  @Override
  public String toString() {
    return "Mensaje{" +
        "msg='" + msg + '\'' +
        ", state=" + state +
        '}';
  }
}
