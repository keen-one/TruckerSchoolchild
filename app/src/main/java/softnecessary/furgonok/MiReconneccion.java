package softnecessary.furgonok;

import androidx.fragment.app.FragmentActivity;
import org.jivesoftware.smack.ReconnectionListener;
import softnecessary.furgonok.utils.Utilidades;

public class MiReconneccion implements ReconnectionListener {

  private FragmentActivity activity;

  public MiReconneccion(FragmentActivity activity) {
    this.activity = activity;
  }

  @Override
  public void reconnectingIn(int seconds) {
    Utilidades.showMsg(activity, "Reconectando en " + seconds);
  }

  @Override
  public void reconnectionFailed(Exception e) {
    Utilidades.showMsg(activity, "Reconexion fallida");
  }
}
