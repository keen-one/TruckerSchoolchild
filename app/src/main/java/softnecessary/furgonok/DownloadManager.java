package softnecessary.furgonok;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.google.android.material.snackbar.Snackbar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import java.io.File;
import java.io.FilenameFilter;
import lib.folderpicker.FolderPicker;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SnackBarUtils;

public class DownloadManager extends AppCompatActivity {


  private static final int CODE_PERMISSION_STORAGE = 998;
  private static final int FOLDER_CODE = 322;
  private ProgressBar progressBar;
  private TextView tvProgreso;
  private ToggleButton btnDownload;

  private Button btnTermino;
  private MisPreferencias pref;
  private Snackbar snackbar = null;
  private SnackBarUtils snack = null;
  private AwesomeValidation awesomeVal;
  private EditText etCarpetaDescarga;

  public void cerrarPantalla(View view) {
    finish();
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
    setContentView(R.layout.activity_download_manager);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle("Descarga el mapa");
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    awesomeVal = new AwesomeValidation(ValidationStyle.BASIC);
    pref = new MisPreferencias(this);

    snack = SnackBarUtils.getInstance();
    OnClickListener clickSnackbar = new OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityCompat.requestPermissions(DownloadManager.this,
            new String[]{permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE}, CODE_PERMISSION_STORAGE);
      }
    };
    snackbar = Snackbar
        .make(findViewById(android.R.id.content), "Requiere permiso de almacenamiento",
            Snackbar.LENGTH_INDEFINITE).setAction("Permitir", clickSnackbar);
    btnTermino = findViewById(R.id.btn_termino);
    progressBar = findViewById(R.id.barra_progreso);
    tvProgreso = findViewById(R.id.tv_progreso);
    btnDownload = findViewById(R.id.btn_descarga);
    etCarpetaDescarga = findViewById(R.id.etCarpetaDescarga);

    btnTermino.setVisibility(View.INVISIBLE);
    awesomeVal.addValidation(this, R.id.etCarpetaDescarga, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return s.trim().length() > 5;
      }
    }, R.string.error_folder_invalid);
    //setLastModifiedSizeUrl();
  }

  public void mostrarEleccionDirectorio(View view) {
    if (ActivityCompat.checkSelfPermission(DownloadManager.this, permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(DownloadManager.this,
        permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

      if (!snackbar.isShown()) {
        snackbar.show();
      }

    } else {
      if (snackbar.isShown()) {
        snackbar.dismiss();
      }
      Intent intent = new Intent(DownloadManager.this, FolderPicker.class);
      startActivityForResult(intent, FOLDER_CODE);
    }

  }

  public void btnDescargar(View view) {

    if (!btnDownload.isChecked()) {
      //checkear el permiso de escritura
      if (ActivityCompat
          .checkSelfPermission(DownloadManager.this, permission.WRITE_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(DownloadManager.this,
          permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

        if (!snackbar.isShown()) {
          snackbar.show();
        }

      } else {
        if (snackbar.isShown()) {
          snackbar.dismiss();
        }
        descargar();
      }

    } else {
      parar();
    }


  }

  private void descargar() {
    if (awesomeVal.validate()) {
      String folder = etCarpetaDescarga.getText().toString();

      //String path = etPathCarpeta.getText().toString();
      File result1 = new File(folder);
      FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.toLowerCase().endsWith(".map");
        }
      };

      if (result1.exists()) {
        File[] miListFiles = result1.listFiles(filter);
        if (miListFiles != null) {
          File nuestroFile = null;
          long numero = 0;
          for (int i = 0; i < miListFiles.length; i++) {
            File file1 = miListFiles[i];
            numero = Math.max(file1.lastModified(), 0);
            for (int j = i + 1; j < miListFiles.length; j++) {
              File file2 = miListFiles[j];
              numero = Math.max(file1.lastModified(), file2.lastModified());
            }
          }
          for (File elFile : miListFiles) {
            if (elFile.lastModified() == numero) {
              nuestroFile = elFile;
              break;
            }
          }
          if (nuestroFile != null) {
            if (nuestroFile.exists()) {
              long fecha = nuestroFile.lastModified();
              pref.guardarFechaDescargaMapa(PantallaInicial.KEY_FECHA_DESCARGA, fecha);
              pref.guardarPathMapa(nuestroFile.getAbsolutePath());
              btnTermino.setVisibility(View.VISIBLE);
            } else {
              descargarAhora(folder);
            }
          } else {
            descargarAhora(folder);
          }
        } else {
          descargarAhora(folder);
        }


      } else {
        descargarAhora(folder);
      }

    }
  }

  private void descargarAhora(String folder) {
    String url = "http://download.mapsforge.org/maps/v4/south-america/chile.map";
    try {
      File file = new File(folder + File.separator + System.currentTimeMillis() + "-chile.map");

      Ion.with(DownloadManager.this)
          .load(url)
          // attach the percentage report to a progress bar.
          // can also attach to a ProgressDialog with progressDialog.
          .progressBar(progressBar)
          // callbacks on progress can happen on the UI thread
          // via progressHandler. This is useful if you need to update a TextView.
          // Updates to TextViews MUST happen on the UI thread.
          .progressHandler(new ProgressCallback() {
            @Override
            public void onProgress(long downloaded, long total) {
              tvProgreso.setText("" + downloaded + " / " + total);
            }
          })
          // write to a file
          .write(file)
          // run a callback on completion
          .setCallback(new FutureCallback<File>() {
            @Override
            public void onCompleted(Exception e, File result) {

              if (e != null) {
                String mensaje = e.getLocalizedMessage();
                if (mensaje != null) {

                  snack.showSnackBar(DownloadManager.this, mensaje);
                }

                return;
              }
              long fecha = result.lastModified();
              pref.guardarFechaDescargaMapa(PantallaInicial.KEY_FECHA_DESCARGA, fecha);
              pref.guardarPathMapa(result.getAbsolutePath());
              btnTermino.setVisibility(View.VISIBLE);
              snack.showSnackBar(DownloadManager.this, "Descarga completa");
            }
          });

    } catch (Exception e) {
      String mensaje = e.getLocalizedMessage();
      if (mensaje != null) {

        snack.showSnackBar(DownloadManager.this, mensaje);
      }

    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FOLDER_CODE && resultCode == RESULT_OK) {
      if (data != null) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
          String folder = bundle.getString("data");
          etCarpetaDescarga.setText(folder);
        }

      }

    }
    if (requestCode == CODE_PERMISSION_STORAGE) {
      if (resultCode == RESULT_OK) {
        recreate();
      } else {
        if (!snackbar.isShown()) {
          snackbar.show();
        }

      }
    }

  }

  private void parar() {
    Ion.getDefault(DownloadManager.this).cancelAll(this);
  }
}
