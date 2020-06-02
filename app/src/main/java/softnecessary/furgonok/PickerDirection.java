package softnecessary.furgonok;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.dao.users.ConductorDAO;
import softnecessary.furgonok.pojo.serialized.Apoderado;
import softnecessary.furgonok.pojo.serialized.Conductor;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesApoderado;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesConductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SnackBarUtils;


public class PickerDirection extends AppCompatActivity implements LocationListener {

  private static final int CODE_LOCATION = 13;
  private static final int CODE_STORAGE = 14;
  private final String[] permisoAlmacenamiento = new String[]{permission.READ_EXTERNAL_STORAGE};
  private final String[] permisoLocation = new String[]{permission.ACCESS_FINE_LOCATION};
  private MisPreferencias pref;
  private SnackBarUtils snack;
  private Snackbar miSnack;
  private LocationManager locationManager;
  private int cont = 0;
  private IMapController mapController = null;
  private EditText etLatitud;
  private EditText etLongitud;
  private EditText etPuntoPartida;
  private EditText etDestino;
  private MapView miMap;
  private TextView tvCasaColegio;
  private ToggleButton btnElegirInicioFin;
  private Snackbar miSnackAlmacen;
  private Overlay touchOverlay;
  private String lugar = "";
  private ToggleButton btnLugar;
  private TextView tvSeleccionLugar;

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

  private void inicializarMapa() {
    final Context ctx = getApplicationContext();
    Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    miMap = findViewById(R.id.mi_mapview);
    miMap.setMultiTouchControls(true);
    mapController = miMap.getController();
    mapController.setZoom(17.0);
  }

  private GeoPoint centrarEnMapa() {

    GeoPoint startPoint = new GeoPoint(PantallaInicial.LATITUD_DEFAULT,
        PantallaInicial.LONGITUD_DEFAULT);
    mapController.setCenter(startPoint);
    return startPoint;


  }

  private void marcarPunto(GeoPoint point) {
    Marker miMarker = new Marker(miMap);
    Drawable iconMarker = ContextCompat
        .getDrawable(PickerDirection.this, R.drawable.marker_red);

    miMarker.setIcon(iconMarker);
    miMarker.setPosition(point);
    if (miMap.getOverlays().size() > 0) {
      miMap.getOverlays().set(0, miMarker);
    } else {
      miMap.getOverlays().add(0, miMarker);
    }
  }

  private void setPosicion(GeoPoint point) {
    etLatitud.setText(String.valueOf(point.getLatitude()));
    etLongitud.setText(String.valueOf(point.getLongitude()));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_picker_direction);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.elige_lugar_mapa);
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }

    View miVista = findViewById(android.R.id.content);
    etLatitud = new EditText(this);
    etLongitud = new EditText(this);
    lugar = PantallaInicial.PUNTO_PARTIDA;
    etPuntoPartida = findViewById(R.id.etPuntoPartida);
    btnElegirInicioFin = findViewById(R.id.btn_toggle_lugar);
    etDestino = findViewById(R.id.etDestino);
    etPuntoPartida.setVisibility(View.VISIBLE);
    etDestino.setVisibility(View.GONE);

    pref = new MisPreferencias(PickerDirection.this);
    Button btnListo = findViewById(R.id.btn_listo);
    //btnListo.setText(getString(R.string.save_partida));

    btnLugar = findViewById(R.id.btn_mi_lugar);
    tvSeleccionLugar = findViewById(R.id.tv_seleccion_lugar);
    if (btnLugar.isChecked()) {
      tvSeleccionLugar.setText(R.string.elegi_estar_en_el_colegio);
      btnLugar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
    } else {
      btnLugar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school, 0, 0, 0);
      tvSeleccionLugar.setText(R.string.elegi_estar_en_casa);
    }
    tvCasaColegio = findViewById(R.id.tv_casa_colegio);
    if (btnElegirInicioFin.isChecked()) {
      btnElegirInicioFin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
      //btnElegirInicioFin.setButtonDrawable();
      tvCasaColegio.setText("Colegio: ");
    } else {
      btnElegirInicioFin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school, 0, 0, 0);
      //btnElegirInicioFin.setButtonDrawable(R.drawable.ic_school);
      tvCasaColegio.setText("Casa: ");
    }
    OnClickListener clickLugar = new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!btnLugar.isChecked()) {
          lugar = PantallaInicial.DESTINO;
          tvSeleccionLugar.setText(R.string.elegi_estar_en_casa);
          btnLugar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school, 0, 0, 0);
        } else {
          lugar = PantallaInicial.PUNTO_PARTIDA;
          tvSeleccionLugar.setText(R.string.elegi_estar_en_el_colegio);
          btnLugar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
        }
      }
    };
    btnLugar.setOnClickListener(clickLugar);
    OnClickListener clickInicioFin = new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (btnElegirInicioFin.isChecked()) {
          etPuntoPartida.setVisibility(View.GONE);
          etDestino.setVisibility(View.VISIBLE);
          tvCasaColegio.setText("Colegio: ");
          btnElegirInicioFin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
        } else {
          etPuntoPartida.setVisibility(View.VISIBLE);
          etDestino.setVisibility(View.GONE);
          tvCasaColegio.setText("Casa: ");
          btnElegirInicioFin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school, 0, 0, 0);
        }
      }
    };
    btnElegirInicioFin.setOnClickListener(clickInicioFin);

    FloatingActionButton btnLocalizar = findViewById(R.id.btn_location);
    snack = SnackBarUtils.getInstance();
    OnClickListener clickMiSnack = new OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityCompat.requestPermissions(PickerDirection.this,
            permisoLocation,
            CODE_LOCATION);
      }
    };
    miSnack = Snackbar.make(miVista, "Requiere permiso de localización",
        Snackbar.LENGTH_INDEFINITE).setAction("PERMITIR", clickMiSnack);
    OnClickListener clickSnackAlmacen = new OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityCompat.requestPermissions(PickerDirection.this,
            permisoAlmacenamiento,
            CODE_STORAGE);
      }
    };
    miSnackAlmacen = Snackbar
        .make(miVista, "Requiere permiso de almacenamiento",
            Snackbar.LENGTH_INDEFINITE).setAction("PERMITIR", clickSnackAlmacen);
    OnClickListener clickLocalizar = new OnClickListener() {
      @Override
      public void onClick(View v) {
        localizar();
      }
    };
    btnLocalizar.setOnClickListener(clickLocalizar);
    OnClickListener clickListo = new OnClickListener() {
      @Override
      public void onClick(View v) {
        listo();
      }
    };
    btnListo.setOnClickListener(clickListo);
    if (ActivityCompat.checkSelfPermission(PickerDirection.this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      if (!miSnack.isShown()) {
        miSnack.show();
      }
    } else {
      if (miSnack.isShown()) {
        miSnack.dismiss();
      }

      if (ActivityCompat.checkSelfPermission(PickerDirection.this, permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        if (!miSnackAlmacen.isShown()) {
          miSnackAlmacen.show();
        }
      } else {
        if (miSnackAlmacen.isShown()) {
          miSnackAlmacen.dismiss();
        }
        inicializarMapa();

        String pathMapa = pref.cargarPathMapa();
        File fileMap = new File(pathMapa);
        if (fileMap.exists() && pathMapa.endsWith(".map")) {
          File[] maps = new File[]{fileMap};
          MapsForgeTileSource.createInstance(this.getApplication());
          MapsForgeTileSource fromFile = MapsForgeTileSource.createFromFiles(maps);
          MapsForgeTileProvider forge = new MapsForgeTileProvider(new SimpleRegisterReceiver(this),
              fromFile, null);
          miMap.setTileProvider(forge);
        /*miMap.post(new Runnable() {
          @Override
          public void run() {
            miMap.zoomToBoundingBox(fromFile.getBoundsOsmdroid(), false);
          }
        });*/

          GeoPoint startPoint = centrarEnMapa();
          setPosicion(startPoint);
          marcarPunto(startPoint);
          String puntoPartida = getPuntoPartida(startPoint.getLatitude(),
              startPoint.getLongitude());
          if (etPuntoPartida.getVisibility() == View.VISIBLE) {
            etPuntoPartida.setText(puntoPartida);
          } else if (etDestino.getVisibility() == View.VISIBLE) {
            etDestino.setText(puntoPartida);
          }
          touchOverlay = new Overlay() {
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;

            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

              Drawable marker = getApplicationContext().getDrawable(R.drawable.marker_red);
              Projection proj = mapView.getProjection();
              GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());

              double longitude = loc.getLongitude();
              double latitude = loc.getLatitude();
              String tipo = pref.loadTypeUser();
              setPosicion(loc);
              if (tipo.equals(PantallaInicial.APODERADO)) {

                if (etPuntoPartida.getVisibility() == View.VISIBLE) {
                  String puntoPartida = getPuntoPartida(latitude, longitude);
                  etPuntoPartida.setText(puntoPartida);
                } else if (etDestino.getVisibility() == View.VISIBLE) {
                  String lugar = getPuntoPartida(latitude, longitude);
                  etDestino.setText(lugar);
                }

              }

              //miLatitud = latitude;
              //miLongitud = longitude;
              ArrayList<OverlayItem> overlayArray = new ArrayList<>();

              OverlayItem mapItem = new OverlayItem("", "",
                  new GeoPoint(loc.getLatitude(),
                      loc.getLongitude()));
              mapItem.setMarker(marker);
              if (overlayArray.size() > 0) {
                overlayArray.set(0, mapItem);
              } else {
                overlayArray.add(0, mapItem);
              }

              if (anotherItemizedIconOverlay == null) {
                anotherItemizedIconOverlay = new ItemizedIconOverlay<>(
                    getApplicationContext(),
                    overlayArray, null);
                if (mapView.getOverlays().size() > 0) {
                  mapView.getOverlays().set(0, anotherItemizedIconOverlay);
                } else {
                  mapView.getOverlays().add(0, anotherItemizedIconOverlay);
                }
                //mapView.getOverlays().add(anotherItemizedIconOverlay);
                //mapView.invalidate();
              } else {
                mapView.getOverlays().remove(anotherItemizedIconOverlay);
                //mapView.invalidate();
                anotherItemizedIconOverlay = new ItemizedIconOverlay<>(
                    getApplicationContext(),
                    overlayArray, null);
                //mapView.getOverlays().add(anotherItemizedIconOverlay);
                if (mapView.getOverlays().size() > 0) {
                  mapView.getOverlays().set(0, anotherItemizedIconOverlay);
                } else {
                  mapView.getOverlays().add(0, anotherItemizedIconOverlay);
                }
              }
              //      dlgThread();
              return true;
            }
          };
          if (miMap.getOverlays().size() >= 2) {
            miMap.getOverlays().set(1, touchOverlay);
          } else {
            miMap.getOverlays().add(1, touchOverlay);
          }


        }

      }


    }

  }

  private void listo() {
    String puntoPartida = etPuntoPartida.getText().toString();
    if (!puntoPartida.equals("")) {
      actualizarPuntoPartida();
    }
  }

  private String getPuntoPartida(double miLatitud, double miLongitud) {
    Locale local = new Locale("es", "CL");
    Geocoder geo = new Geocoder(PickerDirection.this, local);
    String resultado;
    try {
      List<Address> listAddress = geo
          .getFromLocation(miLatitud, miLongitud, 1);
      resultado = listAddress.get(0).getAddressLine(0);
    } catch (Exception e) {
      resultado = "";

    }
    if (resultado == null) {
      resultado = "";
    }
    return resultado;
  }

  private void localizar() {

    if (ActivityCompat.checkSelfPermission(PickerDirection.this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      if (!miSnack.isShown()) {
        miSnack.show();
      }
    } else {
      locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
      if (locationManager != null) {
        locationManager
            .requestSingleUpdate(LocationManager.GPS_PROVIDER, PickerDirection.this,
                null);
      }
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == CODE_STORAGE) {
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int grantResult = grantResults[i];

        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
          if (grantResult == PackageManager.PERMISSION_GRANTED) {
            if (miSnackAlmacen.isShown()) {
              miSnackAlmacen.dismiss();
            }
            recreate();
          } else {
            if (!miSnackAlmacen.isShown()) {
              miSnackAlmacen.show();
            }
          }
        }

      }
    }

    if (requestCode == CODE_LOCATION) {
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int grantResult = grantResults[i];

        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
          if (grantResult == PackageManager.PERMISSION_GRANTED) {
            if (miSnack.isShown()) {
              miSnack.dismiss();
            }
            recreate();
          } else {
            if (!miSnack.isShown()) {
              miSnack.show();
            }
          }
        }

      }
    }

  }

  private void actualizarPuntoPartida() {
    String tipoUsuario = pref.loadTypeUser();
    String email = pref.loadCorreoLogin();
    String password = pref.loadPasswordLogin();
    String token = pref.loadToken();
    String miLatitud = etLatitud.getText().toString();
    String miLongitud = etLongitud.getText().toString();
    int miID = pref.loadID();
    if (!tipoUsuario.equals("") && !email.equals("") && !password.equals("") && !token.equals("")
        && miID != -1) {
      if (tipoUsuario.equals(PantallaInicial.APODERADO)) {
        ApoderadoDAO dao = new ApoderadoDAO(getString(R.string.huella),
            getString(R.string.key_pass));
        FieldUpdatesApoderado apoderado = new FieldUpdatesApoderado();
        apoderado.setLugar(lugar);
        if (etPuntoPartida.getVisibility() == View.VISIBLE) {
          apoderado.setLatitudInicial(miLatitud);
          apoderado.setLongitudInicial(miLongitud);
        } else if (etDestino.getVisibility() == View.VISIBLE) {
          apoderado.setLatitudFinal(miLatitud);
          apoderado.setLongitudFinal(miLongitud);
        }

        Object objeto = dao.updateApoderado(email, password, token, miID, apoderado);
        if (objeto instanceof Mensaje) {
          String mensaje = ((Mensaje) objeto).getMsg();
          snack.showSnackBar(this, mensaje);
        } else if (objeto instanceof Apoderado) {
          snack.showSnackBar(this, "Punto de partida actualizado con exito");

        }
      } else if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
        ConductorDAO dao = new ConductorDAO(getString(R.string.huella),
            getString(R.string.key_pass));
        FieldUpdatesConductor conductor = new FieldUpdatesConductor();
        conductor.setLatitud(miLatitud);
        conductor.setLongitud(miLongitud);
        Object objeto = dao.updateConductor(email, password, token, miID, conductor);
        if (objeto instanceof Mensaje) {
          String mensaje = ((Mensaje) objeto).getMsg();
          snack.showSnackBar(this, mensaje);
        } else if (objeto instanceof Conductor) {
          snack.showSnackBar(this, "Punto de partida actualizado con exito");
          //pref.savePuntoPartidaPerfil(puntoPartida);
        }
      }
    } else {
      snack.showSnackBar(this, "Falló en la autenticación de usuario");
    }

  }

  @Override
  public void onLocationChanged(Location location) {
    cont++;
    if (location == null) {
      if (ActivityCompat.checkSelfPermission(PickerDirection.this, permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      }

    }
    if (location != null) {

      double latitude = location.getLatitude();
      double longitude = location.getLongitude();
      GeoPoint startPoint = new GeoPoint(latitude, longitude);
      mapController.setCenter(startPoint);
      Marker marker = new Marker(miMap);
      Drawable iconMarker = ContextCompat.getDrawable(PickerDirection.this, R.drawable.marker_red);
      marker.setIcon(iconMarker);
      marker.setPosition(startPoint);
      etLatitud.setText("" + latitude);
      etLongitud.setText("" + longitude);
      miMap.getOverlays().clear();
      miMap.getOverlays().add(0, marker);
      miMap.getOverlays().add(touchOverlay);
      String puntoPartida = getPuntoPartida(latitude, longitude);
      if (etPuntoPartida.getVisibility() == View.VISIBLE) {
        etPuntoPartida.setText(puntoPartida);
        locationManager.removeUpdates(this);
      }
      if (etDestino.getVisibility() == View.VISIBLE) {
        etDestino.setText(puntoPartida);
        locationManager.removeUpdates(this);
      }

    }
    if (cont == 10) {
      cont = 0;
      locationManager.removeUpdates(this);
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
