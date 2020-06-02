package softnecessary.furgonok.dao.users;

import android.os.StrictMode;
import com.google.gson.Gson;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Retrofit;
import softnecessary.furgonok.PantallaInicial;
import softnecessary.furgonok.managedb.ApiClient;
import softnecessary.furgonok.managedb.ApoderadoInterface;
import softnecessary.furgonok.pojo.serialized.Apoderado;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesApoderado;
import softnecessary.furgonok.pojo.serialized.Mensaje;
import softnecessary.furgonok.pojo.serialized.MiPartidaApoderado;
import softnecessary.furgonok.pojo.serialized.RespApoderado;
import softnecessary.furgonok.pojo.serialized.RespPuntoPartidaApoderado;
import softnecessary.furgonok.utils.Encryption2;


public final class ApoderadoDAO {


  private ApoderadoInterface apodInterface;
  private Encryption2 encryption;


  private Gson gson = new Gson();


  public ApoderadoDAO(String huella, String keyPass) {
    ApiClient api = new ApiClient(PantallaInicial.APODERADO, keyPass);
    Retrofit retrofit = api.getRetrofit(huella);
    if (retrofit != null) {
      apodInterface = retrofit.create(ApoderadoInterface.class);
    }

    encryption = new Encryption2();
    /*encryption = new Encriptacion(context.getString(R.string.key_encrypt),context.getString(R.string.salt_encrypt),context.getString(R.string.iv_encrypt).getBytes(
        StandardCharsets.UTF_8));*/
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
  }

  private Object getResultadoPuntoPartida(String resultado) {
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
      RespPuntoPartidaApoderado respLoc = new RespPuntoPartidaApoderado();
      try {
        respLoc = gson.fromJson(resultado, RespPuntoPartidaApoderado.class);
      } catch (Exception e) {
        mensaje.setMsg("");
        mensaje.setState(false);
      }
      if (respLoc == null) {
        respLoc = new RespPuntoPartidaApoderado();
      }
      MiPartidaApoderado miLocation = respLoc.getData();
      if (respLoc.getData().getNombreUsuario().equals("")) {
        objeto = mensaje;
      } else {
        objeto = encryption.decriptarMiPuntoPartidaApoderado(miLocation);
      }
    } else {
      objeto = mensaje;
    }
    return objeto;

  }

  public final Object getPuntoPartidaFinalByUsername(int id, String authToken,
      String username) {
    if (apodInterface == null) {
      return new Object();
    }
    String username2 = encryption.encriptarDato(username);
    Object objeto;
    Mensaje mensaje = new Mensaje();
    Call<String> call = apodInterface
        .getPuntoPartidaFinalByUsername(id, authToken, username2);
    try {
      String resultado = call.execute().body();
      objeto = getResultadoPuntoPartida(resultado);
    } catch (Exception e) {
      String miMensaje = e.getLocalizedMessage();
      if (miMensaje != null) {
        mensaje.setMsg(miMensaje);
      }
      mensaje.setState(false);
      objeto = mensaje;
    }
    return objeto;
  }


  public final Object deleteByCorreoApoderado(Integer id, String authToken, String email,
      String contrasena) {
    if (apodInterface == null) {
      return new Object();
    }
    Object objeto;
    String email2 = encryption.encriptarDato(email);
    String password2 = encryption.encriptarDato(contrasena);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Call<String> call = apodInterface.deleteByCorreo(id, authToken, email2, password2, email2);
    try {
      String respuesta = call.execute().body();
      objeto = getApoderado(respuesta);
    } catch (IOException e) {
      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;
  }

  public final Mensaje isExistUsername(String username, boolean defecto) {
    if (apodInterface == null) {
      Mensaje mensaje = new Mensaje();
      mensaje.setMsg("Desconexión con el servidor principal");
      mensaje.setState(defecto);
      return mensaje;
    }
    String username1 = encryption.encriptarDato(username);
    Call<String> call = apodInterface.existeUsername(username1);
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

  private Object getApoderado(String respuesta) {
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
            .fromJson(respuesta, RespApoderado.class);
      } catch (Exception a) {
        mensaje = new Mensaje();
        mensaje.setState(false);
        mensaje.setMsg("Petición denegada");
        objeto = mensaje;
      }
    } else {
      objeto = mensaje;
    }

    if (objeto instanceof RespApoderado) {
      if (!((RespApoderado) objeto).getApoderado().getCorreo().equals("")) {
        objeto = encryption
            .decriptarDataApoderado(((RespApoderado) objeto).getApoderado());
      } else {
        objeto = mensaje;
      }

    }

    return objeto;
  }

  public final Object loggear(String correo, String password) {
    if (apodInterface == null) {
      return new Object();
    }
    String correoNew = encryption.encriptarDato(correo);
    String passwordNew = encryption.encriptarDato(password);
    Call<String> call = apodInterface.logear(correoNew, passwordNew);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getApoderado(respuesta);
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
                  .fromJson(respuestaString, RespApoderado.class);
              objeto = respApod;
            } catch (Exception a) {
              objeto = new Object();
            }
          }

          if (objeto instanceof RespApoderado) {
            Apoderado apoderado = encryption
                .decriptarDataApoderado(((RespApoderado) objeto).getApoderado());
            objeto = apoderado;
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
  /*
  public Object getApodoByUsername(int id, String authToken,
      String username) {
    String username1 = encryption.encriptarDato(username);

    Call<String> call = apodInterface
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

  public final Object getApoderadoByCorreo(String correo, String email, String contrasena,
      String authToken, int id) {
    if (apodInterface == null) {
      return new Object();
    }
    String correo1 = encryption.encriptarDato(correo);
    String email1 = encryption.encriptarDato(email);
    String contrasena1 = encryption.encriptarDato(contrasena);
    Call<String> call = apodInterface
        .getByCorreo(id, authToken, email1, contrasena1, correo1);
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getApoderado(respuesta);
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
                  .fromJson(respuestaString, RespApoderado.class);
              objeto = respApod;
            } catch (Exception a) {
              objeto = new Object();
            }
          }

          if (objeto instanceof RespApoderado) {
            Apoderado apoderado = encryption
                .decriptarDataApoderado(((RespApoderado) objeto).getApoderado());
            objeto = apoderado;
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

  public final Object updateApoderado(String email, String contrasena,
      String authToken, int id, FieldUpdatesApoderado fieldUpdatesApoderado) {
    if (apodInterface == null) {

      return new Object();
    }
    String email2 = encryption.encriptarDato(email);
    String contrasena2 = encryption.encriptarDato(contrasena);
    FieldUpdatesApoderado newApoderado1 = encryption
        .encriptarFieldUpdateApoderado(fieldUpdatesApoderado);
    Call<String> call = apodInterface
        .update(id, authToken, email2, contrasena2, email2, newApoderado1.getLatitudInicial(),
            newApoderado1.getLongitudInicial(), newApoderado1.getLatitudFinal(),
            newApoderado1.getLongitudFinal(), newApoderado1.getPassword(),
            newApoderado1.getLugar());
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getApoderado(respuesta);
    } catch (Exception e) {
      mensaje.setMsg(e.getLocalizedMessage());
      objeto = mensaje;
    }
    return objeto;

  }


  public final Object insertarDataApoderado(Apoderado objNewApoderado) {
    if (apodInterface == null) {
      return new Object();
    }
    Apoderado apod = encryption.encriptarDataApoderado(objNewApoderado);
    Call<String> call = apodInterface
        .store(apod.getApodo(), apod.getCorreo(), apod.getNombreUsuario(),
            apod.getFechaNac(), apod.getPassword());
    Mensaje mensaje = new Mensaje();
    mensaje.setState(false);
    Object objeto;
    try {
      String respuesta = call.execute().body();
      objeto = getApoderado(respuesta);
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
              .fromJson(respuestaString, RespApoderado.class);
          objeto = respApod;
        } catch (Exception a) {
          objeto = new Object();
        }
      }

      if (objeto instanceof RespApoderado) {
        Apoderado apoderado = encryption
            .decriptarDataApoderado(((RespApoderado) objeto).getApoderado());
        objeto = apoderado;
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
                  .fromJson(respuestaString, RespApoderado.class);
              objeto = respApod;
            } catch (Exception a) {
              objeto = new Object();
            }
          }

          if (objeto instanceof RespApoderado) {
            Apoderado apoderado = encryption
                .decriptarDataApoderado(((RespApoderado) objeto).getApoderado());
            objeto = apoderado;
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
