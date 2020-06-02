package softnecessary.furgonok;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.Purchase.PurchaseState;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterUtil;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import softnecessary.furgonok.adapters.AdapterRvSolicitudes;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.dao.users.ConductorDAO;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.pojo.simple.UsuarioInvitado;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SnackBarUtils;
import softnecessary.furgonok.utils.Utilidades;

public class AdicionContacto extends AppCompatActivity {

  private static String username2 = "";
  private String tipoUsuario = "";
  private EditText etNombreUsuario;

  private SnackBarUtils snack;

  private Utilidades util = new Utilidades();
  private List<String> lista = new ArrayList<>();
  private EditText etNick;

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
  protected void onStop() {
    MiAplicacion.runServiceNotificacion(this);
    super.onStop();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adicion_contacto);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle("Nuevo amigo");
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }
    ImageView imgBus = findViewById(R.id.imgBus);

    //MyXMPP xmpp = new MyXMPP(this);
    MisPreferencias pref = new MisPreferencias(this);
    snack = SnackBarUtils.getInstance();
    tipoUsuario = pref.loadTypeUser();
    etNombreUsuario = findViewById(R.id.etNombreUsuario);
    etNick = findViewById(R.id.etNick);

    RecyclerView rvInvitados = findViewById(R.id.rv_invitado);

    /*if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
      list = new ArrayList<>();
    } else if (tipoUsuario.equals(PantallaInicial.APODERADO)) {
      list = new ArrayList<>(1);
    }*/
    List<UsuarioInvitado> listaVacia = new ArrayList<>(1);
    AdapterRvSolicitudes adapter;
    if (BuildConfig.DEBUG) {
      adapter = new AdapterRvSolicitudes(MiAplicacion.listUsuarioInvitado, null,
          this, this);
    } else {
      adapter = new AdapterRvSolicitudes(listaVacia, null,
          this, this);
      if (listaVacia.size() <= 0) {
        imgBus.setVisibility(View.VISIBLE);
      }
      if (MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {
        adapter = new AdapterRvSolicitudes(MiAplicacion.listUsuarioInvitado, null,
            this, this);
      }
    }
    if (MiAplicacion.listUsuarioInvitado.size() > 0) {
      imgBus.setVisibility(View.GONE);
    } else {
      imgBus.setVisibility(View.VISIBLE);
    }
    rvInvitados.setLayoutManager(new LinearLayoutManager(this));

    rvInvitados.setHasFixedSize(true);
    rvInvitados.setAdapter(adapter);

    //String username = pref.loadNombreUsuario();
    //String passwordChat = pref.loadPasswordChat();

    //setAddInvitados(adapter, list);
    Button btnAddUsuario = findViewById(R.id.btn_enviarInvitacion);
    OnClickListener clickAddUser = new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (BuildConfig.DEBUG || MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {
          username2 = etNombreUsuario.getText().toString();
          if (username2.trim().equals("")) {
            snack.showSnackBar(AdicionContacto.this, "Error, campo nombre de usuario vacio");
            return;
          }

          if (etNick.getText().toString().trim().equals("")) {
            snack.showSnackBar(AdicionContacto.this, "Error, campo apodo vacio");
            return;
          }

          Runnable runnable = new Runnable() {
            @Override
            public void run() {
              if (tipoUsuario.equals(PantallaInicial.APODERADO)) {

                if (verificaExisteConductor(username2)) {
                /*MiApodo miApodo = util
                    .getApodoByUsername(username2, PantallaInicial.CONDUCTOR, AdicionContacto.this,
                        AdicionContacto.this);
                String apodo = miApodo.getApodo();*/
                  String apodo = etNick.getText().toString().trim();
                  List<String> lista = getAmigos();
                  String username3 = username2.contains("@" + MyXMPP.DOMAIN) ? username2
                      : username2.concat("@").concat(MyXMPP.DOMAIN);
                  for (String nombreUsuario : lista) {
                    if (nombreUsuario.equals("staff@im.koderoot.net")) {
                      continue;
                    }
                    if (!nombreUsuario.equals(username3)) {

                      util.removeActualAmigo(nombreUsuario, AdicionContacto.this);
                      //confirmarEliminarAmigo(nombreUsuario);

                    }
                  }
                  // System.out.println(apodo + " el apodo");
                  if (!apodo.equals("")) {
                    addContactRoster(username2, apodo);
                  } else {
                    SnackBarUtils.getInstance().showSnackBar(AdicionContacto.this,
                        "Error, solo se agregan amigos con apodo");
                  }

                } else {
                  snack.showSnackBar(AdicionContacto.this, "Conductor inexistente");
                }

              } else if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
                if (verificaExisteApoderado(username2)) {

                /*MiApodo miApodo = util
                    .getApodoByUsername(username2, PantallaInicial.APODERADO, AdicionContacto.this,
                        AdicionContacto.this);
                String apodo = miApodo.getApodo();*/
                  String apodo = etNick.getText().toString().trim();

                  if (!apodo.equals("")) {
                    addContactRoster(username2, apodo);
                  } else {
                    SnackBarUtils.getInstance().showSnackBar(AdicionContacto.this,
                        "Error, solo se agregan amigos con apodo");
                  }

                }
              }
            }
          };

          Thread thread = new Thread(runnable);
          if (!thread.isAlive()) {
            thread.start();
          }
        } else {
          Utilidades.showMsg(AdicionContacto.this, "Necesita premium");
        }

      }

    };
    btnAddUsuario.setOnClickListener(clickAddUser);
  }

  /*private void setAddInvitados(
      AdapterRvSolicitudes adapterInvitado) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        AbstractXMPPConnection conn=MiAplicacion.conexionXMPP;
        //AbstractXMPPConnection conn = xmpp.logear(username, passwordChat);
        if (conn.isAuthenticated()) {

          Roster roster = Utilidades.obtenerRoster(conn, AdicionContacto.this);
          Set<RosterEntry> entries = roster.getEntries();
          for (RosterEntry entry : entries) {
            if (entry.isSubscriptionPending()) {
              UsuarioInvitado invitado = new UsuarioInvitado();
              invitado.setNombreUsuario(entry.getJid().asUnescapedString());
              invitado.setApodo(entry.getName());
              if (!lista.contains(invitado)) {
                lista.add(invitado);
              }
            }
          }
          Runnable runUI = new Runnable() {
            @Override
            public void run() {
              adapterInvitado.notifyDataSetChanged();
            }
          };
          runOnUiThread(runUI);
        }


      }
    };
    Thread thread = new Thread(runnable);
    if(!thread.isAlive()){thread.start();}

  }*/

  private List<String> getAmigos() {

    for (Contact contact : MiAplicacion.listContact) {
      String jid = contact.getJid();
      if (!lista.contains(jid)) {
        lista.add(jid);
      }
    }
    //AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
    //if(conn!=null){
    //return lista;
    //}
    /*
    Roster roster = Utilidades.obtenerRoster(conn, AdicionContacto.this);

    Set<RosterEntry> entries = roster.getEntries();
    for (RosterEntry entry : entries) {
      String miJid = entry.getJid().asUnescapedString();
      if (!lista.contains(miJid)) {
        lista.add(miJid);
      }

    }
    */
    return lista;
  }


  private boolean verificaExisteConductor(String username) {

    ConductorDAO dao1 = new ConductorDAO(getString(R.string.huella), getString(R.string.key_pass));
    Mensaje mensaje = dao1.isExistUsername(username, false);
    return mensaje.getState();
  }

  private boolean verificaExisteApoderado(String username) {
    ApoderadoDAO dao1 = new ApoderadoDAO(getString(R.string.huella), getString(R.string.key_pass));
    Mensaje mensaje = dao1.isExistUsername(username, false);
    return mensaje.getState();
  }

  private void addContactRoster(String username, String apodo) {

    String barejid =
        username.contains("@" + MyXMPP.DOMAIN) ? username
            : username.concat("@").concat(MyXMPP.DOMAIN);
    //String myUsername = pref.loadNombreUsuario();
    //String passwordChat = pref.loadPasswordChat();
    AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
    if (conn == null) {
      return;
    }
    //AbstractXMPPConnection conn = xmpp.logear(myUsername, passwordChat);

    Roster roster = Utilidades.obtenerRoster(conn, AdicionContacto.this);
    if (roster != null) {

      BareJid jid = null;
      try {
        jid = JidCreate.bareFrom(barejid);
      } catch (XmppStringprepException e) {
        snack.showSnackBar(AdicionContacto.this, "Nombre de usuario inválido");

      }
      if (jid != null) {

        try {
          roster
              .createItemAndRequestSubscription(jid, apodo, null);
        } catch (NotLoggedInException | NoResponseException | XMPPErrorException | NotConnectedException | InterruptedException e) {
          snack.showSnackBar(AdicionContacto.this, e.getLocalizedMessage());
          return;
        }
        try {
          RosterUtil.preApproveSubscriptionIfRequiredAndPossible(roster, jid);
        } catch (NotLoggedInException | NotConnectedException | InterruptedException e) {
          snack.showSnackBar(AdicionContacto.this, e.getLocalizedMessage());
          return;
        }

        snack.showSnackBar(AdicionContacto.this, "Solicitud de amistad enviada");
        //setAddInvitados(adapter, list);

      } else {
        snack.showSnackBar(AdicionContacto.this, "Nombre de usuario inválido");
      }
    }

  }
}
