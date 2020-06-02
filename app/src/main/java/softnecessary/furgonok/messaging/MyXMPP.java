package softnecessary.furgonok.messaging;


import androidx.fragment.app.FragmentActivity;
import java.io.IOException;
import javax.net.ssl.SSLSocketFactory;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.MiConneccion;
import softnecessary.furgonok.utils.Utilidades;

//import org.bouncycastle.openpgp.PGPKeyRing;

public final class MyXMPP {


  public static final String DOMAIN = "im.koderoot.net";//im.koderoot.net
  private FragmentActivity activity;


  public MyXMPP(FragmentActivity activity) {
    this.activity = activity;
  }
  /*
  public void desconectar() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        AbstractXMPPConnection conn = init();
        AbstractXMPPConnection conn1 = conectar(conn);
        if (conn1 == null) {
          return;
        }
        if (conn1.isConnected()) {
          conn1.disconnect();
        }
      }
    };
    Thread thread = new Thread(runnable);
    if(!thread.isAlive()){thread.start();}

  }*/

  //Initialize
  public final AbstractXMPPConnection init() {

    try {
      XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration
          .builder();
      //configBuilder.setUsernameAndPassword(userName, passWord);
      configBuilder.setSecurityMode(SecurityMode.ifpossible);
      //configBuilder.setCompressionEnabled(false);
      configBuilder.setSocketFactory(SSLSocketFactory.getDefault());

      configBuilder.addEnabledSaslMechanism("PLAIN");
      //String resource = Utilidades.getRandomString(16);
      String resource = "Android";

      try {
        configBuilder.setResource(resource);
      } catch (XmppStringprepException ignored) {
        Utilidades.showMsg(activity, "Mensajería: recurso inválido");
      }
      try {

        configBuilder.setXmppDomain(DOMAIN);
      } catch (XmppStringprepException ignored) {
        Utilidades.showMsg(activity, "Mensajería: dominio inválido");
      }
      String HOST = "im.koderoot.net";
      configBuilder.setHost(HOST);

      int PORT = 5223;
      configBuilder.setPort(PORT);
      configBuilder.setSendPresence(true);

      //configBuilder.enableDefaultDebugger();
      XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
      XMPPTCPConnection.setUseStreamManagementDefault(true);
      AbstractXMPPConnection xmPP = new XMPPTCPConnection(configBuilder.build());
      xmPP.setReplyTimeout(10000);
      //setConexionListener(xmPP, false);
      return xmPP;
    } catch (Exception e) {

      Utilidades.showMsg(activity, e.getLocalizedMessage());

      return null;
    }

  }

  public final void logear2(AbstractXMPPConnection conn, String user, String pass) {

    if (conn == null) {
      Utilidades.showMsg(activity, "Mensajería: sin conexión con el servidor");
      return;
    }
    if (user != null && pass != null) {

      if (!user.equals("") && !pass.equals("")) {
        if (conn.isConnected()) {
          try {
            if (!conn.isAuthenticated()) {
              SASLAuthentication.unBlacklistSASLMechanism("PLAIN");

              SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
              //SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
              //SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");

              /*String username = user.contains("@" + MyXMPP.DOMAIN) ? user
                  : user.concat("@").concat(MyXMPP.DOMAIN);*/
              conn.login(user, pass);
            } else {
              Utilidades.showMsg(activity, "Mensajería: cliente autenticado");
            }

          } catch (XMPPException | SmackException | IOException | InterruptedException e) {

            Utilidades.showMsg(activity, e.getLocalizedMessage());
          }
        } else {
          Utilidades.showMsg(activity, "Mensajería: conexión desconectada");
        }
      } else {
        Utilidades.showMsg(activity, "Mensajería: nombre de usuario y/o contraseña inválida");
      }
    } else {
      Utilidades.showMsg(activity, "Nombre de usuario y contraseña inválida");
    }
    if (conn.isAuthenticated() && MiAplicacion.conexionXMPP == null) {
      MiAplicacion.conexionXMPP = conn;
    }
    if (conn.isAuthenticated()) {
      if (!MiAplicacion.conexionXMPP.isAuthenticated()) {
        MiAplicacion.conexionXMPP = conn;
      }
    }
  }

  public AbstractXMPPConnection logear(String user, String pass) {
    AbstractXMPPConnection conn1 = init();
    setConexionListener(conn1, false);
    AbstractXMPPConnection conn = conectar(conn1);

    if (conn == null) {
      Utilidades.showMsg(activity, "Mensajería: conexión desestablecida");
      return null;
    }
    if (user != null && pass != null) {

      if (!user.equals("") && !pass.equals("")) {

        if (conn.isConnected()) {
          try {
            if (!conn.isAuthenticated()) {
              SASLAuthentication.unBlacklistSASLMechanism("PLAIN");

              SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
              //SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
              //SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");

              /*String username = user.contains("@" + MyXMPP.DOMAIN) ? user
                  : user.concat("@").concat(MyXMPP.DOMAIN);*/
              conn.login(user, pass);
            } else {
              Utilidades.showMsg(activity, "Mensajería: conexión autenticada");
            }

          } catch (XMPPException | SmackException | IOException | InterruptedException e) {

            Utilidades.showMsg(activity, e.getLocalizedMessage());

          }
        } else {
          Utilidades.showMsg(activity, "Mensajería: conexión desconectada");
        }


      }
    } else {
      Utilidades.showMsg(activity, "Mensajería: nombre de usuario y/o contraseña inválido");
    }
    if (conn.isAuthenticated() && MiAplicacion.conexionXMPP == null) {
      MiAplicacion.conexionXMPP = conn;
    }
    if (conn.isAuthenticated()) {
      if (!MiAplicacion.conexionXMPP.isAuthenticated()) {
        MiAplicacion.conexionXMPP = conn;
      }
    }
    return conn;
  }

  public final void setConexionListener(AbstractXMPPConnection connection, boolean isReconnect) {
    MiConneccion miConnection;

    miConnection = new MiConneccion(connection, activity, true);

    connection.addConnectionListener(miConnection);


  }


  public final void createUser(String user, String pass) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        AbstractXMPPConnection connection1 = init();
        setConexionListener(connection1, false);
        AbstractXMPPConnection connection2 = conectar(connection1);
        if (connection2 == null) {
          Utilidades.showMsg(activity, "Mensajería: conexión desestablecida");
          return;
        }
        if (connection2.isConnected()) {

          try {

            AccountManager manager = AccountManager.getInstance(connection2);
            manager.sensitiveOperationOverInsecureConnection(true);
            manager.createAccount(Localpart.from(user), pass);
          } catch (Exception e) {
            Utilidades.showMsg(activity, e.getLocalizedMessage());
            return;
          }

          Utilidades.showMsg(activity, "Mensajería: usuario registrado con éxito");


        } else {
          Utilidades.showMsg(activity, "Mensajería: conexión desconectada");
        }
      }
    };
    Thread thread = new Thread(runnable);
    if (!thread.isAlive()) {
      thread.start();
    }


  }

  public final AbstractXMPPConnection conectar(AbstractXMPPConnection connection1) {
    if (connection1 == null) {
      Utilidades.showMsg(activity, "Mensajería: conexión desestablecida");
      return null;
    }
    AbstractXMPPConnection conn = null;
    try {
      //if (!connection1.isConnected()) {

      conn = connection1.connect();
      //}
    } catch (SmackException | IOException | XMPPException | InterruptedException e) {
      Utilidades.showMsg(activity, e.getLocalizedMessage());
    }
    if (conn == null) {

      return connection1;
    } else {
      return conn;
    }
  }


}
