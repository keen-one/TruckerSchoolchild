package softnecessary.furgonok.dao.users;

import android.os.StrictMode;
import com.google.gson.Gson;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Retrofit;
import softnecessary.furgonok.PantallaInicial;
import softnecessary.furgonok.managedb.ApiClient;
import softnecessary.furgonok.managedb.ConductorInterface;
import softnecessary.furgonok.pojo.serialized.Conductor;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesConductor;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.pojo.serialized.MyLocation;
import softnecessary.furgonok.pojo.serialized.RespConductor;
import softnecessary.furgonok.pojo.serialized.RespLocalizacion;
import softnecessary.furgonok.utils.Encryption2;


public final class ConductorDAO {


  private ConductorInterface condInterface;
  private Encryption2 encryption;


  private Gson gson = new Gson();


  public ConductorDAO(String huella, String keyPass) {
    ApiClient api = new ApiClient(PantallaInicial.CONDUCTOR, keyPass);
    Retrofit retrofit = api.getRetrofit(huella);
    if (retrofit != null) {
      condInterface = retrofit.create(ConductorInterface.class);
    }

    encryption = new Encryption2();
    /*encryption = new Encriptacion(context.getString(R.string.key_encrypt),context.getString(R.string.salt_encrypt),context.getString(R.string.iv_encrypt).getBytes(
        StandardCharsets.UTF_8));*/
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
  }


  public final Object deleteByCorreoConductor(Integer id, String authToken, String email,
      String contrasena) {
    if (condInterface == null) {
      return new Object();
    }
    Object objeto;
    String email2 = encryption.encriptarDato(email);
    String password2 = encryption.encriptarDato(contrasena);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Call<String> call = condInterface.deleteByCorreo(id, authToken, email2, password2, email2);
    try {
      String respuesta = call.execute().body();
      objeto = getConductor(respuesta);
    } catch (IOException e) {
      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;
  }

  public final Mensaje isExistUsername(String username, boolean defecto) {
    if (condInterface == null) {
      Mensaje mensaje = new Mensaje();
      mensaje.setMsg("Desconexión con el servidor principal");
      mensaje.setState(defecto);
      return mensaje;
    }
    String username1 = encryption.encriptarDato(username);
    Call<String> call = condInterface.existeUsername(username1);
    Mensaje mensaje;
    try {
      String respuesta = call.execute().body();
      mensaje = gson.fromJson(respuesta, Mensaje.class);
    } catch (Exception t) {

      mensaje = new Mensaje();
      mensaje.setState(defecto);
      mensaje.setMsg(t.getLocalizedMessage());
      return mensaje;
    }
    if (mensaje == null) {
      mensaje = new Mensaje();
      mensaje.setState(defecto);
    }
    return mensaje;
  }


  private Object getConductor(String respuesta) {
    Object objeto;
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);

    try {
      mensaje = gson.fromJson(respuesta, Mensaje.class);

    } catch (Exception a) {
      mensaje = new Mensaje();
    }
    if (mensaje == null) {
      mensaje = new Mensaje();
    }
    if (mensaje.getMsg().equals("")) {
      mensaje.setState(false);
      mensaje.setMsg("Petición denegada");
      try {
        objeto = gson
            .fromJson(respuesta, RespConductor.class);
      } catch (Exception a) {
        mensaje = new Mensaje();
        mensaje.setState(false);
        mensaje.setMsg("Petición denegada");
        objeto = mensaje;
      }
    } else {
      objeto = mensaje;
    }

    if (objeto instanceof RespConductor) {
      if (!((RespConductor) objeto).getConductor().getCorreo().equals("")) {
        objeto = encryption
            .decriptarDataConductor(((RespConductor) objeto).getConductor());
      } else {
        objeto = mensaje;
      }

    }

    return objeto;
  }

  public final Object loggear(String correo, String password) {
    if (condInterface == null) {
      return new Object();
    }
    String correoNew = encryption.encriptarDato(correo);
    String passwordNew = encryption.encriptarDato(password);
    Call<String> call = condInterface.logear(correoNew, passwordNew);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getConductor(respuesta);
    } catch (Exception e) {

      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;

    /*call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          Object respuesta = response.body();

          String respuestaString = respuesta.toString();
          try {
            mensaje = gson.fromJson(respuestaString, Mensaje.class);
            objeto = mensaje;
          } catch (Exception a) {
            mensaje = null;
          }
          if (mensaje == null) {
            try {
              respApod = gson
                  .fromJson(respuestaString, RespConductor.class);
              objeto = respApod;
            } catch (Exception a) {
              objeto = new Object();
            }
          }

          if (objeto instanceof RespConductor) {
            Conductor conductor = encryption
                .decriptarDataConductor(((RespConductor) objeto).getConductor());
            objeto = conductor;
          }
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        mensaje = new Mensaje();
        mensaje.setState(false);
        mensaje.setMsg(t.getMessage());
        objeto = mensaje;
      }
    });
    return objeto;*/
  }

  public final Object getConductorByCorreo(String correo, String email, String contrasena,
      String authToken, int id) {
    if (condInterface == null) {
      return new Object();
    }
    String correo1 = encryption.encriptarDato(correo);
    String email1 = encryption.encriptarDato(email);
    String contrasena1 = encryption.encriptarDato(contrasena);
    Call<String> call = condInterface
        .getByCorreo(id, authToken, email1, contrasena1, correo1);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getConductor(respuesta);
    } catch (Exception e) {

      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;
    /*call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call,
          Response<String> response) {
        if (response.isSuccessful()) {
          Object respuesta = response.body();

          String respuestaString = respuesta.toString();
          try {
            mensaje = gson.fromJson(respuestaString, Mensaje.class);
            objeto = mensaje;
          } catch (Exception a) {
            mensaje = null;
          }
          if (mensaje == null) {
            try {
              respApod = gson
                  .fromJson(respuestaString, RespConductor.class);
              objeto = respApod;
            } catch (Exception a) {
              objeto = new Object();
            }
          }

          if (objeto instanceof RespConductor) {
            Conductor conductor = encryption
                .decriptarDataConductor(((RespConductor) objeto).getConductor());
            objeto = conductor;
          }
        }
      }

      @Override
      public void onFailure(Call<String> call,
          Throwable t) {
        mensaje = new Mensaje();
        mensaje.setMsg(t.getLocalizedMessage());
        mensaje.setState(false);
        objeto = new Object();
        objeto = mensaje;
      }
    });
    return objeto;*/
  }

  public final Object updateConductor(String email, String contrasena,
      String authToken, int id, FieldUpdatesConductor fieldConductor) {
    if (condInterface == null) {
      return new Object();
    }
    String email2 = encryption.encriptarDato(email);
    String contrasena2 = encryption.encriptarDato(contrasena);
    FieldUpdatesConductor newFieldConductor = encryption
        .encriptarFieldUpdateConductor(fieldConductor);
    Call<String> call = condInterface
        .update(id, authToken, email2, contrasena2, email2, newFieldConductor.getLatitud(),
            newFieldConductor.getLongitud(), newFieldConductor.getPassword());
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getConductor(respuesta);
    } catch (Exception e) {
      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;

  }

  private Object getResultadoLocation(String resultado) {
    Object objeto;
    Mensaje mensaje = new Mensaje();
    try {
      mensaje = gson.fromJson(resultado, Mensaje.class);
    } catch (Exception e) {
      mensaje.setMsg("");
      mensaje.setState(false);
    }
    if (mensaje == null) {
      mensaje = new Mensaje();
    }
    if (mensaje.getMsg().equals("")) {
      RespLocalizacion respLoc = new RespLocalizacion();
      try {
        respLoc = gson.fromJson(resultado, RespLocalizacion.class);
      } catch (Exception e) {
        mensaje.setMsg("");
        mensaje.setState(false);
      }
      if (respLoc == null) {
        respLoc = new RespLocalizacion();
      }
      MyLocation miLocation = respLoc.getData();
      if (respLoc.getData().getNombreUsuario().equals("")) {
        objeto = mensaje;
      } else {
        objeto = encryption.decriptarLocation(miLocation);
      }
    } else {
      objeto = mensaje;
    }
    return objeto;

  }

  public final Object getLocalizacion(int id, String authToken,
      String username) {
    if (condInterface == null) {
      return new Object();
    }
    Object objeto;
    Mensaje mensaje = new Mensaje();
    String username1 = encryption.encriptarDato(username);
    Call<String> call = condInterface.getLocalizacion(id, authToken, username1);
    try {
      String resultado = call.execute().body();
      objeto = getResultadoLocation(resultado);
    } catch (Exception e) {
      mensaje.setMsg(e.getLocalizedMessage());
      mensaje.setState(false);
      objeto = mensaje;
    }
    return objeto;
  }
  /*
  public Object getApodoByUsername(int id, String authToken, String username) {
    String username1 = encryption.encriptarDato(username);

    Call<String> call = condInterface
        .getApodoByUsername(id, authToken, username1);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = stringToObjectApodo(respuesta);
    } catch (Exception e) {

      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;
  }

  private Object stringToObjectApodo(String resultado) {
    Mensaje mensaje = new Mensaje();
    Object objeto;
    try {
      mensaje = gson.fromJson(resultado, Mensaje.class);
    } catch (Exception e) {
      mensaje.setMsg("");
      mensaje.setState(false);

    }
    if (mensaje == null) {
      mensaje = new Mensaje();
    }
    if (mensaje.getMsg().equals("")) {
      RespUsernameApodo respUser;
      try {
        respUser = gson.fromJson(resultado, RespUsernameApodo.class);
      } catch (Exception e) {
        respUser = new RespUsernameApodo();
      }
      if (respUser == null) {
        respUser = new RespUsernameApodo();
      }
      MiApodo miApodo = respUser.getData();
      if (!miApodo.getApodo().equals("")) {
        objeto = encryption.decriptarMiApodo(miApodo);
      } else {
        objeto = mensaje;
      }
    } else {
      objeto = mensaje;
    }
    return objeto;
  }*/


  public final Object insertarDataConductor(Conductor objNewConductor) {
    if (condInterface == null) {
      return new Object();
    }
    Conductor apod = encryption.encriptarDataConductor(objNewConductor);
    Call<String> call = condInterface
        .store(apod.getApodo(), apod.getCorreo(), apod.getNombreUsuario(),
            apod.getFechaNac(), apod.getPassword());
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getConductor(respuesta);
    } catch (Exception e) {

      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;
    /*try {
      Object respuesta = call.execute().body();
      String respuestaString = respuesta.toString();
      try {
        mensaje = gson.fromJson(respuestaString, Mensaje.class);
        objeto = mensaje;
      } catch (Exception a) {
        mensaje = null;
      }
      if (mensaje == null) {
        try {
          respApod = gson
              .fromJson(respuestaString, RespConductor.class);
          objeto = respApod;
        } catch (Exception a) {
          objeto = new Object();
        }
      }

      if (objeto instanceof RespConductor) {
        Conductor conductor = encryption
            .decriptarDataConductor(((RespConductor) objeto).getConductor());
        objeto = conductor;
      }
    } catch (Exception e) {
      return new Object();
    }
    return objeto;
    /*
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call,
          Response<String> response) {
        if (response.isSuccessful()) {
          Object respuesta = response.body();

          String respuestaString = respuesta.toString();
          try {
            mensaje = gson.fromJson(respuestaString, Mensaje.class);
            objeto = mensaje;
          } catch (Exception a) {
            mensaje = null;
          }
          if (mensaje == null) {
            try {
              respApod = gson
                  .fromJson(respuestaString, RespConductor.class);
              objeto = respApod;
            } catch (Exception a) {
              objeto = new Object();
            }
          }

          if (objeto instanceof RespConductor) {
            Conductor conductor = encryption
                .decriptarDataConductor(((RespConductor) objeto).getConductor());
            objeto = conductor;
          }
        }
      }

      @Override
      public void onFailure(Call<String> call,
          Throwable t) {
        mensaje = new Mensaje();
        mensaje.setMsg(t.getLocalizedMessage());
        mensaje.setState(false);
        objeto = new Object();
        objeto = mensaje;
      }
    });
    return objeto;*/
  }


}
