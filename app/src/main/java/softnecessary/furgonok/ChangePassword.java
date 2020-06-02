package softnecessary.furgonok;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
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
import softnecessary.furgonok.pojo.serialized.FieldUpdatesApoderado;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesConductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.Utilidades;

public class ChangePassword extends AppCompatActivity {

  private EditText cajaNewPassword;
  private EditText cajaOldPassword;
  private AwesomeValidation awesomeVal;
  private MisPreferencias pref;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_change_password);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.modifica_password);
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }

    cajaNewPassword = findViewById(R.id.caja_new_password);
    cajaOldPassword = findViewById(R.id.caja_old_password);

    awesomeVal = new AwesomeValidation(ValidationStyle.BASIC);
    pref = new MisPreferencias(this);
    awesomeVal.addValidation(this, R.id.caja_old_password, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return pref.loadPasswordLogin().equals(s);
      }
    }, R.string.error_old_password_invalid);
    awesomeVal.addValidation(this, R.id.caja_old_password, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return s.trim().length() >= 5;
      }
    }, R.string.error_caja_old_password_length);
    awesomeVal.addValidation(this, R.id.caja_new_password, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return s.trim().length() >= 5;
      }
    }, R.string.error_caja_new_password_length);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  protected void onStart() {
    super.onStart();
    MiAplicacion.runServiceRespaldo(this);
    MiAplicacion.stopServiceNotification(this);
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

  public void clickCambiarPassword(View view) {
    if (awesomeVal.validate()) {
      String tipoUsuario = pref.loadTypeUser();
      String email = pref.loadCorreoLogin();
      int miId = pref.loadID();
      String authToken = pref.loadToken();
      String passwordOld = cajaOldPassword.getText().toString();
      String passwordNew = cajaNewPassword.getText().toString();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          if (tipoUsuario.equals(PantallaInicial.APODERADO)) {
            ApoderadoDAO dao = new ApoderadoDAO(getString(R.string.huella),
                getString(R.string.key_pass));
            FieldUpdatesApoderado apoderado = new FieldUpdatesApoderado();
            apoderado.setPassword(passwordNew);
            Object objeto = dao.updateApoderado(email, passwordOld, authToken, miId, apoderado);
            if (objeto instanceof Mensaje) {
              String mensaje = ((Mensaje) objeto).getMsg();
              Utilidades.showMsg(ChangePassword.this, mensaje);
              //Snackbar.make(mainRL, mensaje, Snackbar.LENGTH_INDEFINITE).show();
            } else if (objeto instanceof Apoderado) {
              pref.guardarPassword(passwordNew);
              Utilidades.showMsg(ChangePassword.this, "Nueva contraseña registrada exitósamente");

            }
          } else if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
            ConductorDAO dao = new ConductorDAO(getString(R.string.huella),
                getString(R.string.key_pass));
            FieldUpdatesConductor conductor = new FieldUpdatesConductor();
            conductor.setPassword(passwordNew);
            Object objeto = dao.updateConductor(email, passwordOld, authToken, miId, conductor);
            if (objeto instanceof Mensaje) {
              String mensaje = ((Mensaje) objeto).getMsg();
              Utilidades.showMsg(ChangePassword.this, mensaje);
              //Snackbar.make(mainRL, mensaje, Snackbar.LENGTH_INDEFINITE).show();
            } else if (objeto instanceof Conductor) {
              pref.guardarPassword(passwordNew);
              Utilidades.showMsg(ChangePassword.this, "Nueva contraseña registrada exitósamente");
              /*Snackbar.make(mainRL, "Nueva contraseña registrada exitósamente",
                  Snackbar.LENGTH_INDEFINITE).show();*/
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
}
