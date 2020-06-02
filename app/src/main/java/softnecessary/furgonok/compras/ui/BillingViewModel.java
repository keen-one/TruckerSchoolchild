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

package softnecessary.furgonok.compras.ui;

import android.app.Application;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import java.util.List;
import java.util.Map;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.compras.Constants;
import softnecessary.furgonok.compras.billing.BillingUtilities;
import softnecessary.furgonok.compras.data.SubscriptionStatus;

public class BillingViewModel extends AndroidViewModel {

  /**
   * Send an event when the Activity needs to buy something.
   */
  public SingleLiveEvent<BillingFlowParams> buyEvent = new SingleLiveEvent<>();
  /**
   * Send an event when the UI should open the Google Play Store for the user to manage their
   * subscriptions.
   */
  public SingleLiveEvent<String> openPlayStoreSubscriptionsEvent = new SingleLiveEvent<>();
  /**
   * Local billing purchase data.
   */
  private MutableLiveData<List<Purchase>> purchases;
  /**
   * SkuDetails for all known SKUs.
   */
  private MutableLiveData<Map<String, SkuDetails>> skusWithSkuDetails;
  /**
   * Subscriptions record according to the server.
   */
  private MediatorLiveData<List<SubscriptionStatus>> subscriptions;

  public BillingViewModel(Application application) {
    super(application);
    MiAplicacion subApp = ((MiAplicacion) application);
    purchases = subApp.getBillingClientLifecycle().purchases;
    skusWithSkuDetails = subApp.getBillingClientLifecycle().skusWithSkuDetails;
    subscriptions = subApp.getRepository().getSubscriptions();
  }

  /**
   * Open the Play Store subscription center. If the user has exactly one SKU, then open the
   * deeplink to the specific SKU.
   */
  public void openPlayStoreSubscriptions() {
    /*boolean hasBasic = BillingUtilities.deviceHasGooglePlaySubscription(
        purchases.getValue(), Constants.BASIC_SKU);*/
    boolean hasPremium = BillingUtilities.deviceHasGooglePlaySubscription
        (purchases.getValue(), Constants.PREMIUM_SKU);

    if (hasPremium) {
      // If we just have a premium subscription, open the premium SKU.
      openPlayStoreSubscriptionsEvent.postValue(Constants.PREMIUM_SKU);
    } else {
      // If we do not have an active subscription,
      // or if we have multiple subscriptions, open the default subscription center.
      openPlayStoreSubscriptionsEvent.call();
    }
  }

  /**
   * Open account hold subscription.
   * <p>
   * We need to use the server data to understand account hold. Most of the other deeplinks are
   * based on the purchase tokens returned on the local device. Since the purchase tokens will not
   * be returned when the subscription is in account hold, we look at the server data to determine
   * the deeplink.
   */
  public void openAccountHoldSubscription() {
    boolean isPremiumOnServer = BillingUtilities
        .serverHasSubscription(subscriptions.getValue(), Constants.PREMIUM_SKU);
    /*boolean isBasicOnServer = BillingUtilities
        .serverHasSubscription(subscriptions.getValue(), Constants.BASIC_SKU);*/
    if (isPremiumOnServer) {
      openPremiumPlayStoreSubscriptions();
    }

    /*if (isBasicOnServer) {
      openBasicPlayStoreSubscriptions();
    }*/
  }

  /**
   * Open the Play Store basic subscription.
   */
  /*
  public void openBasicPlayStoreSubscriptions() {
    openPlayStoreSubscriptionsEvent.postValue(Constants.BASIC_SKU);
  }*/

  /**
   * Open the Play Store premium subscription.
   */
  private void openPremiumPlayStoreSubscriptions() {
    openPlayStoreSubscriptionsEvent.postValue(Constants.PREMIUM_SKU);
  }

  /**
   * Buy a basic subscription.
   */
  public void buyBasic() {
    /*boolean hasBasic = BillingUtilities
        .deviceHasGooglePlaySubscription(purchases.getValue(), Constants.BASIC_SKU);*/
    boolean hasPremium = BillingUtilities
        .deviceHasGooglePlaySubscription(purchases.getValue(), Constants.PREMIUM_SKU);

    if (hasPremium) {
      // If the user just has a premium subscription, downgrade.
      buy(Constants.BASIC_SKU, Constants.PREMIUM_SKU);
    } else {
      // If the user dooes not have a subscription, buy the basic SKU.
      buy(Constants.BASIC_SKU, null);
    }
  }

  /**
   * Buy a premium subscription.
   */
  public void buyPremium() {
    boolean hasBasic = true;
    /*boolean hasBasic = BillingUtilities
        .deviceHasGooglePlaySubscription(purchases.getValue(), Constants.BASIC_SKU);*/
    boolean hasPremium = BillingUtilities
        .deviceHasGooglePlaySubscription(purchases.getValue(), Constants.PREMIUM_SKU);

    if (hasPremium) {
      // If the user has both subscriptions, open the premium SKU on Google Play.
      openPlayStoreSubscriptionsEvent.postValue(Constants.PREMIUM_SKU);
    } else {
      buy(Constants.PREMIUM_SKU, Constants.BASIC_SKU);
    }

  }

  /**
   * Upgrade to a premium subscription.
   */
  public void buyUpgrade() {
    buy(Constants.PREMIUM_SKU, Constants.BASIC_SKU);
  }

  /**
   * Use the Google Play Billing Library to make a purchase.
   */
  private void buy(String sku, @Nullable String oldSku) {
    // First, determine whether the new SKU can be purchased.
    boolean isSkuOnServer = BillingUtilities
        .serverHasSubscription(subscriptions.getValue(), sku);
    boolean isSkuOnDevice = BillingUtilities
        .deviceHasGooglePlaySubscription(purchases.getValue(), sku);

    if (isSkuOnDevice && isSkuOnServer) {

    } else if (isSkuOnDevice && !isSkuOnServer) {

    } else if (!isSkuOnDevice && isSkuOnServer) {

    } else {
      // Second, determine whether the old SKU can be replaced.
      // If the old SKU cannot be used, set this value to null and ignore it.

      String oldSkuToBeReplaced = null;
      if (isOldSkuReplaceable(subscriptions.getValue(), purchases.getValue(), oldSku)) {
        oldSkuToBeReplaced = oldSku;
      }

      // Third, create the billing parameters for the purchase.
      /*
      if (sku.equals(oldSkuToBeReplaced)) {

      } else {
        if (oldSkuToBeReplaced != null) {
          if (Constants.PREMIUM_SKU.equals(sku)
              && oldSkuToBeReplaced.equals(Constants.BASIC_SKU)) {

          } else if (sku.equals(Constants.BASIC_SKU)
              && Constants.PREMIUM_SKU.equals(oldSkuToBeReplaced)) {

          } else {

          }
        }
      }*/

      SkuDetails skuDetails = null;
      // Create the parameters for the purchase.
      if (skusWithSkuDetails.getValue() != null) {
        skuDetails = skusWithSkuDetails.getValue().get(sku);
      }

      if (skuDetails == null) {

        return;
      }

      BillingFlowParams.Builder billingBuilder =
          BillingFlowParams.newBuilder().setSkuDetails(skuDetails);
      // Only set the old SKU parameter if the old SKU is already owned.
      if (oldSkuToBeReplaced != null && !oldSkuToBeReplaced.equals(sku)) {
        billingBuilder.setOldSku(oldSkuToBeReplaced, oldSku);
      }

      BillingFlowParams billingParams = billingBuilder.build();

      // Send the parameters to the Activity in order to launch the billing flow.
      buyEvent.postValue(billingParams);
    }
  }

  /**
   * Determine if the old SKU can be replaced.
   */
  private boolean isOldSkuReplaceable(
      List<SubscriptionStatus> subscriptions,
      List<Purchase> purchases,
      String oldSku) {
    if (oldSku == null) {
      return false;
    }
    boolean isOldSkuOnServer = BillingUtilities.serverHasSubscription(subscriptions, oldSku);
    boolean isOldSkuOnDevice = BillingUtilities.deviceHasGooglePlaySubscription(purchases, oldSku);

    if (!isOldSkuOnDevice) {

      return false;
    } else if (!isOldSkuOnServer) {

      return false;
    } else {
      SubscriptionStatus subscription = BillingUtilities
          .getSubscriptionForSku(subscriptions, oldSku);
      return subscription == null || !subscription.subAlreadyOwned;
    }
  }
}
