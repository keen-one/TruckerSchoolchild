package softnecessary.furgonok;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.Utilidades;

public class ServicioNotificacion extends Service {


  private IncomingChatMessageListener listenerEntrada = new IncomingChatMessageListener() {
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

      String mensaje = message.getBody();

      String sender = from.asEntityBareJidString();
      Utilidades util = new Utilidades();

      Notification notificacion = util
          .shoWNotification(ServicioNotificacion.this, sender.concat(": ").concat(mensaje));

      startForeground(15, notificacion);
    }
  };

  public ServicioNotificacion() {
  }

  private void miBackground() {
    MisPreferencias pref = new MisPreferencias(ServicioNotificacion.this);
    String username = pref.loadNombreUsuario();
    String password = pref.loadPasswordChat();
    if (!password.equals("") && !username.equals("")) {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          if (MiAplicacion.conexionXMPP == null) {
            logearMensajeria();

          }

          if (MiAplicacion.conexionXMPP != null) {
            if (!MiAplicacion.conexionXMPP.isAuthenticated()) {
              logearMensajeria();
            }
            if (MiAplicacion.conexionXMPP.isAuthenticated()) {
              ChatManager chatManager = ChatManager.getInstanceFor(MiAplicacion.conexionXMPP);
              chatManager.addIncomingListener(listenerEntrada);
            }
          }
        }
      };
      Thread thread = new Thread(runnable);
      if (!thread.isAlive()) {
        thread.start();
      }

      /*Utilidades util = new Utilidades();
      Notification notificacion = util
          .shoWNotification(ServicioNotificacion.this, "Servicio observando mensajes recibidos");

      startForeground(15, notificacion);*/
    }

  }

  private void logearMensajeria() {
    MisPreferencias pref = new MisPreferencias(ServicioNotificacion.this);
    String username = pref.loadNombreUsuario();
    String password = pref.loadPasswordChat();
    if (!password.equals("") && !username.equals("")) {
      MyXMPP xmpp = new MyXMPP(null);
      AbstractXMPPConnection connInit = xmpp.init();
      xmpp.setConexionListener(connInit, true);
      AbstractXMPPConnection connConnect = xmpp.conectar(connInit);

      xmpp.logear2(connConnect, username, password);
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    miBackground();

    Utilidades util = new Utilidades();
    Notification notificacion = util
        .shoWNotification(ServicioNotificacion.this, "Servicio observando mensajes recibidos");

    startForeground(15, notificacion);
    return START_REDELIVER_INTENT;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Utilidades util = new Utilidades();
    Notification notificacion = util
        .shoWNotification(ServicioNotificacion.this, "Servicio observando mensajes recibidos");

    startForeground(15, notificacion);
  }


  @Override
  public void onDestroy() {
    stopForeground(true);
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


}
