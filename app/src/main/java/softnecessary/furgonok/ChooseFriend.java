package softnecessary.furgonok;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import softnecessary.furgonok.adapters.AdapterUsuario;
import softnecessary.furgonok.adapters.AdapterUsuario.ItemListener;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.pojo.serialized.MiPartidaApoderado;
import softnecessary.furgonok.pojo.serialized.Usuario;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.Utilidades;

public class ChooseFriend extends AppCompatActivity {

  private MisPreferencias pref;
  private Gson gson = new Gson();
  private List<Usuario> arrayList = new ArrayList<>();
  private AdapterUsuario adapter;
  private Utilidades util = new Utilidades();

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
  protected void onStart() {
    super.onStart();
    MiAplicacion.runServiceRespaldo(this);
    MiAplicacion.stopServiceNotification(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_friend);
    RecyclerView rvChooseFriend = findViewById(R.id.mi_rv_choose_friend);
    pref = new MisPreferencias(ChooseFriend.this);
    //SnackBarUtils snack = SnackBarUtils.getInstance();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }

    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle("Elige un amigo");
    ItemListener listener = new ItemListener() {
      @Override
      public void onItemClick(Usuario item) {
        String json = gson.toJson(item, Usuario.class);
        Intent intent = new Intent(ChooseFriend.this, MainActivity.class);

        intent.putExtra("DATA", json);

        startActivity(intent);
        //intent.putExtra(, );
      }
    };
    if (BuildConfig.DEBUG) {
      adapter = new AdapterUsuario(arrayList, listener);
    } else {
      if (MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {
        adapter = new AdapterUsuario(arrayList, listener);
      } else {
        List<Usuario> arrayVacio = new ArrayList<>(1);
        adapter = new AdapterUsuario(arrayVacio, null);
      }
    }

    LinearLayoutManager linear = new LinearLayoutManager(this);
    rvChooseFriend.setLayoutManager(linear);
    rvChooseFriend.setHasFixedSize(true);
    rvChooseFriend.setAdapter(adapter);
    //String tipoUser = pref.loadTypeUser();
    //ArrayList<String> listUsername = getAmigos();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        for (Contact contact : MiAplicacion.listContact) {
          String username = contact.getNombreUsuario();
          if (!contact.getNombreUsuario().equals("")) {

            MiPartidaApoderado miPuntoPartida = getMiPuntoPartidaApod(username);
            if (!miPuntoPartida.getNombreUsuario().equals("")) {
              Usuario usuario = new Usuario();
              usuario.setApodo(miPuntoPartida.getApodo());
              String miNombreUsuario = miPuntoPartida.getNombreUsuario();
              usuario.setNombreUsuario(miNombreUsuario);
              String lugar = miPuntoPartida.getLugar();
              String latitudInicial = miPuntoPartida.getLatitudInicial();
              String latitudFinal = miPuntoPartida.getLatitudFinal();
              String longitudInicial = miPuntoPartida.getLongitudInicial();
              String longitudFinal = miPuntoPartida.getLongitudFinal();
              String longitudActual = pref.cargarLongitud();
              String latitudActual = pref.cargarLatitud();
              double miLongitud = util.stringToDouble(longitudActual, 0.0);
              double miLatitud = util.stringToDouble(latitudActual, 0.0);
              double latitudInicialVal = util
                  .stringToDouble(longitudActual, PantallaInicial.LATITUD_DEFAULT);
              double latitudFinalVal = util
                  .stringToDouble(longitudActual, PantallaInicial.LATITUD_FINAL);
              double longitudInicialVal = util
                  .stringToDouble(longitudActual, PantallaInicial.LONGITUD_DEFAULT);
              double longitudFinalVal = util
                  .stringToDouble(longitudActual, PantallaInicial.LONGITUD_FINAL);
              if (lugar.equals(PantallaInicial.PUNTO_PARTIDA)) {
                double distancia = util
                    .distance(miLatitud, miLongitud, latitudInicialVal, longitudInicialVal);
                usuario.setDistancia(String.valueOf(distancia));
              } else if (lugar.equals(PantallaInicial.DESTINO)) {
                double distancia = util
                    .distance(miLatitud, miLongitud, latitudFinalVal, longitudFinalVal);
                usuario.setDistancia(String.valueOf(distancia));
              }
              usuario.setLugar(lugar.toLowerCase());
              usuario.setLatitudInicial(latitudInicial);
              usuario.setLongitudInicial(longitudInicial);
              usuario.setLatitudFinal(latitudFinal);
              usuario.setLongitudFinal(longitudFinal);

              if (!arrayList.contains(usuario)) {
                if (util.findUsuario(miNombreUsuario, arrayList)) {
                  arrayList.add(usuario);
                }

              }

            }
          }

        }
        Runnable runnable2 = new Runnable() {
          @Override
          public void run() {
            Collections.sort(arrayList, new Comparator<Usuario>() {
              @Override
              public int compare(Usuario o1, Usuario o2) {
                String distancia1 = o1.getDistancia();
                String distancia2 = o2.getDistancia();
                double distanciaVal1 = util.stringToDouble(distancia1, 0.0);
                double distanciaVal2 = util.stringToDouble(distancia2, 0.0);
                return Double.compare(distanciaVal1, distanciaVal2);
              }
            });
            adapter.notifyDataSetChanged();
          }
        };
        runOnUiThread(runnable2);
      }
    };
    Thread thread = new Thread(runnable);
    if (!thread.isAlive()) {
      thread.start();
    }


  }

  private MiPartidaApoderado getMiPuntoPartidaApod(String username) {
    ApoderadoDAO dao = new ApoderadoDAO(getString(R.string.huella), getString(R.string.key_pass));

    int miID = pref.loadID();
    String token = pref.loadToken();
    Object object = dao.getPuntoPartidaFinalByUsername(miID, token, username);
    if (object instanceof MiPartidaApoderado) {
      return (MiPartidaApoderado) object;
    } else {
      return new MiPartidaApoderado();
    }
  }

  /*private ArrayList<Contact> getAmigos() {
    //ArrayList<String> listaAmigos = new ArrayList<>();


      for(Contact contacto:MiAplicacion.listContact){
        MiApodo miApodo = util
            .getApodoByUsername(contacto.getJid().substring(0,contacto.getJid().indexOf("@")), PantallaInicial.CONDUCTOR, ChooseFriend.this,
                ChooseFriend.this);
        String apodo = miApodo.getApodo();
        if (!apodo.equals("")) {
          contacto.setApodo(apodo);
        }
        break;
      }
      //String username = pref.loadNombreUsuario();
      //String passwordChat = pref.loadPasswordChat();

      //if (!username.equals("") && !passwordChat.equals("")) {


        //AbstractXMPPConnection conn=MiAplicacion.conexionXMPP;
        //AbstractXMPPConnection conn = xmpp.logear(username, passwordChat);
        /*if (conn == null) {
          return MiAplicacion.listContact;
        }*/
        /*if (conn.isAuthenticated()) {
          Roster roster = Utilidades.obtenerRoster(conn, ChooseFriend.this);

          Set<RosterEntry> entries = roster.getEntries();
          Utilidades util = new Utilidades();
          for (RosterEntry entry : entries) {
            BareJid barejid = entry.getJid();
            Localpart localPart = barejid.getLocalpartOrNull();
            if (localPart != null) {
              String localParte = localPart.asUnescapedString();

              MiApodo miApodo = util
                  .getApodoByUsername(localParte, PantallaInicial.CONDUCTOR, ChooseFriend.this,
                      ChooseFriend.this);
              String apodo = miApodo.getApodo();
              if (!apodo.equals("")) {
                if (!listaAmigos.contains(apodo)) {
                  listaAmigos.add(0, apodo);
                }
              }
              break;
            }
          }
        }
*/

  //}
  //} else if (tipoUser.equals(PantallaInicial.CONDUCTOR)) {
      /*String username = pref.loadNombreUsuario();
      String passwordChat = pref.loadPasswordChat();

      if (!username.equals("") && !passwordChat.equals("")) {*/

  // return MiAplicacion.listContact;
  //}
}
