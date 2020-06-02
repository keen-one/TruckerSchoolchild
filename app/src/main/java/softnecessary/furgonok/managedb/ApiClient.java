package softnecessary.furgonok.managedb;


import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import softnecessary.furgonok.PantallaInicial;
import softnecessary.furgonok.utils.AESCipher;

public final class ApiClient {

  public static final String miIp = "furgonservicio.000webhostapp.com";
  private String tipoUsuario;
  private String keyPass;

  public ApiClient(String tipoUsuario, String keyPass) {

    this.tipoUsuario = tipoUsuario;
    this.keyPass = keyPass;
  }

  public final Retrofit getRetrofit(String huella) {
    String baseurl = "";
    String encriptado = AESCipher.encrypt(keyPass, keyPass, huella);

    MiInterceptor interceptor = new MiInterceptor(encriptado);
    //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    //interceptor.level(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor).build();

    String urlBase = "https://furgonservicio.000webhostapp.com/";
    if (tipoUsuario.equals(PantallaInicial.APODERADO)) {
      baseurl = urlBase.concat("api/apoderado/");

    } else if (tipoUsuario.equals(PantallaInicial.CONDUCTOR)) {
      baseurl = urlBase.concat("api/conductor/");

    }
    if (baseurl.equals("")) {
      return null;
    } else {
      return new Retrofit.Builder().baseUrl(baseurl).client(client)
          //.addConverterFactory(GsonConverterFactory.create())
          .addConverterFactory(ScalarsConverterFactory.create())
          .build();
    }

  }

  @NonNull
  @Override
  public String toString() {
    return "ApiClient{" +
        "tipoUsuario='" + tipoUsuario + '\'' +
        ", keyPass='" + keyPass + '\'' +
        '}';
  }
}
