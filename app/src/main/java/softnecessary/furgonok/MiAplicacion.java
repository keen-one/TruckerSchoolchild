package softnecessary.furgonok;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.android.billingclient.api.Purchase.PurchaseState;
import java.util.ArrayList;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import softnecessary.furgonok.compras.AppExecutors;
import softnecessary.furgonok.compras.Constants;
import softnecessary.furgonok.compras.billing.BillingClientLifecycle;
import softnecessary.furgonok.compras.data.DataRepository;
import softnecessary.furgonok.compras.data.disk.AppDatabase;
import softnecessary.furgonok.compras.data.disk.LocalDataSource;
import softnecessary.furgonok.compras.data.network.WebDataSource;
import softnecessary.furgonok.compras.data.network.firebase.FakeServerFunctions;
import softnecessary.furgonok.compras.data.network.firebase.ServerFunctions;
import softnecessary.furgonok.compras.data.network.firebase.ServerFunctionsImpl;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.pojo.simple.UsuarioInvitado;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.Utilidades;

public class MiAplicacion extends Application {

  public static AbstractXMPPConnection conexionXMPP = null;

  public static ArrayList<Contact> listContact = new ArrayList<>();
  public static ArrayList<UsuarioInvitado> listUsuarioInvitado = new ArrayList<>();
  public static int miStatePurchase = PurchaseState.UNSPECIFIED_STATE;
  public static ReconnectionManager reconnectionManager;
  public static String miUsername = "";
  public static boolean banderaReconnect = false;
  public static boolean banderaAutenticado = false;
  public static String MI_LOGIN_EMAIL = "";

  private AppExecutors executors = new AppExecutors();

  public static void salirDeTodo(Context context, FragmentActivity activity) {

    MisPreferencias pref = new MisPreferencias(context);
    //pref.guardarCredentialsLogin("", "");

    pref.saveTokenID("", -1);
    pref.saveTypeUser("");

    MiAplicacion.conexionXMPP = null;
    Intent intent = new Intent(context, PantallaInicial.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    Utilidades.showMsg(activity, "Ha salido");
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        if (MiAplicacion.conexionXMPP != null) {
          if (MiAplicacion.conexionXMPP.isAuthenticated()) {

            Presence presence = new Presence(Presence.Type.unavailable);
            presence.setStatus("Desconectado");
            presence.setMode(Mode.dnd);
            try {
              MiAplicacion.conexionXMPP.sendStanza(presence);
            } catch (NotConnectedException | InterruptedException e) {
              Utilidades.showMsg(activity, e.getLocalizedMessage());
            }

            if (MiAplicacion.listContact.size() > 0) {
              MiAplicacion.listContact.clear();
            }
            if (MiAplicacion.listUsuarioInvitado.size() > 0) {
              MiAplicacion.listUsuarioInvitado.clear();
            }
            //pref.guardarPasswordChat("");
            //pref.guardarNombreUsuario("");
          }
        }
      }


    };
    Thread thread = new Thread(runnable);
    if (!thread.isAlive()) {
      thread.start();
    }

    //pref.guardarNombreUsuario("");
    //pref.guardarPasswordChat("");
    context.startActivity(intent);
  }

  public static void runServiceRespaldo(Context context) {
    if (MiAplicacion.banderaAutenticado) {

      MisPreferencias pref = new MisPreferencias(context);
      String nombreUsuario = pref.loadNombreUsuario();
      String passwordChat = pref.loadPasswordChat();
      if (!nombreUsuario.equals("") && !passwordChat.equals("")) {
        Intent intent = new Intent(context, ServiceRespaldoChat.class);
        ContextCompat.startForegroundService(context, intent);
      }
    }

  }

  public static void runServiceNotificacion(Context context) {
    if (MiAplicacion.banderaAutenticado) {

      MisPreferencias pref = new MisPreferencias(context);
      String nombreUsuario = pref.loadNombreUsuario();
      String passwordChat = pref.loadPasswordChat();
      if (!nombreUsuario.equals("") && !passwordChat.equals("")) {
        Intent intent = new Intent(context, ServicioNotificacion.class);
        ContextCompat.startForegroundService(context, intent);
      }
    }

  }

  public static void stopServiceNotification(Context context) {

    MisPreferencias pref = new MisPreferencias(context);
    String nombreUsuario = pref.loadNombreUsuario();
    String passwordChat = pref.loadPasswordChat();
    if (!nombreUsuario.equals("") && !passwordChat.equals("")) {
      Intent intent = new Intent(context, ServicioNotificacion.class);
      context.stopService(intent);
    }


  }


  @Override
  public void onCreate() {
    super.onCreate();

  }

  private AppDatabase getDatabase() {
    return AppDatabase.getInstance(this);
  }

  private LocalDataSource getLocalDataSource() {
    return LocalDataSource.getInstance(executors, getDatabase());
  }

  private ServerFunctions getServerFunctions() {
    if (Constants.USE_FAKE_SERVER) {
      return FakeServerFunctions.getInstance();
    } else {
      return ServerFunctionsImpl.getInstance();
    }
  }

  private WebDataSource getWebDataSource() {
    return WebDataSource.getInstance(executors, getServerFunctions());
  }

  public final BillingClientLifecycle getBillingClientLifecycle() {
    return BillingClientLifecycle.getInstance(this);
  }

  public final DataRepository getRepository() {
    return DataRepository
        .getInstance(getLocalDataSource(), getWebDataSource(), getBillingClientLifecycle());
  }
}
