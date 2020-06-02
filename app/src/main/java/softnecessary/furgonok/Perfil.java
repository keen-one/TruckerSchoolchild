package softnecessary.furgonok;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.util.List;
import java.util.Locale;
import org.jivesoftware.smack.AbstractXMPPConnection;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.dao.users.ConductorDAO;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.pojo.serialized.Apoderado;
import softnecessary.furgonok.pojo.serialized.Conductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SnackBarUtils;
import softnecessary.furgonok.utils.Utilidades;

public class Perfil extends Fragment {

  private MisPreferencias pref;
  private String correo = "";
  private String password = "";
  private int miId = -1;
  private String authToken = "";

  private String tipoUsuario = "";

  private SnackBarUtils snack = null;

  public Perfil() {
  }
/*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }*/
/*
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    if (item.getItemId() == R.id.launch_loggout) {

      MiAplicacion.salirDeTodo(getActivity(), getActivity());
      return true;
    } else if (item.getItemId() == R.id.launch_admin_descarga) {
      Intent intent = new Intent(getActivity(), DownloadManager.class);
      startActivity(intent);
      return true;
    }
    return false;
  }
*/

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_mi_perfil, container, false);
    AppCompatActivity context = (AppCompatActivity) getActivity();
    if (context != null) {
      ActionBar actionbar = context.getSupportActionBar();
      if (actionbar != null) {
        context.getSupportActionBar().setTitle(getString(R.string.administrador_de_cuenta));
      }
    }

    CardView cardProfile = view.findViewById(R.id.my_cv);
    TextView tvNombreUsuario = view.findViewById(R.id.tv_nombreUsuario);
    OnLongClickListener clickLongProfile = new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (context != null) {
          ClipboardManager clipBoard = (ClipboardManager) context
              .getSystemService(CLIPBOARD_SERVICE);
          String dato = pref.loadNombreUsuario();
          ClipData clipData = ClipData
              .newPlainText("Nombre de usuario copiado al porta papeles", dato);

          if (clipBoard != null) {
            clipBoard.setPrimaryClip(clipData);
            Snackbar
                .make(context.findViewById(android.R.id.content),
                    "Nombre de usuario copiado al porta papeles",
                    Snackbar.LENGTH_LONG).show();
          }

          return true;
        } else {
          return false;
        }

      }
    };
    cardProfile.setOnLongClickListener(clickLongProfile);

    pref = new MisPreferencias(getActivity());
    authToken = pref.loadToken();
    correo = pref.loadCorreoLogin();
    password = pref.loadPasswordLogin();
    miId = pref.loadID();
    tipoUsuario = pref.loadTypeUser();
    snack = SnackBarUtils.getInstance();
    ImageView ivProfile = view.findViewById(R.id.iv_profile);

    Button btnChangePassword = view.findViewById(R.id.btn_cambio_password);
    Button btnModificarPuntoEncuentro = view.findViewById(R.id.btn_cambio_punto_partida);

    Button btnEliminarPerfil = view.findViewById(R.id.btn_eliminacion);
    Button btnRegistroChat = view.findViewById(R.id.btn_registro_chat);
    //Button btnEliminarCuentaChat = view.findViewById(R.id.btn_eliminar_cuenta_chat);
    Button btnLoginChat = view.findViewById(R.id.btn_inicar_cuenta_chat);
    TextView tvLugar = view.findViewById(R.id.tv_lugar);
    OnClickListener clickLogin = new OnClickListener() {
      @Override
      public void onClick(View v) {
        inicializarMensajeria();
      }
    };
    btnLoginChat.setOnClickListener(clickLogin);
    /*OnClickListener clickRemoveAccount = new OnClickListener() {
      @Override
      public void onClick(View v) {
        eliminarCuentaChat();
      }
    };*/
    //btnEliminarCuentaChat.setOnClickListener(clickRemoveAccount);
    TextView tvPuntoPartida = view.findViewById(R.id.tv_puntoPartida);
    TextView tvDestino = view.findViewById(R.id.tv_destino);
    OnClickListener clickRegistroChat = new OnClickListener() {
      @Override
      public void onClick(View v) {
        registrarEnChat();
      }
    };
    btnRegistroChat.setOnClickListener(clickRegistroChat);
    OnClickListener clickChangePass = new OnClickListener() {
      @Override
      public void onClick(View v) {
        mostrarActivityPassword();
      }
    };
    btnChangePassword.setOnClickListener(clickChangePass);
    OnClickListener removeProfile = new OnClickListener() {
      @Override
      public void onClick(View v) {
        mostrarEliminacionPerfil();
      }
    };
    btnEliminarPerfil.setOnClickListener(removeProfile);
    OnClickListener clickPoint = new OnClickListener() {
      @Override
      public void onClick(View v) {
        mostrarActivityPuntoEncuentro();
      }
    };
    btnModificarPuntoEncuentro.setOnClickListener(clickPoint);
    if (tipoUsuario.equals(PantallaInicial.APODERADO)) {
      btnModificarPuntoEncuentro.setVisibility(View.VISIBLE);
    } else {
      btnModificarPuntoEncuentro.setVisibility(View.GONE);
    }
    if (!correo.equals("") && !password.equals("") && miId != -1 && !authToken.equals("")) {
      FragmentActivity activity = getActivity();
      if (activity != null) {
        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            if (tipoUsuario.equals(PantallaInicial.APODERADO)) {
              Runnable runnableUI1 = new Runnable() {
                @Override
                public void run() {
                  tvPuntoPartida.setVisibility(View.VISIBLE);
                  tvDestino.setVisibility(View.VISIBLE);
                  ivProfile.setImageResource(R.mipmap.ic_user_apoderado_circle);
                }
              };
              activity.runOnUiThread(runnableUI1);

              ApoderadoDAO apoderadoDAO = new ApoderadoDAO(getString(R.string.huella),
                  getString(R.string.key_pass));

              Object objApoderado = apoderadoDAO
                  .getApoderadoByCorreo(correo, correo, password, authToken, miId);
              if (objApoderado instanceof Apoderado) {
                String nombreUsuarioApoderado = ((Apoderado) objApoderado).getNombreUsuario();
                double latitudInicial;
                double longitudInicial;
                double latitudFinal;
                double longitudFinal;

                latitudInicial = stringToDouble(((Apoderado) objApoderado).getLatitudInicial());
                longitudInicial = stringToDouble(((Apoderado) objApoderado).getLongitudInicial());
                latitudFinal = stringToDouble(((Apoderado) objApoderado).getLatitudFinal());
                longitudFinal = stringToDouble(((Apoderado) objApoderado).getLongitudFinal());
                String lugar = ((Apoderado) objApoderado).getLugar();
                String puntoPartidaApoderado = getPuntoPartida(latitudInicial, longitudInicial);
                String destino = getPuntoPartida(latitudFinal, longitudFinal);
                Runnable runnable1Ui1 = new Runnable() {
                  @Override
                  public void run() {

                    tvLugar.setVisibility(View.VISIBLE);
                    if (lugar.equals("")) {
                      tvLugar.setText(R.string.estoy_en_desconocido);
                    } else {
                      tvLugar
                          .setText(
                              activity.getString(R.string.estoy_en).concat(lugar.toLowerCase()));
                    }

                    if (destino.equals("")) {
                      tvDestino
                          .setText(R.string.colegio_desconocido);
                    } else {
                      tvDestino
                          .setText(
                              activity.getString(R.string.colegio_dos_puntos).concat(destino));
                    }
                    if (puntoPartidaApoderado.equals("")) {
                      tvPuntoPartida
                          .setText(R.string.casa_desconocido);
                    } else {
                      tvPuntoPartida
                          .setText(activity.getString(R.string.casa_dos_puntos)
                              .concat(puntoPartidaApoderado));
                    }
                    if (nombreUsuarioApoderado.equals("")) {
                      tvNombreUsuario.setText(R.string.nombre_usuario_desconocido);
                    } else {
                      tvNombreUsuario
                          .setText("Nombre de usuario: "
                              .concat(Utilidades.getAsterisco(nombreUsuarioApoderado)));
                    }
                  }
                };
                activity.runOnUiThread(runnable1Ui1);


              } else if (objApoderado instanceof Mensaje) {
                String mensaje = ((Mensaje) objApoderado).getMsg();
                Utilidades.showMsg(activity, mensaje);
              }

            } else if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
              Runnable runnable1Ui1 = new Runnable() {
                @Override
                public void run() {

                  tvLugar.setVisibility(View.GONE);

                  tvPuntoPartida.setVisibility(View.GONE);
                  tvDestino.setVisibility(View.GONE);
                  ivProfile.setImageResource(R.mipmap.ic_user_conductor_circle);
                  String nombreUsuario1 = pref.loadNombreUsuario();
                  if (nombreUsuario1.equals("")) {
                    tvNombreUsuario
                        .setText(
                            R.string.nombre_usuario_desconocido);
                  } else {
                    tvNombreUsuario
                        .setText(
                            "Nombre de usuario: ".concat(Utilidades.getAsterisco(nombreUsuario1)));
                  }

                }
              };
              activity.runOnUiThread(runnable1Ui1);
              /*ConductorDAO conductorDAO = new ConductorDAO(getString(R.string.huella),
                  getString(R.string.key_pass));

              Object objeto1 = conductorDAO
                  .getConductorByCorreo(correo, correo, password, authToken, miId);

              if (objeto1 instanceof Conductor) {
                Runnable runnableUi2 = new Runnable() {
                  @Override
                  public void run() {
                    tvNombreUsuario
                        .setText(
                            "Nombre de usuario: ".concat(((Conductor) objeto1).getNombreUsuario()));
                  }
                };
                activity.runOnUiThread(runnableUi2);


              } else if (objeto1 instanceof Mensaje) {
                String mensaje = ((Mensaje) objeto1).getMsg();
                Utilidades.showMsg(activity, mensaje);
              }*/
            }
          }
        };
        Thread thread = new Thread(runnable);
        if (!thread.isAlive()) {
          thread.start();
        }
      }


    } else {
      String mensaje = "Usuario sin autenticar";
      snack.showSnackBar(getActivity(), mensaje);

    }
    return view;
  }

  /*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_mi_perfil);



    }
  */
  private double stringToDouble(String string) {
    double valor;
    try {
      valor = Double.parseDouble(string);
    } catch (Exception e) {
      valor = 0.0;
    }
    return valor;
  }

  private String getPuntoPartida(double latitud, double longitud) {
    if (latitud == 0.0 && longitud == 0.0) {
      return "";
    }
    Locale local = new Locale("es", "CL");
    Geocoder geo = new Geocoder(getActivity(), local);
    String resultado = "";
    try {
      List<Address> address = geo.getFromLocation(latitud, longitud, 1);
      resultado = address.get(0).getAddressLine(0);
    } catch (Exception ignored) {

    }
    if (resultado == null) {
      resultado = "";
    }
    return resultado;
  }

  private void inicializarMensajeria() {

    String username = pref.loadNombreUsuario();
    String passwordChat = pref.loadPasswordChat();

    if (!username.equals("") && !passwordChat.equals("")) {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          logearEnMensajeria();
          //Utilidades.showMsg(getActivity(),"Autenticado con exito");
        }
      };
      Thread thread = new Thread(runnable);
      if (!thread.isAlive()) {
        thread.start();
      }
    } else {
      FragmentActivity activity = getActivity();
      if (activity != null) {
        snack.showSnackBar(activity, "Usuario sin autenticar");
      }

    }


  }

  @Override
  public void onStart() {
    super.onStart();
    FragmentActivity activity = getActivity();
    if (activity != null) {
      MiAplicacion.runServiceRespaldo(activity);
      MiAplicacion.stopServiceNotification(activity);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onStop() {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      MiAplicacion.runServiceNotificacion(activity);
    }

    super.onStop();
  }

  private void logearEnMensajeria() {
    if (BuildConfig.DEBUG || MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {

      String username = pref.loadNombreUsuario();
      String passwordChat = pref.loadPasswordChat();
      if (!username.equals("") && !passwordChat.equals("")) {
        MyXMPP xmpp = new MyXMPP(getActivity());
        AbstractXMPPConnection connInit = xmpp.init();
        xmpp.setConexionListener(connInit, true);
        AbstractXMPPConnection connConnect = xmpp.conectar(connInit);
        xmpp.logear2(connConnect, username, passwordChat);
      }
    } else {
      Utilidades.showMsg(getActivity(), "Necesita premium");
    }
  }

  private void mostrarActivityPuntoEncuentro() {
    if (!correo.equals("") && !password.equals("") && miId != -1 && !authToken.equals("")) {
      String path = pref.cargarPathMapa();
      File file = new File(path);
      if (file.exists() && path.endsWith(".map")) {
        Intent intent = new Intent(getActivity(), PickerDirection.class);
        startActivity(intent);
      } else {
        Intent intent = new Intent(getActivity(), DownloadManager.class);
        startActivity(intent);
      }

    } else {
      String mensaje = "Usuario sin autenticar";

      snack.showSnackBar(getActivity(), mensaje);


    }
  }

  private void mostrarActivityPassword() {
    if (!correo.equals("") && !password.equals("") && miId != -1 && !authToken.equals("")) {
      Intent intent = new Intent(getActivity(), ChangePassword.class);
      startActivity(intent);
    } else {
      String mensaje = "Usuario sin autenticar";
      snack.showSnackBar(getActivity(), mensaje);
    }
  }

  private void mostrarEliminacionPerfil() {
    if (!correo.equals("") && !password.equals("") && miId != -1 && !authToken.equals("")) {

      AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
      alert.setTitle("Elimina la cuenta actual");

      TextView tvLeyenda = new TextView(getActivity());

      tvLeyenda.setText("¿Esta seguro que desea eliminar el perfil?");
      tvLeyenda.setGravity(Gravity.CENTER);
      tvLeyenda.setPadding(40, 40, 40, 40);
      tvLeyenda.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      LinearLayout.LayoutParams linearParam1 = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      tvLeyenda.setLayoutParams(linearParam1);

      alert.setView(tvLeyenda);
      DialogInterface.OnClickListener clickPos = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Runnable runnable = new Runnable() {
            @Override
            public void run() {
              if (tipoUsuario.equals(PantallaInicial.APODERADO)) {

                ApoderadoDAO dao = new ApoderadoDAO(
                    getString(R.string.huella), getString(R.string.key_pass));
                Object object = dao
                    .deleteByCorreoApoderado(miId, authToken, correo, password);
                if (object instanceof Apoderado) {
                  FragmentActivity activity = getActivity();
                  if (activity != null) {
                    pararServicios(activity);
                    Utilidades.showMsg(activity, "La cuenta a sido borrada exitósamente");

                    SharedPreferences pref1 = pref.getPref();
                    if (pref1 != null) {
                      pref1.edit().clear().apply();
                    }

                    Intent intent = new Intent(activity, PantallaInicial.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Runnable runnableUi = new Runnable() {
                      @Override
                      public void run() {
                        startActivity(intent);
                      }
                    };
                    activity.runOnUiThread(runnableUi);

                  }

                } else if (object instanceof Mensaje) {
                  String mensaje = "Error, "
                      .concat(((Mensaje) object).getMsg());
                  Utilidades.showMsg(getActivity(), mensaje);

                }
              } else if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
                ConductorDAO dao = new ConductorDAO(
                    getString(R.string.huella), getString(R.string.key_pass));
                Object object = dao
                    .deleteByCorreoConductor(miId, authToken, correo, password);
                if (object instanceof Conductor) {
                  FragmentActivity activity = getActivity();
                  if (activity != null) {
                    Utilidades.showMsg(activity, "La cuenta a sido borrada exitósamente");
                    pararServicios(activity);
                    SharedPreferences pref1 = pref.getPref();
                    if (pref1 != null) {
                      pref1.edit().clear().apply();
                    }

                    Intent intent = new Intent(activity, PantallaInicial.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Runnable runnableUi = new Runnable() {
                      @Override
                      public void run() {
                        startActivity(intent);
                      }
                    };
                    activity.runOnUiThread(runnableUi);
                  }


                } else if (object instanceof Mensaje) {
                  String mensaje = "Error, "
                      .concat(((Mensaje) object).getMsg());
                  Utilidades.showMsg(getActivity(), mensaje);
                  //snack.showSnackBar(getActivity(), mensaje);
                }
              }
            }
          };
          Thread thread = new Thread(runnable);
          if (!thread.isAlive()) {
            thread.start();
          }
        }

      };
      alert.setPositiveButton("Borrar mi cuenta actual", clickPos);
      DialogInterface.OnClickListener clickNeg = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }

      };
      alert.setNegativeButton("Cerrar", clickNeg);
      alert.show();


    } else {

      snack.showSnackBar(getActivity(), "Usuario sin autenticar");
    }
  }

  private void pararServicios(FragmentActivity activity) {
    if (activity != null) {
      Intent intent1 = new Intent(activity, ServiceRespaldoChat.class);
      Intent intent2 = new Intent(activity, ServicioNotificacion.class);
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          activity.stopService(intent1);
          activity.stopService(intent2);
        }
      };
      activity.runOnUiThread(runnable);
    }
  }
  /*
  private void eliminarCuentaChat() {

    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    alert.setTitle("Elimina la cuenta de mensajeria");
    TextView tvLeyenda = new TextView(getActivity());
    tvLeyenda.setText("¿Esta seguro que desea eliminar la cuenta de mensajeria?");

    alert.setView(tvLeyenda);
    alert.setPositiveButton("Borrar",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            String user = pref.loadNombreUsuario();
            String pass = pref.loadPasswordChat();
            MyXMPP xmpp = new MyXMPP(getActivity());

            if (!user.equals("") && !pass.equals("")) {
              Runnable runnable = new Runnable() {
                @Override
                public void run() {
                  AbstractXMPPConnection connection1 = xmpp.logear(user, pass);
                  if (connection1 != null) {

                    if (connection1.isAuthenticated()) {
                      AccountManager manager = AccountManager.getInstance(connection1);
                      manager.sensitiveOperationOverInsecureConnection(true);
                      try {
                        manager.deleteAccount();
                      } catch (NoResponseException | XMPPErrorException | NotConnectedException | InterruptedException e) {
                        Utilidades.showMsg(getActivity(), e.getLocalizedMessage());
                        return;
                      }

                      Utilidades.showMsg(getActivity(),
                          "Mensajería: usuario eliminado del servidor con éxito");

                    } else {
                      Utilidades.showMsg(getActivity(), "Mensajería: sin autenticar al servidor");
                    }
                  }
                }
              };
              Thread thread = new Thread(runnable);
              if (!thread.isAlive()) {
                thread.start();
              }
            } else {
              Utilidades
                  .showMsg(getActivity(), "Mensajería: nombre de usuario y/o contraseña inválida");
            }


          }
        });
    DialogInterface.OnClickListener clickNegativo1 = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }

    };
    alert.setNegativeButton("Cerrar", clickNegativo1);
    alert.show();

  }*/

  private void registrarEnChat() {
    if (BuildConfig.DEBUG || MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {

      String username = pref.loadNombreUsuario();
      String passwordChat = pref.loadPasswordChat();
      if (!username.equals("") && !passwordChat.equals("")) {
        MyXMPP xmpp = new MyXMPP(getActivity());
        xmpp.createUser(username, passwordChat);
      } else {
        snack.showSnackBar(getActivity(), "Usuario sin autenticar");
      }
    } else {
      Utilidades.showMsg(getActivity(), "Necesita premium");
    }
  }
}
