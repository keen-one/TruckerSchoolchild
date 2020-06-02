package softnecessary.furgonok;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.ChatView.OnSentMessageListener;
import co.intentservice.chatui.models.ChatMessage;
import co.intentservice.chatui.models.ChatMessage.Type;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import softnecessary.furgonok.dao.users.RespaldoDAO;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.pojo.simple.MensajeChat;
import softnecessary.furgonok.utils.Utilidades;


public class ChatActivity extends AppCompatActivity {

  private static final int CODE_STORAGE = 15;
  private ChatView mChatView;
  private String contactJid = "";
  private Snackbar snackStorage;
  private IncomingChatMessageListener incomingListener = new IncomingChatMessageListener() {
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

      String sender = from.asEntityBareJidString();
      String mensaje = message.getBody();
      Type tipo = Type.RECEIVED;
      Locale locale = new Locale("es", "CL");
      Calendar c = Calendar.getInstance(locale);
      long timestamp = c.getTimeInMillis();
      ChatMessage chatMessage = new ChatMessage(mensaje, timestamp, tipo, sender);
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          mChatView.addMessage(chatMessage);
        }
      };
      runOnUiThread(runnable);

    }
  };
  private OnSentMessageListener listenerOnSentMsg = new OnSentMessageListener() {
    @Override
    public boolean sendMessage(ChatMessage chatMessage) {

      String message = chatMessage.getMessage();
      if (message != null) {
        if (!message.trim().equals("")) {
          String[] array = getResources().getStringArray(R.array.arrayGroserias);

          String censurado = "#!@f*";
          for (String palabra : array) {

            message = message.replaceAll(" " + palabra + " ", " " + censurado + " ");
          }

          AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
          if (conn != null) {
            ChatManager manager = ChatManager.getInstanceFor(conn);
            EntityBareJid jid = null;
            try {
              jid = JidCreate.entityBareFrom(contactJid);
            } catch (XmppStringprepException ignored) {
              Utilidades.showMsg(ChatActivity.this, "Nombre usuario inválido");
            }
            if (jid != null) {

              Chat chat = manager.chatWith(jid);
              try {
                chat.send(message);
              } catch (NotConnectedException | InterruptedException e) {
                Utilidades.showMsg(ChatActivity.this, e.getLocalizedMessage());
              }

              return true;
            }
            return false;
          } else {
            return false;
          }
        }
      }
      return false;
    }
  };

  @Override
  protected void onStart() {
    super.onStart();
    MiAplicacion.runServiceRespaldo(this);
    MiAplicacion.stopServiceNotification(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    if (item.getItemId() == R.id.launch_loggout) {

      MiAplicacion.salirDeTodo(this, this);
      return true;
    } else if (item.getItemId() == R.id.launch_admin_descarga) {
      Intent intent = new Intent(this, DownloadManager.class);
      startActivity(intent);
      return true;
    }
    return false;
  }


  @Override
  protected void onResume() {
    super.onResume();

  }

  @Override
  protected void onStop() {
    MiAplicacion.runServiceNotificacion(this);
    super.onStop();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == CODE_STORAGE) {
      for (int granted : grantResults) {

        if (granted == PackageManager.PERMISSION_GRANTED) {
          recreate();
          break;
        } else {
          if (!snackStorage.isShown()) {
            snackStorage.show();
          }
        }
      }
    }
  }

  private ArrayList<ChatMessage> getMensajes(String sender) {

    RespaldoDAO dao = new RespaldoDAO(ChatActivity.this);
    List<MensajeChat> lista = dao.consultar(sender);
    ArrayList<ChatMessage> arrayList = new ArrayList<>();

    for (MensajeChat msgChat : lista) {
      Type tipo = msgChat.getTipo().equals("RECEIVE") ? Type.RECEIVED : Type.SENT;
      ChatMessage chatMessage = new ChatMessage(msgChat.getMensaje(),
          msgChat.getTimestamp(), tipo, msgChat.getSender());
      if (!arrayList.contains(chatMessage)) {
        arrayList.add(chatMessage);
      }

    }
    return arrayList;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }

    //Toolbar toolbar=findViewById(R.id.toolbar);
    //toolbar.setTitle("Menu " + tipoUser.toLowerCase());
    OnClickListener clickSnackStorage = new OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{
            permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE}, CODE_STORAGE);
      }
    };
    snackStorage = Snackbar.make(findViewById(android.R.id.content), "Permiso de almacenamiento",
        Snackbar.LENGTH_INDEFINITE).setAction(
        "PERMITIR", clickSnackStorage);
    Intent intent = getIntent();
    Gson gson = new Gson();

    String json = intent.getStringExtra("DATA");
    Contact contact = gson.fromJson(json, Contact.class);
    if (contact != null) {
      contactJid = contact.getJid();
      String nombreUsuario = contact.getNombreUsuario();
      if (!nombreUsuario.equals("")) {
        String nombreNuevo = Utilidades.getAsterisco(nombreUsuario);
        String apodo = contact.getApodo();
        if (!apodo.equals("")) {
          toolbar.setTitle(nombreNuevo.concat(" " + apodo));
        }
      }
    }

    mChatView = findViewById(R.id.my_chat_view);
    //carga los mensajes
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        RespaldoDAO dao = new RespaldoDAO(ChatActivity.this);
        dao.borrarMas250Filas(contactJid);
        ArrayList<ChatMessage> miArray = getMensajes(contactJid);
        Runnable runnableUI = new Runnable() {
          @Override
          public void run() {
            mChatView.addMessages(miArray);
          }
        };
        runOnUiThread(runnableUI);
      }
    };
    Thread thread = new Thread(runnable);
    if (!thread.isAlive()) {
      thread.start();
    }
    mChatView.setOnSentMessageListener(listenerOnSentMsg);
    AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
    if (conn != null) {
      ChatManager manager = ChatManager.getInstanceFor(conn);
      manager.addIncomingListener(incomingListener);
    }
  }


  @Override
  protected void onPause() {
    super.onPause();

  }
}
