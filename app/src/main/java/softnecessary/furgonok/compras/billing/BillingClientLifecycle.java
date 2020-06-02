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

package softnecessary.furgonok.compras.billing;

import android.app.Activity;
import android.app.Application;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.compras.Constants;
import softnecessary.furgonok.compras.ui.SingleLiveEvent;

public class BillingClientLifecycle implements LifecycleObserver, PurchasesUpdatedListener,
    BillingClientStateListener, SkuDetailsResponseListener {


  private static volatile BillingClientLifecycle INSTANCE;
  /**
   * The purchase event is observable. Only one observer will be notified.
   */
  public SingleLiveEvent<List<Purchase>> purchaseUpdateEvent = new SingleLiveEvent<>();
  /**
   * Purchases are observable. This list will be updated when the Billing Library detects new or
   * existing purchases. All observers will be notified.
   */
  public MutableLiveData<List<Purchase>> purchases = new MutableLiveData<>();
  /**
   * SkuDetails for all known SKUs.
   */
  public MutableLiveData<Map<String, SkuDetails>> skusWithSkuDetails = new MutableLiveData<>();
  private Application app;
  private BillingClient billingClient;

  private BillingClientLifecycle(Application app) {
    this.app = app;
  }

  public static BillingClientLifecycle getInstance(Application app) {
    if (INSTANCE == null) {
      synchronized (BillingClientLifecycle.class) {
        if (INSTANCE == null) {
          INSTANCE = new BillingClientLifecycle(app);
        }
      }
    }
    return INSTANCE;
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  public void create() {

    // Create a new BillingClient in onCreate().
    // Since the BillingClient can only be used once, we need to create a new instance
    // after ending the previous connection to the Google Play Store in onDestroy().
    billingClient = BillingClient.newBuilder(app)
        .setListener(this)
        .enablePendingPurchases() // Not used for subscriptions.
        .build();
    if (!billingClient.isReady()) {

      billingClient.startConnection(this);
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void destroy() {

    if (billingClient.isReady()) {

      // BillingClient can only be used once.
      // After calling endConnection(), we must create a new BillingClient.
      billingClient.endConnection();
    }
  }

  @Override
  public void onBillingSetupFinished(BillingResult billingResult) {
    int responseCode = billingResult.getResponseCode();
    String debugMessage = billingResult.getDebugMessage();

    if (responseCode == BillingClient.BillingResponseCode.OK) {
      // The billing client is ready. You can query purchases here.
      querySkuDetails();
      queryPurchases();
    }
  }

  @Override
  public void onBillingServiceDisconnected() {

    // TODO: Try connecting again with exponential backoff.
  }

  /**
   * Receives the result from {@link #querySkuDetails()}}.
   * <p>
   * Store the SkuDetails and post them in the {@link #skusWithSkuDetails}. This allows other parts
   * of the app to use the {@link SkuDetails} to show SKU information and make purchases.
   */
  @Override
  public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
    if (billingResult == null) {

      return;
    }

    int responseCode = billingResult.getResponseCode();
    String debugMessage = billingResult.getDebugMessage();
    switch (responseCode) {
      case BillingClient.BillingResponseCode.OK:

        if (skuDetailsList == null) {

          skusWithSkuDetails.postValue(Collections.emptyMap());
        } else {
          Map<String, SkuDetails> newSkusDetailList = new HashMap<>();
          for (SkuDetails skuDetails : skuDetailsList) {
            newSkusDetailList.put(skuDetails.getSku(), skuDetails);
          }
          skusWithSkuDetails.postValue(newSkusDetailList);

        }
        break;
      case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
      case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
      case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
      case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
      case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
      case BillingClient.BillingResponseCode.ERROR:

        break;
      case BillingClient.BillingResponseCode.USER_CANCELED:

        break;
      // These response codes are not expected.
      case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
      case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
      case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
      default:

    }
  }

  /**
   * Query Google Play Billing for existing purchases.
   * <p>
   * New purchases will be provided to the PurchasesUpdatedListener. You still need to check the
   * Google Play Billing API to know when purchase tokens are removed.
   */
  public void queryPurchases() {
    if (!billingClient.isReady()) {
      return;
    }

    Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
    if (result == null) {
      MiAplicacion.miStatePurchase = PurchaseState.UNSPECIFIED_STATE;

      processPurchases(null);
    } else {
      if (result.getPurchasesList() == null) {
        MiAplicacion.miStatePurchase = PurchaseState.UNSPECIFIED_STATE;

        processPurchases(null);
      } else {
        if (result.getPurchasesList().size() > 0) {
          MiAplicacion.miStatePurchase = result.getPurchasesList()
              .get(result.getPurchasesList().size() - 1).getPurchaseState();
        }

        processPurchases(result.getPurchasesList());
      }
    }
  }

  /**
   * Called by the Billing Library when new purchases are detected.
   */
  public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
    if (billingResult == null) {

      return;
    }
    int responseCode = billingResult.getResponseCode();
    String debugMessage = billingResult.getDebugMessage();

    switch (responseCode) {
      case BillingClient.BillingResponseCode.OK:
        if (purchases == null) {

          processPurchases(null);
        } else {
          processPurchases(purchases);
        }
        break;
      case BillingClient.BillingResponseCode.USER_CANCELED:

        break;
      case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:

        break;
      case BillingClient.BillingResponseCode.DEVELOPER_ERROR:

        break;
    }
  }

  /**
   * Send purchase SingleLiveEvent and update purchases LiveData.
   * <p>
   * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever. The
   * LiveData will allow Google Play settings UI to update based on the latest purchase data.
   */
  private void processPurchases(List<Purchase> purchasesList) {
    if (purchasesList != null) {

    } else {

    }
    if (isUnchangedPurchaseList(purchasesList)) {

      return;
    }
    purchaseUpdateEvent.postValue(purchasesList);
    purchases.postValue(purchasesList);
    if (purchasesList != null) {
      logAcknowledgementStatus(purchasesList);
    }
  }

  /**
   * Log the number of purchases that are acknowledge and not acknowledged.
   * <p>
   * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
   * <p>
   * When the purchase is first received, it will not be acknowledge. This application sends the
   * purchase token to the server for registration. After the purchase token is registered to an
   * account, the Android app acknowledges the purchase token. The next time the purchase list is
   * updated, it will contain acknowledged purchases.
   */
  private void logAcknowledgementStatus(List<Purchase> purchasesList) {
    int ack_yes = 0;
    int ack_no = 0;
    for (Purchase purchase : purchasesList) {
      if (purchase.isAcknowledged()) {
        ack_yes++;
      } else {
        ack_no++;
      }
    }

  }

  /**
   * Check whether the purchases have changed before posting changes.
   */
  private boolean isUnchangedPurchaseList(List<Purchase> purchasesList) {
    // TODO: Optimize to avoid updates with identical data.
    return false;
  }

  /**
   * In order to make purchases, you need the {@link SkuDetails} for the item or subscription. This
   * is an asynchronous call that will receive a result in {@link #onSkuDetailsResponse}.
   */
  private void querySkuDetails() {

    List<String> skus = new ArrayList<>();
    //skus.add(Constants.BASIC_SKU);
    skus.add(Constants.PREMIUM_SKU);

    SkuDetailsParams params = SkuDetailsParams.newBuilder()
        .setType(BillingClient.SkuType.SUBS)
        .setSkusList(skus)
        .build();

    billingClient.querySkuDetailsAsync(params, this);
  }

  /**
   * Launching the billing flow.
   * <p>
   * Launching the UI to make a purchase requires a reference to the Activity.
   */
  public int launchBillingFlow(Activity activity, BillingFlowParams params) {
    String sku = params.getSku();
    String oldSku = params.getOldSku();

    if (!billingClient.isReady()) {

    }
    BillingResult billingResult = billingClient.launchBillingFlow(activity, params);
    int responseCode = billingResult.getResponseCode();
    String debugMessage = billingResult.getDebugMessage();

    return responseCode;
  }

  /**
   * Acknowledge a purchase.
   * <p>
   * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
   * <p>
   * Apps should acknowledge the purchase after confirming that the purchase token has been
   * associated with a user. This app only acknowledges purchases after successfully receiving the
   * subscription data back from the server.
   * <p>
   * Developers can choose to acknowledge purchases from a server using the Google Play Developer
   * API. The server has direct access to the user database, so using the Google Play Developer API
   * for acknowledgement might be more reliable. TODO(134506821): Acknowledge purchases on the
   * server.
   * <p>
   * If the purchase token is not acknowledged within 3 days, then Google Play will automatically
   * refund and revoke the purchase. This behavior helps ensure that users are not charged for
   * subscriptions unless the user has successfully received access to the content. This eliminates
   * a category of issues where users complain to developers that they paid for something that the
   * app is not giving to them.
   */
  public void acknowledgePurchase(String purchaseToken) {

    AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
        .setPurchaseToken(purchaseToken)
        .build();
    billingClient.acknowledgePurchase(params, new AcknowledgePurchaseResponseListener() {
      @Override
      public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();

      }
    });
  }
}
