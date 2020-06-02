package softnecessary.furgonok;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.dao.users.ConductorDAO;
import softnecessary.furgonok.pojo.serialized.Apoderado;
import softnecessary.furgonok.pojo.serialized.Conductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SimpleEula;
import softnecessary.furgonok.utils.SnackBarUtils;

public class LoginPantalla extends AppCompatActivity {


  private EditText cajaCorreo;
  private EditText cajaPassword;

  private AwesomeValidation awesomeVal;
  private MisPreferencias pref;
  private Spinner tipoUser;
  private SnackBarUtils snackBarUtils;
  private ImageView ivProfile = null;

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
    setContentView(R.layout.activity_login_pantalla);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.inicio_sesion));
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }

    snackBarUtils = SnackBarUtils.getInstance();
    pref = new MisPreferencias(this);
    awesomeVal = new AwesomeValidation(ValidationStyle.BASIC);
    tipoUser = findViewById(R.id.spinner_tipo_usuario);
    ivProfile = findViewById(R.id.profile_img);
    cajaCorreo = findViewById(R.id.caja_email);
    if (MiAplicacion.MI_LOGIN_EMAIL != null) {
      cajaCorreo.setText(MiAplicacion.MI_LOGIN_EMAIL);
    }
    cajaCorreo.setKeyListener(null);
    cajaPassword = findViewById(R.id.caja_password);
    String[] itemsSpinner = getResources().getStringArray(R.array.tipoUsers);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.items_spinner,
        R.id.tv_item_spinner, itemsSpinner);
    tipoUser.setAdapter(adapter);
    awesomeVal.addValidation(this, R.id.caja_email, Patterns.EMAIL_ADDRESS, R.string.error_correo);
    awesomeVal.addValidation(this, R.id.caja_password, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return s.length() > 5;
      }
    }, R.string.error_password);
    if (!BuildConfig.DEBUG) {

      awesomeVal.addValidation(this, R.id.caja_email,
          new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
              if (s != null && MiAplicacion.MI_LOGIN_EMAIL != null) {
                return MiAplicacion.MI_LOGIN_EMAIL.trim().equals(s.trim());
              }
              return false;
            }
          }, R.string.error_email_login);
    }
    tipoUser.setSelection(pref.loadTipoUserNumber());

    tipoUser.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
          ivProfile.setImageResource(R.drawable.darth_vader);
        } else {
          ivProfile.setImageResource(R.drawable.ic_soy_conductor);
          if (BuildConfig.DEBUG) {

            cajaCorreo.setText("r.leal01@gmail.com");
          }
        }
        pref.saveTipoUserNumber(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    new SimpleEula(this).show();
  }

  public void entrar(View view) {
    if (awesomeVal.validate()) {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          String password = cajaPassword.getText().toString();
          String correo = cajaCorreo.getText().toString();

          if (tipoUser.getSelectedItemPosition() == 0) {

            ApoderadoDAO apodDAO = new ApoderadoDAO(getString(R.string.huella),
                getString(R.string.key_pass));
            Object objeto = apodDAO.loggear(correo, password);
            if (objeto instanceof Mensaje) {
              snackBarUtils.showSnackBar(LoginPantalla.this, ((Mensaje) objeto).getMsg());

            } else if (objeto instanceof Apoderado) {

              pref.guardarCredentialsLogin(correo, password);
              String nombreUsuario = ((Apoderado) objeto).getNombreUsuario();
              pref.guardarNombreUsuario(nombreUsuario);
              String authToken = ((Apoderado) objeto).getAuthToken();
              int miID = ((Apoderado) objeto).getId();

              pref.saveTokenID(authToken, miID);
              pref.saveTypeUser(PantallaInicial.APODERADO);
              String passwordChat = ((Apoderado) objeto).getPasswordChat();

              pref.guardarPasswordChat(passwordChat);
              //Start the service

              Intent intent = new Intent(LoginPantalla.this, MainActivity.class);
              Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                  startActivity(intent);
                }
              };
              runOnUiThread(runnable1);

            } else {
              snackBarUtils.showSnackBar(LoginPantalla.this, "Conexión fallida");

            }


          } else if (tipoUser.getSelectedItemPosition() == 1) {

            ConductorDAO condDAO = new ConductorDAO(getString(R.string.huella),
                getString(R.string.key_pass));
            Object objeto = condDAO.loggear(correo, password);
            if (objeto instanceof Mensaje) {
              snackBarUtils.showSnackBar(LoginPantalla.this, ((Mensaje) objeto).getMsg());

            } else if (objeto instanceof Conductor) {

              pref.saveTypeUser(PantallaInicial.CONDUCTOR);
              //Start the service
              pref.guardarCredentialsLogin(correo, password);
              String authToken = ((Conductor) objeto).getAuthToken();
              int miID = ((Conductor) objeto).getId();

              pref.saveTokenID(authToken, miID);
              String nombreUsuario = ((Conductor) objeto).getNombreUsuario();
              String passwordChat = ((Conductor) objeto).getPasswordChat();

              pref.guardarPasswordChat(passwordChat);
              pref.guardarNombreUsuario(nombreUsuario);
              //Intent i1 = new Intent(this, ConnectionService.class);
              //startService(i1);
              Intent intent = new Intent(LoginPantalla.this, MainActivity.class);

              Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                  startActivity(intent);
                }
              };
              runOnUiThread(runnable1);

            } else {
              snackBarUtils.showSnackBar(LoginPantalla.this, "Conexión fallida");

            }
          }
        }
      };
      Thread thread = new Thread(runnable);
      if (!thread.isAlive()) {
        thread.start();
      }

    }
  }

  public void limpiarEntrada(View view) {

    cajaCorreo.setText("");
    cajaPassword.setText("");

    snackBarUtils.showSnackBar(this, "Campos limpiados");
  }


}
