package softnecessary.furgonok;

import androidx.fragment.app.FragmentActivity;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import softnecessary.furgonok.utils.Utilidades;

public class MiConneccion implements ConnectionListener {

  private AbstractXMPPConnection conn;


  private FragmentActivity activity;
  private boolean isReconnect;

  public MiConneccion(AbstractXMPPConnection conn,
      FragmentActivity activity, boolean isReconnect) {
    this.conn = conn;
    this.isReconnect = isReconnect;
    this.activity = activity;
  }

  @Override
  public void connected(XMPPConnection connection) {
    Utilidades.showMsg(activity, "Conectado al servidor de mensajería");

  }


  @Override
  public void authenticated(XMPPConnection connection, boolean resumed) {

    Utilidades.showMsg(activity, "Autenticado al servidor de mensajería");
    MiAplicacion.banderaAutenticado = true;
    /*
    Presence p = new Presence(Type.available, "En linea", 42, Mode.available);
    try {
      connection.sendStanza(p);
    } catch (NotConnectedException e) {
      Utilidades.showMsg(activity, "Desconectado del servidor de mensajería");
    } catch (InterruptedException e) {
      Utilidades.showMsg(activity, e.getLocalizedMessage());
    }*/
  }

  @Override
  public void connectionClosed() {
    Utilidades.showMsg(activity, "Conexión de mensajería cerrada");
    if (!MiAplicacion.banderaAutenticado) {
      MiAplicacion.banderaReconnect = false;

    }
    if (isReconnect && MiAplicacion.banderaReconnect) {
      reconectar();
    }

    //reconectar();
    MiAplicacion.banderaAutenticado = false;
  }

  private void reconectar() {
    MiAplicacion.reconnectionManager = ReconnectionManager.getInstanceFor(this.conn);
    MiReconneccion miReconneccion = new MiReconneccion(activity);
    MiAplicacion.reconnectionManager.enableAutomaticReconnection();
    ReconnectionManager.setEnabledPerDefault(true);
    MiAplicacion.reconnectionManager.setFixedDelay(5000);
    MiAplicacion.reconnectionManager.addReconnectionListener(miReconneccion);
  }


  @Override
  public void connectionClosedOnError(Exception e) {
    if (!MiAplicacion.banderaAutenticado) {
      MiAplicacion.banderaReconnect = false;
    }
    if (e != null) {
      Utilidades.showMsg(activity, e.getLocalizedMessage());
    }
    if (isReconnect && MiAplicacion.banderaReconnect) {
      reconectar();
    }
    //reconectar();
    MiAplicacion.banderaAutenticado = false;


  }

}
