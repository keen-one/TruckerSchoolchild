package softnecessary.furgonok;

import android.Manifest;
import android.Manifest.permission;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import softnecessary.furgonok.dao.users.ConductorDAO;
import softnecessary.furgonok.pojo.serialized.Conductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SimpleEula;
import softnecessary.furgonok.utils.SnackBarUtils;
import softnecessary.furgonok.utils.Utilidades;

//import com.creativemorph.sociallogins.Google;
//import com.creativemorph.sociallogins.SocialLoginConstant;

public class RegistroConductor extends AppCompatActivity implements
    DatePickerDialog.OnDateSetListener {

  //implements Google.GoogleResponseListener
  private static final int LICENCIA_REQUEST_CODE = 0;
  private static final int CURRENT_PHOTO_REQUEST_CODE = 1;
  private static final int CODE_STORAGE = 90;
  private final String[] permisos = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE};
  private String nombreArchivoFotoActual = "";
  private String nombreArchivoLicencia = "";
  private EditText etApodo;
  private EditText etCorreo;
  private EditText etFechaNac;
  private EditText etPassword;

  private Button btnAddUser;
  private AwesomeValidation awesomeVal;
  private File carpetaCompare;
  private String fileNameCurrentPhoto = "";
  private boolean bandera = false;
  private AlertDialog.Builder dialog2;
  private File archivo = null;
  //private Google google;
  private MisPreferencias pref;
  private SnackBarUtils snackBarUtils;
  private Snackbar snackStorage;

  private static Uri getImageUri(Context inContext, Bitmap inImage) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = Utilidades.insertImage(inContext.getContentResolver(), inImage, "Title", null);
    return Uri.parse(path);
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
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int grantResult = grantResults[i];

        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) || permission
            .equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          if (grantResult == PackageManager.PERMISSION_GRANTED) {
            if (snackStorage.isShown()) {
              snackStorage.dismiss();
            }
            recreate();
          } else {
            if (!snackStorage.isShown()) {
              snackStorage.show();
            }
          }
        }
      }
    }
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

    setContentView(R.layout.activity_registro_chofer);
    snackBarUtils = SnackBarUtils.getInstance();
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.registro_conductor_furgon_escolar);
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }

    pref = new MisPreferencias(RegistroConductor.this);
    View.OnClickListener clickStorage = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityCompat.requestPermissions(RegistroConductor.this, permisos, CODE_STORAGE);
      }
    };
    snackStorage = Snackbar.make(findViewById(android.R.id.content), "Permiso de almacenamiento",
        BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("PERMITIR", clickStorage);
    // Places.initialize(getApplicationContext(), getString(R.string.api_key_place_sdk));
    dialog2 = new AlertDialog.Builder(RegistroConductor.this);
    dialog2.setTitle("Toma la foto");
    carpetaCompare = getExternalFilesDir(
        Environment.DIRECTORY_PICTURES + File.separator + "verify_conductor" + File.separator);
    awesomeVal = new AwesomeValidation(ValidationStyle.BASIC);
    etApodo = findViewById(R.id.etApodo);
    etCorreo = findViewById(R.id.etCorreo);
    etCorreo.setKeyListener(null);
    if (MiAplicacion.MI_LOGIN_EMAIL != null) {
      etCorreo.setText(MiAplicacion.MI_LOGIN_EMAIL);
    }
    etFechaNac = findViewById(R.id.etFechaNac);
    etPassword = findViewById(R.id.etPassword);

    etFechaNac.setKeyListener(null);

    awesomeVal.addValidation(RegistroConductor.this, R.id.etApodo, "[a-zA-Z\\s]+",
        R.string.error_firstName);
    awesomeVal.addValidation(RegistroConductor.this, R.id.etPassword, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return !s.trim().isEmpty() && s.trim().length() >= 8;
      }
    }, R.string.error_password);
    awesomeVal.addValidation(RegistroConductor.this, R.id.etPassword_confirm, R.id.etPassword,
        R.string.err_password_confirmation);
    awesomeVal.addValidation(RegistroConductor.this, R.id.etCorreo, Patterns.EMAIL_ADDRESS,
        R.string.error_correo);

    awesomeVal.addValidation(RegistroConductor.this, R.id.etCorreo,
        new SimpleCustomValidation() {
          @Override
          public boolean compare(String s) {
            if (s != null && MiAplicacion.MI_LOGIN_EMAIL != null) {
              return MiAplicacion.MI_LOGIN_EMAIL.trim().equals(s.trim());
            }
            return false;
          }
        }, R.string.error_email_login);

    awesomeVal.addValidation(RegistroConductor.this, R.id.etFechaNac,
        "^\\d{2}-\\d{2}-\\d{4}$", R.string.error_fechaNac);
    awesomeVal.addValidation(RegistroConductor.this, R.id.etFechaNac, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        Locale local = new Locale("es", "CL");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", local);
        try {
          Date first = format.parse(s);
          Calendar a = getCalendar(first);
          Calendar b = Calendar.getInstance(local);
          int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
          if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
              (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b
                  .get(Calendar.DATE))) {
            diff--;
          }

          return diff >= 18;
        } catch (Exception e) {
          return false;
        }
      }
    }, R.string.error_correo_verdadero);
    /*awesomeVal.addValidation(RegistroConductor.this, R.id.etCorreo, new SimpleCustomValidation() {
      @Override
      public boolean compare(String s) {
        return s.trim().contains("@gmail");
      }
    }, R.string.error_correo_verdadero);*/

    btnAddUser = findViewById(R.id.btn_add_user);

    //if (!BuildConfig.DEBUG) {
    if (pref.loadVerificationByKey(PantallaInicial.ESTADO_CONDUCTOR)) {
      enableBtnRegistro();
    } else {
      disableBtnRegistro();
    }
    //}
    /*if (BuildConfig.DEBUG) {
      enableBtnRegistro();
      etCorreo.setText("r.leal01@gmail.com");
    }*/
    new SimpleEula(this).show();
  }

  private Calendar getCalendar(Date date) {
    Locale local = new Locale("es", "CL");
    Calendar cal = Calendar.getInstance(local);
    cal.setTime(date);
    return cal;
  }

  private void enableBtnRegistro() {
    btnAddUser.setEnabled(true);
    btnAddUser.setBackground(getDrawable(R.drawable.button_background));
  }

  private void disableBtnRegistro() {
    btnAddUser.setEnabled(false);
    btnAddUser.setBackground(getDrawable(R.drawable.button_background_gray));
  }

  public void ingresarChofer(View view) {
    if (awesomeVal.validate()) {
      ingresarDatos();
    }
  }

  public void showDateDialog(View view) {
    Locale local = new Locale("es", "CL");
    Calendar c = Calendar.getInstance(local);
    DatePickerDialog dialog = new DatePickerDialog(RegistroConductor.this, RegistroConductor.this,
        c.get(Calendar.YEAR) - 18,
        1, 1);

    dialog.show();
  }

  private void tomarFoto(String nameFile, int indice) {

    boolean success = true;
    if (!carpetaCompare.exists()) {
      success = carpetaCompare.mkdirs();
    }
    if (success) {
      archivo = new File(carpetaCompare, nameFile);

      //Uri uri = Uri.fromFile(archivo);
      Uri uri = FileProvider
          .getUriForFile(RegistroConductor.this, BuildConfig.APPLICATION_ID + ".provider", archivo);
      Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
      intentCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
      startActivityForResult(intentCapture, indice);
    }

  }


  private String getPathFromDrawableLicenciaNew() {
    Bitmap bitmap = BitmapFactory
        .decodeResource(getResources(), R.drawable.ic_licencia_conducir_new);
    //Bitmap bitmapNew=Bitmap.createScaledBitmap(bitmap,width, height,false);
    // CALL RegistroConductor.this METHOD TO GET THE URI FROM THE BITMAP
    Uri tempUri = getImageUri(RegistroConductor.this, bitmap);

    // CALL RegistroConductor.this METHOD TO GET THE ACTUAL PATH
    return Utilidades.getRealPathFromURI2(tempUri, RegistroConductor.this);
  }

  private String getPathFromDrawableLicenciaOld() {
    Bitmap bitmap = BitmapFactory
        .decodeResource(getResources(), R.drawable.ic_licencia_conductor_old);
    //Bitmap bitmapNew=Bitmap.createScaledBitmap(bitmap,width,height,false);
    // CALL RegistroConductor.this METHOD TO GET THE URI FROM THE BITMAP
    Uri tempUri = getImageUri(RegistroConductor.this, bitmap);

    // CALL RegistroConductor.this METHOD TO GET THE ACTUAL PATH
    return Utilidades.getRealPathFromURI2(tempUri, RegistroConductor.this);
  }


  private void ingresarDatos() {

    ConductorDAO conductorDAO = new ConductorDAO(
        getString(R.string.huella), getString(R.string.key_pass));
    Conductor objConductor = new Conductor();
    objConductor.setCorreo(etCorreo.getText().toString());
    objConductor.setFechaNac(etFechaNac.getText().toString());

    Utilidades util = new Utilidades();
    String apodo = etApodo.getText().toString();
    String nombreUsuario;

    nombreUsuario = util.generateUsername();
    if (BuildConfig.DEBUG) {
      nombreUsuario = "8cdd605c609a4c32b9dd76eee31a9ff7";
    }
    Mensaje mensaje = conductorDAO.isExistUsername(nombreUsuario, true);
    if (mensaje.getState()) {

      snackBarUtils.showSnackBar(RegistroConductor.this,
          mensaje.getMsg());
      return;

    }

    objConductor.setNombreUsuario(nombreUsuario);

    objConductor.setPassword(etPassword.getText().toString());
    objConductor.setApodo(apodo);

    Object objeto = conductorDAO.insertarDataConductor(objConductor);
    if (objeto instanceof Mensaje) {

      snackBarUtils.showSnackBar(RegistroConductor.this, ((Mensaje) objeto).getMsg());


    } else if (objeto instanceof Conductor) {

      Integer miId = ((Conductor) objeto).getId();
      String authToken = ((Conductor) objeto).getAuthToken();
      String email = ((Conductor) objeto).getCorreo();
      String username = ((Conductor) objeto).getNombreUsuario();
      String password = ((Conductor) objeto).getPassword();
      if (miId != -1 && !authToken.equals("") && !email.equals("") && !username.equals("")
          && !password.equals("")) {
        pref.saveTokenID(authToken, miId);
        pref.guardarCredentialsLogin(email, password);
        pref.guardarNombreUsuario(username);
        snackBarUtils.showSnackBar(RegistroConductor.this, "El conductor ha sido registrado"
        );
        //startActivity(new Intent(RegistroConductor.this, PantallaInicial.class));

      } else {
        snackBarUtils.showSnackBar(RegistroConductor.this, "El conductor no pudo ser registrado");

      }

    } else {
      snackBarUtils.showSnackBar(RegistroConductor.this,
          "Conexión fallida"
      );

    }
  }
  /*
  public void mostrarLoginExclusivo(View view) {
    try {
      google = new Google(RegistroConductor.this);
      google.login();
    } catch (Exception e) {
      snackBarUtils.showSnackBar(RegistroConductor.this, "Error. Actualiza la pantalla registro");
    }

  }*/

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    /*if (requestCode == SocialLoginConstant.GOOGLE_REQUEST_CODE) {
      google.activityResult(requestCode, resultCode, data);
    }*/

    if (requestCode == LICENCIA_REQUEST_CODE && resultCode == RESULT_OK) {
      if (bandera) {
        snackBarUtils.showSnackBar(RegistroConductor.this, "Verificando persona"
        );
        if (ActivityCompat
            .checkSelfPermission(RegistroConductor.this, permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
            .checkSelfPermission(RegistroConductor.this, permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          if (!snackStorage.isShown()) {
            snackStorage.show();
          }
        } else {
          if (snackStorage.isShown()) {
            snackStorage.dismiss();
          }
          Runnable runnable = new Runnable() {

            @Override
            public void run() {
              try {
                int predict = Utilidades
                    .reconocerCara(RegistroConductor.this, carpetaCompare.getAbsolutePath(),
                        fileNameCurrentPhoto);
                if (predict != -1) {
                  if (archivo != null) {

                    Bitmap bitmapOri = BitmapFactory.decodeFile(archivo.getPath());
                    //final Bitmap bmp = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
                    int width = 640;
                    int height = 480;
                    Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmapOri, width, height, false);
                    String texto;

                    Bitmap bitmap2 = Utilidades.toGrayscale(bitmap1);

                    texto = Utilidades
                        .getTextFromImage2(RegistroConductor.this,
                            bitmap2);


                    /*Bitmap bitmap2 = Utilidades.rotacion(bitmap1);
                    for (int cont = 0; cont < 4; cont++) {
                      if (bitmap2 != null) {
                        texto = Utilidades
                            .getTextFromImage2(RegistroConductor.this,
                                bitmap2);
                        if (!texto.trim().equals("")) {
                          break;
                        }
                        bitmap2 = Utilidades.rotacion(bitmap2);
                      }


                    }
                    */

                    if (!texto.trim().equals("")) {
                      if (texto.trim().contains("A3") || texto.trim().contains("Al") || texto
                          .trim().contains("A1")) {
                        if (BuildConfig.DEBUG) {
                          archivo = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                              "1-conductor_2.png");
                        }
                        if (Utilidades.compareImage(archivo.getAbsolutePath(),
                            getPathFromDrawableLicenciaOld()) || Utilidades
                            .compareImage(archivo.getAbsolutePath(),
                                getPathFromDrawableLicenciaNew())) {
                          Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                              pref
                                  .saveVerificationByKey(PantallaInicial.ESTADO_CONDUCTOR, true);
                              enableBtnRegistro();
                            }
                          };
                          runOnUiThread(runnable);
                          snackBarUtils.showSnackBar(RegistroConductor.this,
                              "Persona verificada exitosamente"
                          );
                        } else {
                          snackBarUtils.showSnackBar(RegistroConductor.this,
                              "La actual foto no pertenece a una licencia de conducir");
                        }
                      } else {
                        snackBarUtils.showSnackBar(RegistroConductor.this,
                            "La licencia de conducir no pertenece a una clase valida (A3 o A1)"
                        );
                      }
                    } else {
                      snackBarUtils.showSnackBar(RegistroConductor.this,
                          "Texto de la licencia de conducir no valido"
                      );
                    }

                  }

                } else {
                  snackBarUtils.showSnackBar(RegistroConductor.this,
                      "La persona es diferente al actual licencia de conducir"
                  );
                }
              } catch (Exception e) {

                snackBarUtils.showSnackBar(RegistroConductor.this,
                    e.getLocalizedMessage()
                );
              }
            }
          };
          Thread thread = new Thread(runnable);
          if (!thread.isAlive()) {
            thread.start();
          }
        }


      }
      bandera = false;
    } else if (requestCode == CURRENT_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
      bandera = true;
      dialog2.show();
    }

  }


  public void verificarChofer(View view) {
    if (ActivityCompat.checkSelfPermission(RegistroConductor.this, permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat
        .checkSelfPermission(RegistroConductor.this, permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      if (!snackStorage.isShown()) {
        snackStorage.show();
      }

    } else {
      if (snackStorage.isShown()) {
        snackStorage.dismiss();
      }

      String nombrePersona = "conductor";
      nombreArchivoFotoActual = "1-" + nombrePersona + "_1.png";
      nombreArchivoLicencia = "1-" + nombrePersona + "_2.png";
      fileNameCurrentPhoto = nombreArchivoFotoActual;
      AlertDialog.Builder dialog = new AlertDialog.Builder(RegistroConductor.this);
      dialog.setTitle("Toma la foto");
      LinearLayout linearLayout = new LinearLayout(RegistroConductor.this);
      LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      linearLayout.setLayoutParams(linearParam);
      linearLayout.setGravity(Gravity.CLIP_VERTICAL);
      linearLayout.setPadding(20, 20, 20, 20);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      LinearLayout linearLayout2 = new LinearLayout(RegistroConductor.this);
      LinearLayout.LayoutParams linearParam2 = new LinearLayout.LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      linearLayout2.setLayoutParams(linearParam2);
      linearLayout2.setGravity(Gravity.CLIP_VERTICAL);
      linearLayout2.setPadding(20, 20, 20, 20);
      linearLayout2.setOrientation(LinearLayout.VERTICAL);
      TextView tvInfo = new TextView(RegistroConductor.this);
      TextView tvIntro = new TextView(RegistroConductor.this);
      TextView tvInfo2 = new TextView(RegistroConductor.this);
      TextView tvIntro2 = new TextView(RegistroConductor.this);
      ImageView imgInfo = new ImageView(RegistroConductor.this);
      ImageView imgInfo2 = new ImageView(RegistroConductor.this);
      imgInfo2.setImageResource(R.drawable.ic_licencia_conducir_new);
      imgInfo.setImageResource(R.drawable.ic_foto_perfil);
      imgInfo.setPadding(15, 15, 15, 2);
      imgInfo2.setPadding(15, 15, 15, 2);
      imgInfo.setForegroundGravity(Gravity.CENTER);
      imgInfo2.setForegroundGravity(Gravity.CENTER);
      /*LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT);
      LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT);*/
      //imgInfo.setLayoutParams(param1);
      //imgInfo2.setLayoutParams(param2);

      tvInfo
          .setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      tvInfo.setPadding(15, 15, 15, 2);
      tvInfo.setGravity(Gravity.CENTER);
      tvIntro
          .setTextSize(TypedValue.COMPLEX_UNIT_SP,
              14);
      tvIntro.setGravity(Gravity.CENTER);
      tvIntro.setPadding(15, 15, 15, 2);
      tvInfo2
          .setTextSize(TypedValue.COMPLEX_UNIT_SP,
              14);
      tvInfo2.setGravity(Gravity.CENTER);
      tvInfo2.setPadding(15, 15, 15, 2);
      tvIntro2
          .setTextSize(TypedValue.COMPLEX_UNIT_SP,
              14);
      tvIntro2.setGravity(Gravity.CENTER);
      tvIntro2.setPadding(15, 15, 15, 2);
      tvIntro.setText(
          getString(R.string.info_intro));
      tvInfo.setText(getString(R.string.info_photo_current));
      LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
          110);
      LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
          85);
      LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(100,
          100);

      LinearLayout.LayoutParams param4 = new LinearLayout.LayoutParams(100, 75);
      imgInfo.setLayoutParams(param3);
      imgInfo.setScaleType(ScaleType.FIT_CENTER);
      imgInfo2.setLayoutParams(param4);
      imgInfo2.setScaleType(ScaleType.FIT_CENTER);
      linearLayout.addView(tvIntro, 0);
      linearLayout.addView(tvInfo, 1);
      linearLayout.addView(imgInfo, 2, param1);
      linearLayout.setGravity(Gravity.CLIP_VERTICAL);
      dialog.setView(linearLayout);
      tvInfo2.setText(getString(R.string.info_licencia_conducir));

      linearLayout2.addView(tvIntro2, 0);
      linearLayout2.addView(tvInfo2, 1);
      linearLayout2.addView(imgInfo2, 2, param2);
      linearLayout2.setGravity(Gravity.CLIP_VERTICAL);
      dialog2.setView(linearLayout2);
      OnClickListener clickPos1 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog14, int which) {
          tomarFoto(nombreArchivoLicencia, LICENCIA_REQUEST_CODE);
          dialog14.dismiss();
        }
      };
      dialog2.setPositiveButton("Tomar la foto", clickPos1);
      OnClickListener clickPos2 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog13, int which) {
          tomarFoto(nombreArchivoFotoActual, CURRENT_PHOTO_REQUEST_CODE);
          dialog13.dismiss();
        }
      };
      dialog.setPositiveButton("Tomar la foto", clickPos2);
      OnClickListener clickNeg1 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog12, int which) {
          dialog12.dismiss();
        }
      };
      dialog.setNegativeButton("Cerrar", clickNeg1);
      OnClickListener clickNeg2 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog1, int which) {
          dialog1.dismiss();
        }
      };
      dialog2.setNegativeButton("Cerrar", clickNeg2);

      dialog.show();

    }

  }

  /*@Override
  public void onGoogleResponseListener(JSONObject response, boolean error) {
    if (!error) {
      try {
        etCorreo.setText(response.getString("email"));
      } catch (JSONException ignored) {

      }
    }
  }*/

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    Locale local = new Locale("es", "CL");
    Calendar c = Calendar.getInstance(local);
    c.set(Calendar.YEAR, year);
    c.set(Calendar.MONTH, month + 1);
    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy", local);
    String dia = formato.format(c.getTime());
    etFechaNac.setText(dia);
  }
}
