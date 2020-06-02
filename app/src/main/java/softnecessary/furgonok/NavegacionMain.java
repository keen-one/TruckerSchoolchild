package softnecessary.furgonok;


import android.Manifest;
import android.Manifest.permission;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController.Visibility;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Marker.OnMarkerClickListener;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.dao.users.ConductorDAO;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesConductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.pojo.serialized.MiPartidaApoderado;
import softnecessary.furgonok.pojo.serialized.MyLocation;
import softnecessary.furgonok.pojo.serialized.Usuario;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SnackBarUtils;
import softnecessary.furgonok.utils.Utilidades;


public class NavegacionMain extends Fragment implements LocationListener {

  private static final int CODE_LOCATION = 13;
  private static final int CODE_STORAGE = 14;
  private final String[] permisoAlmacenamiento = new String[]{permission.READ_EXTERNAL_STORAGE};
  private final String[] permisoLocation = new String[]{permission.ACCESS_FINE_LOCATION};
  private Snackbar snackLocation;
  private Snackbar snackStorage;
  private SnackBarUtils snackBarra;
  private MisPreferencias pref;
  private MapView mapView;
  private Gson gson = new Gson();
  private Polyline mRoadOverlays = null;
  private FolderOverlay mRoadNodeMarkers;
  private Road mRoads;
  private LocationManager locationManager;
  private GeoPoint pointInicial = null;
  private GeoPoint pointFinal = null;
  private Marker marcador1 = null;
  private Marker marcador2 = null;
  private String lugar = "";
  private String apodo = "";
  private Usuario usuario = new Usuario();
  private String tipo = "";
  private ToggleButton btnCentrar;

  private FragmentActivity activity;
  private Snackbar bar;
  private Marker.OnMarkerClickListener listenerMarcador = new OnMarkerClickListener() {
    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
      if (tipo.equals(PantallaInicial.CONDUCTOR)) {
        pointFinal = marker.getPosition();

        if (pointInicial != null && pointFinal != null && !apodo.equals("")) {
          ArrayList<GeoPoint> arrays = new ArrayList<>();
          if (!arrays.contains(pointInicial)) {
            arrays.add(0, pointInicial);
          }
          if (!arrays.contains(pointFinal)) {
            arrays.add(1, pointFinal);
          }

          crearCamino(arrays);
        }

        //crearCamino();

      }
      return true;
    }
  };
  private ToggleButton btnCompartirUbicacion;
  private Handler handler = new Handler();
  private Runnable miRunnable = new Runnable() {
    @Override
    public void run() {
      try {

        ArrayList<Contact> lista = getAmigos();
        if (lista.size() >= 1) {
          String username = lista.get(0).getNombreUsuario();
          if (!username.trim().equals("")) {

            MyLocation cond = getLocalizacionCondByUsername(username);

            String latitud1 = cond.getLatitud();
            String longitud1 = cond.getLongitud();
            Utilidades util = new Utilidades();
            double latitud = util.stringToDouble(latitud1, 0.0);
            double longitud = util.stringToDouble(longitud1, 0.0);
            FragmentActivity activity = getActivity();
            if (BuildConfig.DEBUG) {
              latitud = -33.449597;
              longitud = -70.661363;
            }
            if (latitud != 0.0 && longitud != 0.0) {
              if (bar != null) {
                if (bar.isShown()) {
                  bar.dismiss();
                }
              }

              String latitudInicial = usuario.getLatitudInicial();
              String longitudInicial = usuario.getLongitudInicial();
              String latitudFinal = usuario.getLatitudFinal();
              String longitudFinal = usuario.getLongitudFinal();
              double latIni = util.stringToDouble(latitudInicial, 0.0);
              double lonIni = util.stringToDouble(longitudInicial, 0.0);
              double latFinal = util.stringToDouble(latitudFinal, 0.0);
              double lonFinal = util.stringToDouble(longitudFinal, 0.0);
              if (latIni != 0.0 && lonIni != 0.0) {
                double distancia = util.distance(latitud, longitud, latIni, lonIni);

                if (distancia <= 25.0) {
                  if (activity != null) {
                    String partida = PantallaInicial.PUNTO_PARTIDA;
                    Notification notificacion = util.shoWNotification(activity,
                        "El conductor esta aproxidamente 25 km de " + partida.toLowerCase());
                    NotificationManager manNoti = (NotificationManager) activity
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manNoti != null) {
                      manNoti.notify(90, notificacion);
                    }
                  }

                }
              }
              if (latFinal != 0.0 && lonFinal != 0.0) {
                double distancia = util.distance(latitud, longitud, latFinal, lonFinal);

                if (distancia <= 25.0) {
                  if (activity != null) {
                    String destino = PantallaInicial.DESTINO;
                    Notification notificacion = util.shoWNotification(activity,
                        "El conductor esta aproxidamente 25 km del " + destino.toLowerCase());
                    NotificationManager manNoti = (NotificationManager) activity
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manNoti != null) {
                      manNoti.notify(90, notificacion);
                    }
                  }

                }
              }
              Marker marker = new Marker(mapView);
              Drawable icon = null;
              if (activity != null) {
                icon = ContextCompat
                    .getDrawable(activity, R.drawable.ic_bus_round);
              }

              marker.setIcon(icon);
              GeoPoint geoPoint = new GeoPoint(latitud,
                  longitud);
              marker.setPosition(geoPoint);
              if (mapView.getOverlays().size() >= 1) {
                mapView.getOverlays().set(0, marker);
              } else {
                mapView.getOverlays().add(0, marker);
              }

              mapView.getController().setCenter(geoPoint);
            } else {
              if (bar != null) {
                if (!bar.isShown()) {
                  bar.setText("Ubicación desconocida del conductor");
                  bar.show();
                }
              }
            }
          }
        } else {
          if (bar != null) {
            if (!bar.isShown()) {
              bar.setText("Sin amigo conductor");
              bar.show();
            }
          }
        }
      } catch (Exception ignored) {
      } finally {
        handler.postDelayed(miRunnable, 1000L * 60L * 2L);
      }


    }
  };

  public NavegacionMain() {
  }

  @Override
  public void onStop() {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      MiAplicacion.runServiceNotificacion(activity);
    }

    super.onStop();
  }

  private void centrarOrigenDestino() {

    if (pointInicial != null && pointFinal != null) {

      if (btnCentrar.isChecked()) {

        centrar(pointInicial, false, 0);
      } else {

        centrar(pointFinal, false, 0);

      }


    }
  }

  private void showChooseContact() {
    Intent intent = new Intent(getActivity(), ChooseFriend.class);

    startActivity(intent);
  }

  /*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }*/

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


  private String getPathXml() {
    FragmentActivity activity = getActivity();
    File f = null;
    if (activity != null) {

      f = new File(activity.getExternalCacheDir(), "detailed.xml");
      if (f.exists()) {
        return f.getPath();
      } else {

        try {

          InputStream is = getActivity().getAssets().open("detailed.xml");
          int size = is.available();
          byte[] buffer = new byte[size];
          is.close();

          FileOutputStream fos = new FileOutputStream(f);
          fos.write(buffer);
          fos.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

    }
    return f == null ? "" : f.getPath();
  }

  private void showRutaOrigenDestino() {
    if (usuario != null) {
      if (!usuario.getApodo().equals("")) {
        establecerCaminos(usuario);
      }
    }


  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_navigation, container, false);
    AppCompatActivity context = (AppCompatActivity) getActivity();
    if (context != null) {
      ActionBar actionbar = context.getSupportActionBar();
      if (actionbar != null) {
        context.getSupportActionBar().setTitle(getString(R.string.navegaci_n));
      }
    }
    activity = getActivity();
    View miVista = null;
    if (activity != null) {
      miVista = activity.findViewById(android.R.id.content);
      OnClickListener clickBar = new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (bar != null) {
            if (bar.isShown()) {
              bar.dismiss();
            }
          }
        }
      };
      bar = Snackbar.make(miVista, "Ubicación desconocida del conductor",
          BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("CERRAR", clickBar);
    }

    pref = new MisPreferencias(activity);
    Button btnChooseContacto = view.findViewById(R.id.btnChooseContacto);
    OnClickListener clickChooseContact = new OnClickListener() {
      @Override
      public void onClick(View v) {
        showChooseContact();
      }
    };
    btnCompartirUbicacion = view.findViewById(R.id.btnCompartirUbicacion);

    btnChooseContacto.setOnClickListener(clickChooseContact);
    btnCentrar = view.findViewById(R.id.btnCentrar);
    OnClickListener clickCentrar = new OnClickListener() {
      @Override
      public void onClick(View v) {
        centrarOrigenDestino();
      }
    };
    btnCentrar.setOnClickListener(clickCentrar);
    Button btnTrazarRuta = view.findViewById(R.id.btnTrazarRuta);
    OnClickListener clickRuta = new OnClickListener() {
      @Override
      public void onClick(View v) {
        showRutaOrigenDestino();
      }
    };
    btnTrazarRuta.setOnClickListener(clickRuta);
    FloatingActionButton btnUbicacion = view.findViewById(R.id.btn_ubicacion);

    tipo = pref.loadTypeUser();
    if (tipo.equals(PantallaInicial.APODERADO)) {
      btnChooseContacto.setVisibility(View.GONE);
    } else if (tipo.equals(PantallaInicial.CONDUCTOR)) {
      btnChooseContacto.setVisibility(View.VISIBLE);
    }
    locationManager = (LocationManager) getActivity()
        .getSystemService(Context.LOCATION_SERVICE);
    OnClickListener clickLocation = new OnClickListener() {
      @Override
      public void onClick(View v) {
        pedirLocalizacion();
      }
    };
    btnUbicacion.setOnClickListener(clickLocation);
    snackBarra = SnackBarUtils.getInstance();
    if (miVista != null) {
      snackStorage = Snackbar
          .make(miVista, "Requiere permiso de almacenamiento",
              Snackbar.LENGTH_INDEFINITE).setAction("PERMITIR",
              new OnClickListener() {
                @Override
                public void onClick(View v) {
                  activity
                      .requestPermissions(
                          permisoAlmacenamiento, CODE_STORAGE);

                }
              });
    }
    if (miVista != null) {
      snackLocation = Snackbar.make(miVista, "Permiso de localización",
          Snackbar.LENGTH_INDEFINITE).setAction("PERMITIR",
          new OnClickListener() {
            @Override
            public void onClick(View v) {
              activity
                  .requestPermissions(
                      permisoLocation, CODE_LOCATION);
            }
          });
      OnClickListener clickShareUbicacion = new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (btnCompartirUbicacion.isChecked()) {
            if (locationManager != null) {
              locationManager.removeUpdates(NavegacionMain.this);
            }
          } else {
            if (activity.checkSelfPermission(permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
              if (snackLocation != null) {
                if (!snackLocation.isShown()) {
                  snackLocation.show();
                }
              }
            } else {
              if (snackLocation != null) {
                if (snackLocation.isShown()) {
                  snackLocation.dismiss();
                }
              }
              if (locationManager != null) {
                locationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
                        NavegacionMain.this);
              }
            }
          }
        }
      };
      btnCompartirUbicacion.setOnClickListener(clickShareUbicacion);
    }
    //ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity());
    String pathMap = pref.cargarPathMapa();
    String pathRender = getPathXml();
    if (activity != null) {

      if (activity.checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        if (!snackStorage.isShown()) {
          snackStorage.show();
        }
      } else {

        if (snackStorage.isShown()) {
          snackStorage.dismiss();
        }

        if (!pathMap.equals("") && pathMap.endsWith(".map") && ((new File(pathMap)).exists())
            && !pathRender.equals("")) {

          inicializarMapa(view, pathMap);
          setPointDefecto();
          inicializarRuta();
          if (tipo.equals(PantallaInicial.CONDUCTOR)) {
            btnCompartirUbicacion.setVisibility(View.VISIBLE);
            Bundle bundle = getArguments();
            String data = null;
            if (bundle != null) {
              data = bundle.getString("DATA", null);

            }

            if (data != null) {
              usuario = gson.fromJson(data, Usuario.class);
              if (usuario == null) {
                usuario = new Usuario();
              }
              lugar = usuario.getLugar();
              if (!usuario.getApodo().equals("") && !usuario.getLatitudInicial().equals("")
                  && !usuario
                  .getLongitudInicial().equals("") && !usuario.getLatitudFinal().equals("")
                  && !usuario
                  .getLongitudFinal().equals("")) {
                establecerCaminos(usuario);
                if (pointInicial != null) {
                  centrar(pointInicial, false, 0);
                } else if (pointFinal != null) {
                  centrar(pointFinal, false, 0);
                }


              }

            }
          /*if (ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_COARSE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
            if (!snackLocation.isShown()) {
              snackLocation.show();
            }


          } else {
            if (snackLocation.isShown()) {
              snackLocation.dismiss();
            }

            //codigo para conductor
            String data = getIntent().getStringExtra("DATA");
            if (data != null) {
              usuario = gson.fromJson(data, Usuario.class);
              if (usuario == null) {
                usuario = new Usuario();
              }
              if (!usuario.getApodo().equals("") && !usuario.getLatitudInicial().equals("")
                  && !usuario
                  .getLongitudInicial().equals("") && !usuario.getLatitudFinal().equals("")
                  && !usuario
                  .getLongitudFinal().equals("")) {
                establecerCaminos(usuario);
              }

            }
          }*/
          } else if (tipo.equals(PantallaInicial.APODERADO)) {
            btnCompartirUbicacion.setVisibility(View.GONE);
            //codigo para apoderado
            String username = pref.loadNombreUsuario();
            if (!username.equals("")) {
              MiPartidaApoderado miPartidaApoderado = getPuntoPartidaApodByUsername(username);
              usuario = new Usuario();
              usuario.setApodo(miPartidaApoderado.getApodo());
              usuario.setLatitudInicial(miPartidaApoderado.getLatitudInicial());
              usuario.setLongitudInicial(miPartidaApoderado.getLongitudInicial());
              usuario.setLatitudFinal(miPartidaApoderado.getLatitudFinal());
              usuario.setLongitudFinal(miPartidaApoderado.getLongitudFinal());
              if (BuildConfig.DEBUG) {
                try {
                  usuario.setLatitudFinal(Double.toString(-33.449705));
                  usuario.setLongitudFinal(Double.toString(-70.661655));
                } catch (Exception e) {
                  usuario.setLatitudFinal("");
                  usuario.setLongitudFinal("");
                }

              }
              if (!usuario.getApodo().equals("") && !usuario.getLatitudInicial().equals("")
                  && !usuario
                  .getLongitudInicial().equals("") && !usuario.getLatitudFinal().equals("")
                  && !usuario
                  .getLongitudFinal().equals("")) {
                establecerCaminos(usuario);
                marcarPosicionConductor();
              }
            }


          } else {
            snackBarra
                .showSnackBar(getActivity(),
                    "Necesita tener un conductor como amigo de mensajeria");
          }


        } else {
          Intent intent = new Intent(getActivity(), DownloadManager.class);
          startActivity(intent);
        }
      }
    }
    return view;
  }

  /*@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);



  }*/

  private void establecerCaminos(Usuario usuario) {

    ArrayList<GeoPoint> waysPoint = new ArrayList<>();

    double latitudInicial = stringToDouble(usuario.getLatitudInicial(),
        PantallaInicial.LATITUD_DEFAULT);
    double longitudInicial = stringToDouble(usuario.getLongitudInicial(),
        PantallaInicial.LONGITUD_DEFAULT);
    lugar = usuario.getLugar();

    apodo = usuario.getApodo();
    if (apodo.equals("")) {
      apodo = getString(R.string.desconocido);
    }
    double latitudFinal = stringToDouble(usuario.getLatitudFinal(), PantallaInicial.LATITUD_FINAL);
    double longitudFinal = stringToDouble(usuario.getLongitudFinal(),
        PantallaInicial.LONGITUD_FINAL);
    pointInicial = new GeoPoint(latitudInicial, longitudInicial);
    pointFinal = new GeoPoint(latitudFinal, longitudFinal);

    waysPoint.add(0, pointInicial);
    waysPoint.add(1, pointFinal);

    crearCamino(waysPoint);

  }

  private void centrar(GeoPoint geoPoint, boolean estaMarcador, int idDrawable) {
    IMapController controller = mapView.getController();
    controller.setCenter(geoPoint);

    if (estaMarcador) {

      Marker marker = new Marker(mapView);
      FragmentActivity activity = getActivity();
      if (activity != null) {

        Drawable drawable = ResourcesCompat
            .getDrawable(activity.getResources(), idDrawable, null);
        marker.setPosition(geoPoint);
        marker.setIcon(drawable);
        marker.setImage(drawable);

        marker.setTitle("");

        if (mapView.getOverlays().size() > 0) {
          mapView.getOverlays().set(0, marker);
        } else {
          mapView.getOverlays().add(marker);
        }
      }

      //mapView.invalidate();
    }
  }

  private double stringToDouble(String string, double valueDefault) {
    double valor;
    try {
      valor = Double.parseDouble(string);
    } catch (Exception e) {
      valor = valueDefault;
    }
    return valor;
  }

  private void setPointDefecto() {
    GeoPoint geoPoint = new GeoPoint(PantallaInicial.LATITUD_DEFAULT,
        PantallaInicial.LONGITUD_DEFAULT);
    IMapController controller = mapView.getController();
    controller.setCenter(geoPoint);
    controller.setZoom(17.0);
  }

  private void pedirLocalizacion() {

    if (locationManager != null) {
      FragmentActivity activity = getActivity();
      if (activity != null) {
        if (activity.checkSelfPermission(
            permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
          if (!snackLocation.isShown()) {
            snackLocation.show();
          }
        } else {
          if (snackLocation.isShown()) {
            snackLocation.dismiss();
          }

          String tipo = pref.loadTypeUser();
          if (tipo.equals(PantallaInicial.APODERADO)) {
            locationManager
                .requestSingleUpdate(LocationManager.GPS_PROVIDER, NavegacionMain.this, null);
          } else if (tipo.equals(PantallaInicial.CONDUCTOR)) {
            locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
                    NavegacionMain.this);
          }

        }
      }

    }
  }

  private void inicializarMapa(View view, String pathMap) {
    FragmentActivity activity = getActivity();
    if (activity != null) {

      final Context ctx = activity.getApplicationContext();
      Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }
    Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    mapView = view.findViewById(R.id.miMap);
    File[] maps = new File[]{new File(pathMap)};

    if (activity != null) {
      MapsForgeTileSource.createInstance(activity.getApplication());
    }
    MapsForgeTileSource fromFiles = MapsForgeTileSource.createFromFiles(maps);
    MapsForgeTileProvider forge = new MapsForgeTileProvider(
        new SimpleRegisterReceiver(getActivity()), fromFiles, null);
    mapView.setTileProvider(forge);
        /*mapView.post(new Runnable() {
          @Override
          public void run() {
            mapView.zoomToBoundingBox(fromFiles.getBoundsOsmdroid(), false);
          }
        });*/
    mapView.getZoomController().setVisibility(Visibility.NEVER);
    mapView.setMultiTouchControls(true);


  }

  private void inicializarRuta() {
    mRoadNodeMarkers = new FolderOverlay();
    mRoadNodeMarkers.setName("Etapas de Ruta");
    mapView.getOverlays().add(mRoadNodeMarkers);
  }

  private void crearCamino(ArrayList<GeoPoint> waysPoint) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        RoadManager roadManager = new OSRMRoadManager(getActivity());

        mRoads = roadManager.getRoad(waysPoint);
        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            updateUIWithRoads(mRoads);
          }
        };
        FragmentActivity activity = getActivity();

        if (activity != null) {
          activity.runOnUiThread(runnable);
        }

      }
    };
    Thread thread = new Thread(runnable);
    if (!thread.isAlive()) {
      thread.start();
    }

  }


  private void marcarPosicionConductor() {
    miRunnable.run();
    /*ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {

        ArrayList<Contact> lista = getAmigos();
        if (lista.size() >= 1) {
          String username = lista.get(0).getNombreUsuario();
          if (!username.trim().equals("")) {

            MyLocation cond = getLocalizacionCondByUsername(username);

            String latitud1 = cond.getLatitud();
            String longitud1 = cond.getLongitud();
            Utilidades util = new Utilidades();
            double latitud = util.stringToDouble(latitud1, 0.0);
            double longitud = util.stringToDouble(longitud1, 0.0);
            FragmentActivity activity = getActivity();
            if (BuildConfig.DEBUG) {
              latitud = -33.449597;
              longitud = -70.661363;
            }
            if (latitud != 0.0 && longitud != 0.0) {
              if (bar != null) {
                if (bar.isShown()) {
                  bar.dismiss();
                }
              }

              String latitudInicial = usuario.getLatitudInicial();
              String longitudInicial = usuario.getLongitudInicial();
              String latitudFinal = usuario.getLatitudFinal();
              String longitudFinal = usuario.getLongitudFinal();
              double latIni = util.stringToDouble(latitudInicial, 0.0);
              double lonIni = util.stringToDouble(longitudInicial, 0.0);
              double latFinal = util.stringToDouble(latitudFinal, 0.0);
              double lonFinal = util.stringToDouble(longitudFinal, 0.0);
              if (latIni != 0.0 && lonIni != 0.0) {
                double distancia = util.distance(latitud, longitud, latIni, lonIni);

                if (distancia <= 25.0) {
                  if (activity != null) {
                    String partida = PantallaInicial.PUNTO_PARTIDA;
                    Notification notificacion = util.shoWNotification(activity,
                        "El conductor esta aproxidamente 25 km de " + partida.toLowerCase());
                    NotificationManager manNoti = (NotificationManager) activity
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manNoti != null) {
                      manNoti.notify(90, notificacion);
                    }
                  }

                }
              }
              if (latFinal != 0.0 && lonFinal != 0.0) {
                double distancia = util.distance(latitud, longitud, latFinal, lonFinal);

                if (distancia <= 25.0) {
                  if (activity != null) {
                    String destino = PantallaInicial.DESTINO;
                    Notification notificacion = util.shoWNotification(activity,
                        "El conductor esta aproxidamente 25 km del " + destino.toLowerCase());
                    NotificationManager manNoti = (NotificationManager) activity
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manNoti != null) {
                      manNoti.notify(90, notificacion);
                    }
                  }

                }
              }
              Marker marker = new Marker(mapView);
              Drawable icon = null;
              if (activity != null) {
                icon = ContextCompat
                    .getDrawable(activity, R.drawable.ic_bus_round);
              }

              marker.setIcon(icon);
              GeoPoint geoPoint = new GeoPoint(latitud,
                  longitud);
              marker.setPosition(geoPoint);
              if (mapView.getOverlays().size() >= 1) {
                mapView.getOverlays().set(0, marker);
              } else {
                mapView.getOverlays().add(0, marker);
              }

              mapView.getController().setCenter(geoPoint);
            } else {
              if (bar != null) {
                if (!bar.isShown()) {
                  bar.setText("Ubicación desconocida del conductor");
                  bar.show();
                }
              }
            }
          }
        } else {
          if (bar != null) {
            if (!bar.isShown()) {
              bar.setText("Sin amigo conductor");
              bar.show();
            }
          }
        }

      }
    };
    service.scheduleWithFixedDelay(runnable, 1, 2, TimeUnit.MINUTES);*/
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


  private ArrayList<Contact> getAmigos() {

    //String tipo = pref.loadTypeUser();
    //if (tipo.equals(PantallaInicial.APODERADO)) {

      /*
      Utilidades util=new Utilidades();
      for (Contact contact : MiAplicacion.listContact) {
        String nombreUsuario = contact.getNombreUsuario();

        MiApodo miApodo = util
            .getApodoByUsername(nombreUsuario, PantallaInicial.CONDUCTOR, getActivity(),
                getActivity());
        String apodo = miApodo.getApodo();
        if (!apodo.equals("")) {
          if (!listaAmigos.contains(apodo)) {
            listaAmigos.add(0, apodo);
          }

        }
        break;
      }*/
    //AbstractXMPPConnection conn = xmpp.logear(username, passwordChat);
      /*AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
      Roster roster = Utilidades.obtenerRoster(conn, getActivity());

      Set<RosterEntry> entries = roster.getEntries();
      for (RosterEntry entry : entries) {
        BareJid barejid = entry.getJid();
        Localpart localPart = barejid.getLocalpartOrNull();
        if (localPart != null) {
          String localParte = localPart.asUnescapedString();
          Utilidades util = new Utilidades();
          MiApodo miApodo = util
              .getApodoByUsername(localParte, PantallaInicial.CONDUCTOR, getActivity(),
                  getActivity());
          String apodo = miApodo.getApodo();
          if (!apodo.equals("")) {
            listaAmigos.add(0, apodo);
          }
          break;
        }
      }


    }*/
    //}
    return MiAplicacion.listContact;
  }

  private void updatePositionConductor(String latitud, String longitud) {
    String tipoUser = pref.loadTypeUser();
    String correo = pref.loadCorreoLogin();
    String password = pref.loadPasswordLogin();
    int miId = pref.loadID();
    String authToken = pref.loadToken();

    if (!correo.equals("") && !password.equals("") && miId != -1
        && !authToken.equals("")) {
      if (tipoUser.equals(PantallaInicial.CONDUCTOR)) {
        ConductorDAO dao = new ConductorDAO(getString(R.string.huella),
            getString(R.string.key_pass));
        FieldUpdatesConductor fieldUpdate = new FieldUpdatesConductor();
        fieldUpdate.setLatitud(latitud);
        fieldUpdate.setLongitud(longitud);
        Object objeto = dao
            .updateConductor(correo, password, authToken, miId, fieldUpdate);
        if (objeto instanceof Mensaje) {
          snackBarra
              .showSnackBar(getActivity(), ((Mensaje) objeto).getMsg());
        }
      }
    }
  }


  private MiPartidaApoderado getPuntoPartidaApodByUsername(String nombreUsuario) {
    MiPartidaApoderado miPartida = new MiPartidaApoderado();
    String correo = pref.loadCorreoLogin();
    String password = pref.loadPasswordLogin();
    int miId = pref.loadID();
    String authToken = pref.loadToken();

    if (!nombreUsuario.equals("") && !correo.equals("") && !password.equals("") && miId != -1
        && !authToken.equals("")) {

      ApoderadoDAO dao = new ApoderadoDAO(getString(R.string.huella),
          getString(R.string.key_pass));
      Object objeto = dao
          .getPuntoPartidaFinalByUsername(miId, authToken, nombreUsuario);
      if (objeto instanceof MiPartidaApoderado) {
        miPartida = (MiPartidaApoderado) objeto;
      }

    }
    return miPartida;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
      case CODE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          // permission was granted, yay! Do the
          // location-related task you need to do.
          FragmentActivity activity = getActivity();
          if (activity != null) {

            if (activity.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

              //Request location updates:
              if (snackLocation.isShown()) {
                snackLocation.dismiss();
              }
              NavegacionMain nav = new NavegacionMain();

              activity.getSupportFragmentManager().beginTransaction()
                  .replace(R.id.miContainer, nav).addToBackStack(null).commit();


            }
          }
        }  // permission denied, boo! Disable the
        // functionality that depends on this permission.

        break;
      }
      case CODE_STORAGE:
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          // permission was granted, yay! Do the
          // location-related task you need to do.
          FragmentActivity activity = getActivity();
          if (activity != null) {
            if (activity.checkSelfPermission(
                permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

              if (snackStorage.isShown()) {
                snackStorage.dismiss();
              }
              NavegacionMain nav = new NavegacionMain();

              activity.getSupportFragmentManager().beginTransaction()
                  .replace(R.id.miContainer, nav).addToBackStack(null).commit();


            }
          }

        }  // permission denied, boo! Disable the
        // functionality that depends on this permission.

        break;
    }
  }


  private void updateUIWithRoads(Road roads) {
    mRoadNodeMarkers.getItems().clear();
    //TextView textView = (TextView)findViewById(R.id.routeInfo);
    //textView.setText("");
    List<Overlay> mapOverlays = mapView.getOverlays();
    if (mRoadOverlays != null) {

      mapOverlays.remove(mRoadOverlays);

      mRoadOverlays = null;
    }
    if (roads == null) {
      return;
    }
    if (roads.mStatus == Road.STATUS_TECHNICAL_ISSUE) {
      Toast.makeText(mapView.getContext(), "Technical issue when getting the route",
          Toast.LENGTH_SHORT).show();
    } else if (roads.mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
    {
      Toast.makeText(mapView.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
    }
    mRoadOverlays = new Polyline();

    Polyline roadPolyline = RoadManager.buildRoadOverlay(roads);
    mRoadOverlays = roadPolyline;
    FragmentActivity activity = getActivity();
    String routeDesc = null;
    if (activity != null) {
      routeDesc = roads.getLengthDurationText(activity, -1);
    }
    roadPolyline.setTitle(getString(R.string.route) + " - " + routeDesc);
    roadPolyline.setInfoWindow(
        new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView));
    roadPolyline.setRelatedObject(0);

      /*roadPolyline.setOnClickListener(new Polyline.OnClickListener() {
        @Override
        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
          return false;
        }
      });*/
    if (mapOverlays.size() >= 2) {
      mapOverlays.set(1, roadPolyline);
    } else {
      mapOverlays.add(1, roadPolyline);
    }

    //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
    //to avoid covering the other overlays.

    selectRoad();
  }

  private void selectRoad() {

    putRoadNodes(mRoads);
    //Set route info in the text view:
    //TextView textView = (TextView)findViewById(R.id.routeInfo);
    //textView.setText(mRoads[roadIndex].getLengthDurationText(getActivity(), -1));

    Paint p = mRoadOverlays.getPaint();

    p.setColor(0x800000FF); //blue

    mapView.invalidate();
  }

  private void putRoadNodes(Road road) {
    mRoadNodeMarkers.getItems().clear();
    Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
    int n = road.mNodes.size();
    MarkerInfoWindow infoWindow = new MarkerInfoWindow(
        org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);

    TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);
    String tipo = pref.loadTypeUser();
    FragmentActivity activity = getActivity();
    for (int i = 0; i < n; i++) {

      RoadNode node = road.mNodes.get(i);
      String instructions = (node.mInstructions == null ? "" : node.mInstructions);
      Marker nodeMarker = new Marker(mapView);
      nodeMarker.setTitle(getString(R.string.step) + " " + (i + 1));
      nodeMarker.setSnippet(instructions);
      if (activity != null) {
        nodeMarker.setSubDescription(
            Road.getLengthDurationText(activity, node.mLength,
                node.mDuration));
      }
      nodeMarker.setPosition(node.mLocation);
      nodeMarker.setIcon(icon);
      nodeMarker.setInfoWindow(infoWindow); //use a shared infowindow.
      int iconId = iconIds.getResourceId(node.mManeuverType, R.drawable.ic_empty);
      if (iconId != R.drawable.ic_empty) {
        Drawable image = ResourcesCompat.getDrawable(getResources(), iconId, null);
        nodeMarker.setImage(image);
      }
      nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
      if (tipo.equals(PantallaInicial.CONDUCTOR)) {

        if (i == 0) {
          marcador1 = nodeMarker;
        }
        if (i == (n - 1)) {
          marcador2 = nodeMarker;
        }
      }
      mRoadNodeMarkers.add(nodeMarker);
    }
    iconIds.recycle();
    if (tipo.equals(PantallaInicial.CONDUCTOR)) {
      if (marcador1 != null) {
        marcador1.setOnMarkerClickListener(listenerMarcador);
        if (lugar.equals(PantallaInicial.PUNTO_PARTIDA)) {
          marcador1.setTitle(apodo.concat(" está aquí"));

        }

      }
      if (marcador2 != null) {
        marcador2.setOnMarkerClickListener(listenerMarcador);
        if (lugar.equals(PantallaInicial.DESTINO)) {
          marcador2.setTitle(apodo.concat(" está aquí"));
        }

      }
    }
  }

  private MyLocation getLocalizacionCondByUsername(String nombreUsuario) {

    MyLocation miPartida = new MyLocation();
    String correo = pref.loadCorreoLogin();
    String password = pref.loadPasswordLogin();
    int miId = pref.loadID();
    String authToken = pref.loadToken();

    if (!nombreUsuario.equals("") && !correo.equals("") && !password.equals("") && miId != -1
        && !authToken.equals("")) {

      ConductorDAO dao = new ConductorDAO(getString(R.string.huella),
          getString(R.string.key_pass));
      Object objeto = dao
          .getLocalizacion(miId, authToken, nombreUsuario);
      if (objeto instanceof MyLocation) {
        miPartida = (MyLocation) objeto;
      }

    }
    return miPartida;
  }



/*
  @Override
  public void destroy() {

  }*/

  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      String tipo = pref.loadTypeUser();
      if (tipo.equals(PantallaInicial.CONDUCTOR)) {
        double latitud = location.getLatitude();
        double longitud = location.getLongitude();
        pref.guardarLatitud(String.valueOf(latitud));
        pref.guardarLongitud(String.valueOf(longitud));
        pointInicial = new GeoPoint(location);
        updatePositionConductor(latitud + "", longitud + "");
        centrar(pointInicial, true, R.drawable.ic_bus_round);
      } else if (tipo.equals(PantallaInicial.APODERADO)) {
        double latitud = location.getLatitude();
        double longitud = location.getLongitude();
        pref.guardarLatitud(String.valueOf(latitud));
        pref.guardarLongitud(String.valueOf(longitud));
        GeoPoint point = new GeoPoint(location);

        centrar(point, true, R.drawable.marker_red);
        if (locationManager != null) {
          locationManager.removeUpdates(NavegacionMain.this);
        }

      }

    }
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }
}



