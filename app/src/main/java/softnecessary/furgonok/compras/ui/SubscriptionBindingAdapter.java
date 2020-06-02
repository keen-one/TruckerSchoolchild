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

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import softnecessary.furgonok.R;
import softnecessary.furgonok.compras.Constants;
import softnecessary.furgonok.compras.billing.BillingUtilities;
import softnecessary.furgonok.compras.data.ContentResource;
import softnecessary.furgonok.compras.data.SubscriptionStatus;
import softnecessary.furgonok.compras.utils.SubscriptionUtilities;

// TODO(123725049): Improve data binding.
public class SubscriptionBindingAdapter {


  /**
   * Update a loading progress bar when the status changes.
   * <p>
   * When the network state changes, the binding adapter triggers this view in the layout XML. See
   * the layout XML files for the app:loadingProgressBar attribute.
   */
  @BindingAdapter("loadingProgressBar")
  public static void loadingProgressBar(ProgressBar view, boolean loading) {
    view.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  /**
   * Update basic content when the URL changes.
   * <p>
   * When the image URL content changes, the binding adapter triggers this view in the layout XML.
   * See the layout XML files for the app:updateBasicContent attribute.
   */
  @BindingAdapter("updateBasicContent")
  public static void updateBasicContent(View view, @Nullable ContentResource basicContent) {
    ImageView image = view.findViewById(R.id.home_basic_image);
    TextView textView = view.findViewById(R.id.home_basic_text);
    String url = basicContent == null ? null : basicContent.url;
    if (url == null) {

      image.setVisibility(View.VISIBLE);
      Glide.with(view.getContext())
          .load(url)
          .into(image);

      textView.setText(view.getResources().getString(R.string.basic_content_text));
    } else {
      image.setVisibility(View.GONE);
      textView.setText(view.getResources().getString(R.string.no_basic_content));
    }
  }

  /**
   * Update premium content on the Premium fragment when the URL changes.
   * <p>
   * When the image URL content changes, the binding adapter triggers this view in the layout XML.
   * See the layout XML files for the app:updatePremiumContent attribute.
   */
  @BindingAdapter("updatePremiumContent")
  public static void updatePremiumContent(View view, @Nullable ContentResource premiumContent) {
    ImageView image = view.findViewById(R.id.premium_premium_image);
    TextView textView = view.findViewById(R.id.premium_premium_text);
    String url = premiumContent == null ? null : premiumContent.url;
    if (url != null) {

      image.setVisibility(View.VISIBLE);
      Glide.with(image.getContext())
          .load(url)
          .into(image);
      textView.setText(view.getResources().getString(R.string.premium_content_text));

    } else {

      image.setVisibility(View.GONE);
      textView.setText(view.getResources().getString(R.string.no_premium_content));

    }
  }

  /**
   * Update subscription views on the Home fragment when the subscription changes.
   * <p>
   * When the subscription changes, the binding adapter triggers this view in the layout XML. See
   * the layout XML files for the app:updateHomeViews attribute.
   */
  @BindingAdapter("updateHomeViews")
  public static void updateHomeViews(View view, List<SubscriptionStatus> subscriptions) {
    TextView restoreMsg = view.findViewById(R.id.home_restore_message);
    LinearLayout paywallMsg = view.findViewById(R.id.home_paywall_message);
    TextView gracePeriodMsg = view.findViewById(R.id.home_grace_period_message);
    LinearLayout transferMsg = view.findViewById(R.id.home_transfer_message);
    LinearLayout accountHoldMsg = view.findViewById(R.id.home_account_hold_message);
    LinearLayout basicMsg = view.findViewById(R.id.home_basic_message);

    // Set visibility assuming no subscription is available.
    // If a subscription is found that meets certain criteria,
    // then the visibility of the paywall will be changed to View.GONE.
    paywallMsg.setVisibility(View.VISIBLE);

    // The remaining views start hidden. If a subscription is found that meets each criteria,
    // then the visibility will be changed to View.VISIBLE.
    restoreMsg.setVisibility(View.GONE);
    gracePeriodMsg.setVisibility(View.GONE);
    transferMsg.setVisibility(View.GONE);
    accountHoldMsg.setVisibility(View.GONE);
    basicMsg.setVisibility(View.VISIBLE);
    // Update based on subscription information.
    if (subscriptions != null) {
      for (SubscriptionStatus subscription : subscriptions) {
        if (BillingUtilities.isPremiumContent(subscription)) {

          if (BillingUtilities.isSubscriptionRestore(subscription)) {

            restoreMsg.setVisibility(View.VISIBLE);
            String expiryDate = getHumanReadableExpiryDate(subscription);
            restoreMsg.setText(view.getResources()
                .getString(R.string.restore_message_with_date, expiryDate));

            paywallMsg.setVisibility(View.GONE); // Paywall gone.
          }
          if (BillingUtilities.isGracePeriod(subscription)) {

            gracePeriodMsg.setVisibility(View.VISIBLE);

            paywallMsg.setVisibility(View.GONE); // Paywall gone.
          }
        /*
        if (BillingUtilities.isTransferRequired(subscription)
            && TextUtils.equals(subscription.sku, Constants.BASIC_SKU)) {

          transferMsg.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }*/
          if (BillingUtilities.isAccountHold(subscription)) {

            accountHoldMsg.setVisibility(View.VISIBLE);
            paywallMsg.setVisibility(View.GONE); // Paywall gone.
          }
        }
        if (BillingUtilities.isBasicContent(subscription)
            || BillingUtilities.isPremiumContent(subscription)) {

          basicMsg.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }

      }
    }
  }

  /**
   * Update subscription views on the Premium fragment when the subscription changes.
   * <p>
   * When the subscription changes, the binding adapter triggers this view in the layout XML. See
   * the layout XML files for the app:updatePremiumViews attribute.
   */
  @BindingAdapter("updatePremiumViews")
  public static void updatePremiumViews(View view, List<SubscriptionStatus> subscriptions) {
    TextView restoreMsg = view.findViewById(R.id.premium_restore_message);
    LinearLayout paywallMsg = view.findViewById(R.id.premium_paywall_message);
    TextView gracePeriodMsg = view.findViewById(R.id.premium_grace_period_message);
    LinearLayout transferMsg = view.findViewById(R.id.premium_transfer_message);
    LinearLayout accountHoldMsg = view.findViewById(R.id.premium_account_hold_message);
    LinearLayout premiumContent = view.findViewById(R.id.premium_premium_content);
    LinearLayout upgradeMsg = view.findViewById(R.id.premium_upgrade_message);

    // Set visibility assuming no subscription is available.
    // If a subscription is found that meets certain criteria, then the visibility of the paywall
    // will be changed to View.GONE.
    paywallMsg.setVisibility(View.VISIBLE);
    // The remaining views start hidden. If a subscription is found that meets each criteria,
    // then the visibility will be changed to View.VISIBLE.
    restoreMsg.setVisibility(View.GONE);
    gracePeriodMsg.setVisibility(View.GONE);
    transferMsg.setVisibility(View.GONE);
    accountHoldMsg.setVisibility(View.GONE);
    premiumContent.setVisibility(View.GONE);
    upgradeMsg.setVisibility(View.GONE);

    // The Upgrade button should appear if the user has a basic subscription, but does not
    // have a premium subscription. This variable keeps track of whether a premium subscription
    // has been found when looking throug the list of subscriptions.
    boolean hasPremium = false;
    // Update based on subscription information.
    if (subscriptions != null) {
      for (SubscriptionStatus subscription : subscriptions) {
        if (BillingUtilities.isSubscriptionRestore(subscription)) {

          restoreMsg.setVisibility(View.VISIBLE);
          String expiryDate = getHumanReadableExpiryDate(subscription);
          restoreMsg.setText(view.getResources()
              .getString(R.string.restore_message_with_date, expiryDate));
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }
        if (BillingUtilities.isGracePeriod(subscription)) {

          gracePeriodMsg.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }
        if (BillingUtilities.isTransferRequired(subscription)
            && TextUtils.equals(subscription.sku, Constants.PREMIUM_SKU)) {

          transferMsg.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }
        if (BillingUtilities.isAccountHold(subscription)) {

          accountHoldMsg.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }

        // The upgrade message must be shown if there is a basic subscription
        // and there are zero premium subscriptions. We need to keep track of the premium
        // subscriptions and hide the upgrade message if we find any.
        if (BillingUtilities.isPremiumContent(subscription)) {

          premiumContent.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
          // Make sure we do not ask for an upgrade when user has premium subscription.
          hasPremium = true;
          upgradeMsg.setVisibility(View.GONE);
        }
        if (BillingUtilities.isBasicContent(subscription)
            && !BillingUtilities.isPremiumContent(subscription)
            && !hasPremium) {

          // Upgrade message will be hidden if a premium subscription is found later.
          upgradeMsg.setVisibility(View.VISIBLE);
          paywallMsg.setVisibility(View.GONE); // Paywall gone.
        }
      }
    }
  }

  /**
   * Update views on the Settings fragment when the subscription changes.
   * <p>
   * When the subscription changes, the binding adapter triggers this view in the layout XML. See
   * the layout XML files for the app:updateSettingsViews attribute.
   */
  @BindingAdapter("updateSettingsViews")
  public static void updateSettingsViews(View view, List<SubscriptionStatus> subscriptions) {
    Button premiumBtn = view.findViewById(R.id.subscription_option_premium_button);
    Button basicBtn = view.findViewById(R.id.subscription_option_basic_button);
    LinearLayout transferMsg = view.findViewById(R.id.settings_transfer_message);
    TextView transferMsgText = view.findViewById(R.id.settings_transfer_message_text);
    basicBtn.setVisibility(View.GONE);
    // Set default button text: it might be overridden based on the subscription state.
    premiumBtn.setText(view.getResources()
        .getString(R.string.subscription_option_premium_message));
    basicBtn.setText(view.getResources()
        .getString(R.string.subscription_option_basic_message));
    transferMsg.setVisibility(View.GONE);
    // Update based on subscription information.

    boolean premiumRequiresTransfer = false;
    if (subscriptions != null) {
      for (SubscriptionStatus subscription : subscriptions) {
        String sku = subscription.sku;

        if (sku != null) {
          if (Constants.PREMIUM_SKU.equals(sku)) {
            premiumBtn.setText(SubscriptionUtilities
                .premiumTextForSubscription(view.getResources(), subscription));
            if (BillingUtilities.isTransferRequired(subscription)) {
              premiumRequiresTransfer = true;
            }
          }
        }
      }
    }

    String message = null;
    if (premiumRequiresTransfer) {
      String premiumName = view.getResources().getString(R.string.premium_button_text);
      message = view.getResources()
          .getString(R.string.transfer_message_with_sku, premiumName);
    }
    if (message != null) {

      transferMsg.setVisibility(View.VISIBLE);
      transferMsgText.setText(message);
    } else {
      transferMsgText.setText(view.getResources().getString(R.string.transfer_message));
    }
  }

  /**
   * Get a readable expiry date from a subscription.
   */
  private static String getHumanReadableExpiryDate(SubscriptionStatus subscription) {
    Long milliSeconds = subscription.activeUntilMillisec;
    Locale locale = new Locale("es", "CL");
    DateFormat formatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, locale);

    Calendar calendar = Calendar.getInstance(locale);
    calendar.setTimeInMillis(milliSeconds);
    if (milliSeconds == 0L) {

    } else {

    }
    return formatter.format(calendar.getTime());
  }
}
