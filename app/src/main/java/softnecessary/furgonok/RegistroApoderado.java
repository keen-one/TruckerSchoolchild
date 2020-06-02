package softnecessary.furgonok;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import softnecessary.furgonok.dao.users.ApoderadoDAO;
import softnecessary.furgonok.pojo.serialized.Apoderado;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.SimpleEula;
import softnecessary.furgonok.utils.SnackBarUtils;
import softnecessary.furgonok.utils.Utilidades;

public class RegistroApoderado extends AppCompatActivity implements
    OnDateSetListener {

  //implements Google.GoogleResponseListener
  //private static final int CARNET_REQUEST_CODE = 0;
  //private static final int CURRENT_PHOTO_REQUEST_CODE = 1;
  private EditText etCorreo;
  private EditText etFechaNac;
  private EditText etApodo;
  //private File carpetaCompare = null;
  //private String fileNameCurrentPhoto = "";
  //private boolean bandera = false;
  private Button btnRegistrar;
  private EditText etPassword;
  private AwesomeValidation awesomeVal;
  //private File archivo = null;

  //private Google google;
  private MisPreferencias pref;
  private SnackBarUtils snackBarUtils;
  //private String nombreArchivoCarnet = "";
  //private String nombreArchivoFotoActual = "";

  /*
  private static Uri getImageUri(Context inContext, Bitmap inImage) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    //MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
    String path = Utilidades.insertImage(inContext.getContentResolver(), inImage, "Title", null);
    return Uri.parse(path);
  }*/

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
    setContentView(R.layout.activity_registro_apoderado);
    snackBarUtils = SnackBarUtils.getInstance();
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.registro_apoderado);
    ActionBar actionbar = getSupportActionBar();
    if (actionbar != null) {
      actionbar.setDisplayHomeAsUpEnabled(true);
    }
    pref = new MisPreferencias(RegistroApoderado.this);

    //AlertDialog.Builder dialog2 = new AlertDialog.Builder(RegistroApoderado.this);
    //dialog2.setTitle("Toma la foto");
    /*carpetaCompare = getExternalFilesDir(
        Environment.DIRECTORY_PICTURES + File.separator + "verify_apoderado" + File.separator);*/
    etPassword = findViewById(R.id.caja_password);
    etCorreo = findViewById(R.id.caja_correo);
    etCorreo.setKeyListener(null);
    if (MiAplicacion.MI_LOGIN_EMAIL != null) {
      etCorreo.setText(MiAplicacion.MI_LOGIN_EMAIL);
    }

    etFechaNac = findViewById(R.id.caja_fechaNac);

    etApodo = findViewById(R.id.caja_apodo);
    etFechaNac.setKeyListener(null);
    awesomeVal = new AwesomeValidation(ValidationStyle.BASIC);

    awesomeVal.addValidation(RegistroApoderado.this, R.id.caja_password_confirm, R.id.caja_password,
        R.string.err_password_confirmation);

    awesomeVal
        .addValidation(RegistroApoderado.this, R.id.caja_apodo, "[a-zA-Z\\s]+",
            R.string.error_firstName);
    awesomeVal.addValidation(RegistroApoderado.this, R.id.caja_fechaNac,
        "^\\d{2}-\\d{2}-\\d{4}$",
        R.string.error_fechaNac);
    awesomeVal
        .addValidation(RegistroApoderado.this, R.id.caja_fechaNac, new SimpleCustomValidation() {
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
            },
            R.string.error_fechaNacEdad);
    awesomeVal.addValidation(RegistroApoderado.this, R.id.caja_correo,
        new SimpleCustomValidation() {
          @Override
          public boolean compare(String s) {
            if (s != null && MiAplicacion.MI_LOGIN_EMAIL != null) {
              return MiAplicacion.MI_LOGIN_EMAIL.trim().equals(s.trim());
            }
            return false;
          }
        }, R.string.error_email_login);
    awesomeVal.addValidation(RegistroApoderado.this, R.id.caja_correo,
        Patterns.EMAIL_ADDRESS,
        R.string.error_correo);
    awesomeVal
        .addValidation(RegistroApoderado.this, R.id.caja_password, new SimpleCustomValidation() {
          @Override
          public boolean compare(String s) {
            return s.trim().length() >= 8;
          }
        }, R.string.error_password);
    /*awesomeVal
        .addValidation(RegistroApoderado.this, R.id.caja_correo, new SimpleCustomValidation() {
          @Override
          public boolean compare(String s) {
            return s.trim().contains("@gmail");
          }
        }, R.string.error_correo_verdadero);*/

    btnRegistrar = findViewById(R.id.btn_add_user);

    /*if (pref.loadVerificationByKey(PantallaInicial.ESTADO_APODERADO)) {
      enableBtnRegistro();
    } else {
      disableBtnRegistro();
    }*/
    enableBtnRegistro();
    new SimpleEula(this).show();
  }

  private Calendar getCalendar(Date date) {
    Locale local = new Locale("es", "CL");
    Calendar cal = Calendar.getInstance(local);
    cal.setTime(date);
    return cal;
  }

  private void enableBtnRegistro() {
    btnRegistrar.setEnabled(true);
    btnRegistrar.setBackground(getDrawable(R.drawable.button_background));
  }
  /*
  public void mostrarLoginExclusivo(View view) {
    try {
      google = new Google(RegistroApoderado.this);
      google.login();
    } catch (Exception e) {
      snackBarUtils.showSnackBar(RegistroApoderado.this, "Error. Intente otra vez mas tarde");
    }

  }*/

  public void showDatePicker(View view) {
    Locale local = new Locale("es", "CL");
    Calendar c = Calendar.getInstance(local);

    DatePickerDialog dialog = new DatePickerDialog(RegistroApoderado.this, RegistroApoderado.this,
        c.get(Calendar.YEAR) - 18, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    dialog.show();
  }

  public void registerApoderado(View view) {
    if (awesomeVal.validate()) {

      String correo = etCorreo.getText().toString();
      ApoderadoDAO objDb = new ApoderadoDAO(getString(R.string.huella),
          getString(R.string.key_pass));
      Apoderado objApoderado = new Apoderado();
      objApoderado.setCorreo(correo);
      String apodo = etApodo.getText().toString();
      Utilidades util = new Utilidades();

      String nombreUsuario;

      snackBarUtils.showSnackBar(RegistroApoderado.this, "Comprobando nombre de usuario existente");

      nombreUsuario = util.generateUsername();
      if (BuildConfig.DEBUG) {
        nombreUsuario = "d3d2378223f24afd98d2b47db9210502";
      }
      Mensaje mensaje = objDb
          .isExistUsername(nombreUsuario, true);

      if (mensaje.getState()) {

        snackBarUtils
            .showSnackBar(RegistroApoderado.this,
                mensaje.getMsg());

        return;
      }

      objApoderado.setFechaNac(etFechaNac.getText().toString());
      objApoderado.setNombreUsuario(nombreUsuario);

      objApoderado.setPassword(etPassword.getText().toString());

      objApoderado.setApodo(apodo);

      snackBarUtils
          .showSnackBar(RegistroApoderado.this, "Ingresando el usuario en nuestra base de datos");

      Object objeto = objDb
          .insertarDataApoderado(objApoderado);

      if (objeto instanceof Mensaje) {

        snackBarUtils.showSnackBar(RegistroApoderado.this, ((Mensaje) objeto).getMsg());


      } else if (objeto instanceof Apoderado) {

        snackBarUtils.showSnackBar(RegistroApoderado.this, "Registro exitoso");

        Integer miId = ((Apoderado) objeto).getId();
        String authToken = ((Apoderado) objeto).getAuthToken();
        if (miId != -1 && !authToken.equals("")) {
          pref.saveTokenID(authToken, miId);
          snackBarUtils.showSnackBar(RegistroApoderado.this, "El Apoderado ha sido registrado");
        } else {
          snackBarUtils.showSnackBar(RegistroApoderado.this, "El apoderado no pudo ser registrado");
        }
      } else {
        snackBarUtils.showSnackBar(RegistroApoderado.this,
            "El apoderado no pudo ser registrado por problemas de conexión");
      }


    }
  }
  /*
  public void verificarApoderado(View view) {
    if (awesomeVal.validate()) {
      String nombrePersona = "apoderado";
      nombreArchivoFotoActual = "1-" + nombrePersona + "_1.png";
      nombreArchivoCarnet = "1-" + nombrePersona + "_2.png";
      fileNameCurrentPhoto = nombreArchivoFotoActual;
      AlertDialog.Builder dialog = new AlertDialog.Builder(RegistroApoderado.this);
      dialog.setTitle("Toma la foto");
      LinearLayout linearLayout = new LinearLayout(RegistroApoderado.this);
      linearLayout.setPadding(12, 12, 12, 12);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      LinearLayout linearLayout2 = new LinearLayout(RegistroApoderado.this);
      linearLayout2.setPadding(12, 12, 12, 12);
      linearLayout2.setOrientation(LinearLayout.VERTICAL);
      TextView tvInfo = new TextView(RegistroApoderado.this);
      TextView tvIntro2 = new TextView(RegistroApoderado.this);
      TextView tvIntro = new TextView(RegistroApoderado.this);
      TextView tvInfo2 = new TextView(RegistroApoderado.this);
      tvInfo
          .setTextSize(TypedValue.COMPLEX_UNIT_SP,
              14);
      tvInfo2
          .setTextSize(TypedValue.COMPLEX_UNIT_SP,
              14);
      tvIntro
          .setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      tvIntro2
          .setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      ImageView imgInfo = new ImageView(RegistroApoderado.this);
      ImageView imgInfo2 = new ImageView(RegistroApoderado.this);
      imgInfo.setImageResource(R.drawable.ic_foto_perfil);
      imgInfo2.setImageResource(R.drawable.ic_cedula);
      imgInfo.setScaleType(ScaleType.FIT_XY);
      LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(100,
          100);
      imgInfo.setLayoutParams(param);
      imgInfo2.setScaleType(ScaleType.FIT_XY);
      LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(100,
          75);
      imgInfo2.setLayoutParams(param2);
      tvIntro.setText(
          getString(R.string.info_intro));
      tvInfo.setText(getString(R.string.info_photo_current));

      linearLayout.addView(tvIntro, 0);
      linearLayout.addView(tvInfo, 1);
      linearLayout.addView(imgInfo, 2);
      linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
      dialog.setView(linearLayout);
      tvInfo2.setText(getString(R.string.info_cedula));

      linearLayout2.addView(tvIntro2, 0);
      linearLayout2.addView(tvInfo2, 1);
      linearLayout2.addView(imgInfo2, 2);
      linearLayout2.setGravity(Gravity.CENTER_HORIZONTAL);
      dialog2.setView(linearLayout2);
      OnClickListener clickDialogNeg1 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog14, int which) {
          dialog14.dismiss();
        }
      };
      dialog2.setNegativeButton("Cerrar", clickDialogNeg1);
      OnClickListener clickNeg2 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog13, int which) {
          dialog13.dismiss();
        }
      };
      dialog.setNegativeButton("Cerrar", clickNeg2);
      OnClickListener clickPos1 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog12, int which) {
          tomarFoto(nombreArchivoCarnet, CARNET_REQUEST_CODE);
          dialog12.dismiss();
        }
      };
      dialog2.setPositiveButton("Toma la foto", clickPos1);
      OnClickListener clickPos2 = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog1, int which) {
          tomarFoto(nombreArchivoFotoActual, CURRENT_PHOTO_REQUEST_CODE);
          dialog1.dismiss();
        }
      };
      dialog.setPositiveButton("Toma la foto", clickPos2);

      dialog.show();
    }
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
          .getUriForFile(RegistroApoderado.this, BuildConfig.APPLICATION_ID + ".provider", archivo);
      Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
      intentCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
      startActivityForResult(intentCapture, indice);
    }

  }*/

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    /*if (requestCode == SocialLoginConstant.GOOGLE_REQUEST_CODE) {
      google.activityResult(requestCode, resultCode, data);
    }*/
    //checkPermissionOnActivityResult(requestCode, resultCode, data);
    //etPuntoPartida.setText(place.getName());
    //etLatitud.setText(place.getLatLng().latitude + "");
    //etLongitud.setText(place.getLatLng().longitude + "");
    /*
    if (requestCode == CARNET_REQUEST_CODE && resultCode == RESULT_OK) {
      if (bandera) {

        snackBarUtils.showSnackBar(RegistroApoderado.this, "Verificando a la persona");
        try {
          Runnable runnable = new Runnable() {

            @Override
            public void run() {
              int predict = Utilidades
                  .reconocerCara(RegistroApoderado.this, carpetaCompare.getAbsolutePath(),
                      carpetaCompare
                          .getAbsolutePath() + File.separator + fileNameCurrentPhoto);
              if (predict != -1) {
                if (Utilidades.compareImage(archivo.getAbsolutePath(),
                    getPathFromDrawableCedula())) {

                  snackBarUtils
                      .showSnackBar(RegistroApoderado.this, "Persona verificada exitosamente");
                  Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                      enableBtnRegistro();
                      pref
                          .saveVerificationByKey(PantallaInicial.ESTADO_APODERADO, true);
                    }
                  };
                  runOnUiThread(runnable);

                } else {
                  snackBarUtils.showSnackBar(RegistroApoderado.this,
                      "La fotografia es diferente a una cedula de identidad");
                }
              } else {
                snackBarUtils.showSnackBar(RegistroApoderado.this,
                    "La persona es diferente al actual cedula de identidad");
              }
            }
          };
          Thread thread = new Thread(runnable);
          if(!thread.isAlive()){thread.start();}
        } catch (Exception e) {
          snackBarUtils.showSnackBar(RegistroApoderado.this,
              e.getLocalizedMessage());
        }

      }
      bandera = false;
    } else if (requestCode == CURRENT_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
      bandera = true;
      dialog2.show();
    }
    */
  }

  /*
  private String getPathFromDrawableCedula() {
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cedula);
    // CALL RegistroApoderado.this METHOD TO GET THE URI FROM THE BITMAP
    Uri tempUri = getImageUri(getApplicationContext(), bitmap);

    // CALL RegistroApoderado.this METHOD TO GET THE ACTUAL PATH
    return Utilidades.getRealPathFromURI2(tempUri, RegistroApoderado.this);
  }*/

  /*
  @Override
  public void onGoogleResponseListener(JSONObject response, boolean error) {
    if (!error) {
      try {
        etCorreo.setText(response.getString("email"));
      } catch (Exception e) {
        Utilidades
            .showMsg(RegistroApoderado.this, "Error, Intente registrarse con google mas tarde");
      }
    } else {
      Utilidades.showMsg(RegistroApoderado.this, "Error, Intente registrarse con google mas tarde");
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
