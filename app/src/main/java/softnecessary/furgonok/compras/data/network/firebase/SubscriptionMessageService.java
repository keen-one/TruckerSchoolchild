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

package softnecessary.furgonok.compras.data.network.firebase;


import androidx.annotation.Nullable;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.List;
import java.util.Map;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.compras.data.SubscriptionStatus;

public class SubscriptionMessageService extends FirebaseMessagingService {


  private static final String REMOTE_MESSAGE_SUBSCRIPTIONS_KEY = "currentStatus";

  @Override
  public void onMessageReceived(@Nullable RemoteMessage remoteMessage) {
    if (remoteMessage == null) {

      return;
    }
    Map<String, String> data = remoteMessage.getData();
    if (!data.isEmpty()) {
      List<SubscriptionStatus> result = null;
      if (data.containsKey(REMOTE_MESSAGE_SUBSCRIPTIONS_KEY)) {
        result = SubscriptionStatus
            .listFromJsonString(data.get(REMOTE_MESSAGE_SUBSCRIPTIONS_KEY));
      }
      if (result == null) {

      } else {
        ((MiAplicacion) getApplication()).getRepository().updateSubscriptionsFromNetwork(result);
      }
    }
  }
}
