package softnecessary.furgonok.managedb;

import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


class MiInterceptor implements Interceptor {

  private String appId;

  MiInterceptor(String appId) {
    this.appId = appId;

  }

  @NonNull
  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {

    Request request = chain.request();
    Request request1 = request.newBuilder()
        .addHeader("appid", appId)
        .build();

    return chain.proceed(request1);
  }
}
