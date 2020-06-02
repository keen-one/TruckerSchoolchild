/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package softnecessary.furgonok.compras;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.concurrent.Executor;
import org.json.JSONObject;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.R;

public class InstanceIDService extends FirebaseMessagingService {

  @Override
  public void onNewToken(@NonNull String s) {

    sendRegistrationToServer(s);
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    Map<String, String> params = remoteMessage.getData();
    JSONObject object = new JSONObject(params);

    String NOTIFICATION_CHANNEL_ID = "Nilesh_channel";

    long[] pattern = {0, 1000, 500, 1000};

    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
          "Your Notifications",
          NotificationManager.IMPORTANCE_HIGH);

      notificationChannel.setDescription("");
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.RED);
      notificationChannel.setVibrationPattern(pattern);
      notificationChannel.enableVibration(true);
      if (mNotificationManager != null) {
        mNotificationManager.createNotificationChannel(notificationChannel);
      }

    }

    // to diaplay notification in DND Mode
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (mNotificationManager != null) {
        NotificationChannel channel = mNotificationManager
            .getNotificationChannel(NOTIFICATION_CHANNEL_ID);
        channel.canBypassDnd();
      }

    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
        NOTIFICATION_CHANNEL_ID);
    RemoteMessage.Notification remoteMsg = remoteMessage.getNotification();
    if (remoteMsg != null) {
      notificationBuilder.setAutoCancel(true)
          .setColor(ContextCompat.getColor(this, R.color.colorAccent))
          .setContentTitle(getString(R.string.app_name))
          .setContentText(remoteMsg.getBody())
          .setDefaults(Notification.DEFAULT_ALL)
          .setWhen(System.currentTimeMillis())
          .setSmallIcon(R.mipmap.ic_launcher_foreground)
          .setAutoCancel(true);
    }

    if (mNotificationManager != null) {
      mNotificationManager.notify(1000, notificationBuilder.build());
    }

  }

  /**
   * Called if InstanceID token is updated. This may occur if the security of the previous token had
   * been compromised. Note that this is called when the InstanceID token is initially generated so
   * this is where you would retrieve the token.
   */

  public void onTokenRefresh() {
    // Get updated InstanceID token.
    FirebaseInstanceId.getInstance().getInstanceId()
        .addOnSuccessListener((Executor) this, new OnSuccessListener<InstanceIdResult>() {
          @Override
          public void onSuccess(InstanceIdResult instanceIdResult) {
            String refreshedToken = instanceIdResult.getToken();

            sendRegistrationToServer(refreshedToken);

          }
        });


  }

  /**
   * Persist token to servers.
   */
  private void sendRegistrationToServer(@Nullable String token) {
    if (token != null) {
      ((MiAplicacion) getApplication()).getRepository().registerInstanceId(token);
      // No need to unregister the previous Instance ID token because the server
      // automatically removes invalidated tokens.
    }
  }
}
