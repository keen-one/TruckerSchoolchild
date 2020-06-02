package softnecessary.furgonok.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.PantallaInicial;

public final class MisPreferencias {

  private SharedPreferences pref;

  public MisPreferencias(Context context) {
    if (context != null) {
      pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

  }

  public SharedPreferences getPref() {
    return pref;
  }

  public final void guardarLongitud(String longitud) {
    if (pref == null) {
      return;
    }
    if (longitud == null) {
      return;
    }
    pref.edit().putString(PantallaInicial.ACTUAL_LONGITUD, longitud).apply();
  }

  public final void guardarLatitud(String latitud) {
    if (pref == null) {
      return;
    }
    if (latitud == null) {
      return;
    }
    pref.edit().putString(PantallaInicial.ACTUAL_LATITUD, latitud).apply();
  }

  public final String cargarLatitud() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.ACTUAL_LATITUD, "");
  }

  public final String cargarLongitud() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.ACTUAL_LONGITUD, "");
  }

  public final void guardarPasswordChat(String password) {
    if (password == null) {
      return;
    }
    if (pref != null) {

      pref.edit().putString(PantallaInicial.LOGIN_PASSWORD_CHAT, password).apply();
    }
  }

  public final String loadPasswordChat() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.LOGIN_PASSWORD_CHAT, "");
  }

  public final void guardarPassword(String password) {
    if (pref == null) {
      return;
    }
    if (password == null) {
      return;
    }
    pref.edit().putString(PantallaInicial.LOGIN_PASSWORD, password).apply();
  }

  public final void guardarNombreUsuario(String cadena) {
    if (pref == null) {
      return;
    }
    if (cadena == null) {
      MiAplicacion.miUsername = "";
    } else {
      MiAplicacion.miUsername = cadena;
    }
    if (cadena == null) {
      return;
    }
    pref.edit().putString(PantallaInicial.KEY_USERNAME, cadena).apply();
  }

  public final String loadNombreUsuario() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.KEY_USERNAME, "");
  }

  public final void guardarFechaDescargaMapa(String key, long fecha) {
    if (pref == null) {
      return;
    }
    SharedPreferences.Editor editor = pref.edit();
    editor.putLong(key, fecha);
    editor.apply();
  }

  public final void guardarPathMapa(String ruta) {
    if (pref == null) {
      return;
    }
    if (ruta == null) {
      return;
    }
    SharedPreferences.Editor editor = pref.edit();
    editor.putString(PantallaInicial.KEY_PATH_MAPA, ruta);
    editor.apply();
  }

  public final String cargarPathMapa() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.KEY_PATH_MAPA, "");
  }

  public final void saveTokenID(String token, int id) {
    if (pref == null) {
      return;
    }
    if (token == null) {
      return;
    }
    pref.edit().putString(PantallaInicial.MI_TOKEN, token).putInt(PantallaInicial.MI_ID, id)
        .apply();
  }

  public final String loadToken() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.MI_TOKEN, "");
  }

  public final int loadID() {
    if (pref == null) {
      return -1;
    }
    return pref.getInt(PantallaInicial.MI_ID, -1);
  }


  public final void saveVerificationByKey(String key, boolean valor) {
    if (pref == null) {
      return;
    }
    SharedPreferences.Editor edicion = pref.edit();
    edicion.putBoolean(key, valor);
    edicion.apply();
  }

  public final void guardarCredentialsLogin(String correo, String password) {
    if (pref == null) {
      return;
    }
    if (correo == null || password == null) {
      return;
    }
    pref.edit()
        .putString(PantallaInicial.LOGIN_EMAIL, correo)
        .putString(PantallaInicial.LOGIN_PASSWORD, password)
        .apply();
  }

  public final String loadCorreoLogin() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.LOGIN_EMAIL, "");
  }

  public final String loadPasswordLogin() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.LOGIN_PASSWORD, "");
  }

  public final boolean loadVerificationByKey(String key) {
    if (pref == null) {
      return false;
    }
    return pref.getBoolean(key, false);
  }

  public final String loadTypeUser() {
    if (pref == null) {
      return "";
    }
    return pref.getString(PantallaInicial.KEY_TYPE_USER, "");
  }


  public final void saveTypeUser(String tipoUsuario) {
    if (pref == null) {
      return;
    }
    if (tipoUsuario == null) {
      return;
    }
    SharedPreferences.Editor edicion = pref.edit();
    edicion.putString(PantallaInicial.KEY_TYPE_USER, tipoUsuario);
    edicion.apply();
  }

  public final void saveTipoUserNumber(int index) {
    if (pref == null) {
      return;
    }
    pref.edit().putInt(PantallaInicial.INDEX_TIPO_USER, index).apply();
  }

  public final int loadTipoUserNumber() {
    if (pref == null) {
      return 0;
    }
    return pref.getInt(PantallaInicial.INDEX_TIPO_USER, 0);
  }

}
