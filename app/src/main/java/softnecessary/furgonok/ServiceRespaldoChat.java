package softnecessary.furgonok;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import java.util.Calendar;
import java.util.Locale;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import softnecessary.furgonok.dao.users.RespaldoDAO;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.pojo.simple.MensajeChat;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.Utilidades;


public class ServiceRespaldoChat extends Service {

  private RespaldoDAO dao;
  private IncomingChatMessageListener listenerEntrada = new IncomingChatMessageListener() {
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

      String mensaje = message.getBody();
      Locale locale = new Locale("es", "CL");
      Calendar c = Calendar.getInstance(locale);
      long miTiempo = c.getTimeInMillis();
      String sender = from.asEntityBareJidString();
      String tipo = "RECEIVE";
      MensajeChat msgChat = new MensajeChat();
      msgChat.setSender(sender);
      msgChat.setTimestamp(miTiempo);
      msgChat.setMensaje(mensaje);
      msgChat.setTipo(tipo);

      if (sender != null) {

        dao.ingresar(msgChat);
      }

    }
  };
  private OutgoingChatMessageListener listenerSalida = new OutgoingChatMessageListener() {
    @Override
    public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
      String sender = to.asEntityBareJidString();
      String mensaje = message.getBody();
      Locale locale = new Locale("es", "CL");
      Calendar c = Calendar.getInstance(locale);
      long timestamp = c.getTimeInMillis();
      MensajeChat mensajeChat = new MensajeChat();
      mensajeChat.setTimestamp(timestamp);
      mensajeChat.setSender(sender);
      mensajeChat.setMensaje(mensaje);
      String tipo = "SEND";
      mensajeChat.setTipo(tipo);
      dao.ingresar(mensajeChat);
    }
  };


  public ServiceRespaldoChat() {

    this.dao = new RespaldoDAO(this);
  }


  @Override
  public void onCreate() {
    super.onCreate();
    Utilidades util = new Utilidades();
    Notification notificacion = util
        .shoWNotification(this, "Servicio con respaldo de mensajes");

    startForeground(12, notificacion);
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

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    MisPreferencias pref = new MisPreferencias(ServiceRespaldoChat.this);
    String username = pref.loadNombreUsuario();
    String password = pref.loadPasswordChat();
    if (!password.equals("") && !username.equals("")) {
      background();
    }

    Utilidades util = new Utilidades();
    Notification notificacion = util
        .shoWNotification(this, "Servicio con respaldo de mensajes");

    startForeground(12, notificacion);
    return START_REDELIVER_INTENT;
  }

  private void background() {
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
            chatManager.addOutgoingListener(listenerSalida);
          }
        }
      }
    };
    Thread thread = new Thread(runnable);
    if (!thread.isAlive()) {
      thread.start();
    }
  }

  private void logearMensajeria() {
    MisPreferencias pref = new MisPreferencias(ServiceRespaldoChat.this);
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
}
