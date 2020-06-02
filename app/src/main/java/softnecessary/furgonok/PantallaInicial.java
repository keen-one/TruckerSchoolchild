package softnecessary.furgonok;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import softnecessary.furgonok.compras.Constants;
import softnecessary.furgonok.compras.billing.BillingClientLifecycle;
import softnecessary.furgonok.compras.ui.BillingViewModel;
import softnecessary.furgonok.compras.ui.FirebaseUserViewModel;
import softnecessary.furgonok.compras.ui.SubscriptionStatusViewModel;
import softnecessary.furgonok.compras.ui.TabFragment;
import softnecessary.furgonok.utils.Utilidades;


public class PantallaInicial extends AppCompatActivity {


  public static final String KEY_TYPE_USER = "KEY_TYPE_USER";
  public static final String CONDUCTOR = "CONDUCTOR";
  public static final String APODERADO = "APODERADO";
  public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
  public static final String LOGIN_PASSWORD = "LOGIN_PASSWORD";
  public static final String LOGIN_PASSWORD_CHAT = "LOGIN_PASSWORD_CHAT";
  public static final String MI_TOKEN = "MI_TOKEN";
  public static final String MI_ID = "MI_ID";
  public static final String KEY_PATH_MAPA = "KEY_PATH_MAPA";
  public static final String KEY_USERNAME = "KEY_USERNAME";
  public static final double LATITUD_DEFAULT = -33.45;
  public static final double LONGITUD_DEFAULT = -70.66;
  public static final double LATITUD_FINAL = -33.423953;
  public static final double LONGITUD_FINAL = -70.682086;
  public static final String PUNTO_PARTIDA = "CASA";
  public static final String DESTINO = "COLEGIO";

  public static final String FLAG_NEW_MSG = "FLAG_NEW_MSG";
  public static final String CONTACTO = "CONTACTO";
  public static final String INDEX_TIPO_USER = "INDEX_TIPO_USER";
  public static final String ACTUAL_LONGITUD = "ACTUAL_LONGITUD";
  public static final String ACTUAL_LATITUD = "ACTUAL_LATITUD";
  public static final int APP_PAGER_INDEX = 0;
  public static final int HOME_PAGER_INDEX = 1;
  public static final int PREMIUM_PAGER_INDEX = 2;
  public static final int SETTINGS_PAGER_INDEX = 3;
  static final String KEY_FECHA_DESCARGA = "KEY_FECHA_DESCARGA";
  static final String ESTADO_CONDUCTOR = "ESTADO_CONDUCTOR";
  private static final int RC_SIGN_IN = 0;
  private static final int COUNT = 4;
  public static boolean BANDERA_BORRADO = false;
  private BillingClientLifecycle billingClientLifecycle;

  private FirebaseUserViewModel authenticationViewModel;
  private SubscriptionStatusViewModel subscriptionViewModel;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pantalla_inicial);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //ActionBar actionBar = getSupportActionBar();
    //actionBar.setDisplayShowCustomEnabled(true);
    //actionBar.setTitle(R.string.app_name);
    //setSupportActionBar(toolbar);
    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
        getSupportFragmentManager()
    );
    // Set up the ViewPager with the sections adapter.
    ViewPager container = findViewById(R.id.container);
    TabLayout tabs = findViewById(R.id.tabs);
    container.setAdapter(sectionsPagerAdapter);
    container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
    tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(container));

    authenticationViewModel = ViewModelProviders.of(this).get(FirebaseUserViewModel.class);
    BillingViewModel billingViewModel = ViewModelProviders.of(this).get(BillingViewModel.class);
    subscriptionViewModel = ViewModelProviders.of(this).get(SubscriptionStatusViewModel.class);

    billingClientLifecycle = ((MiAplicacion) getApplication()).getBillingClientLifecycle();
    getLifecycle().addObserver(billingClientLifecycle);

    // Register purchases when they change.
    billingClientLifecycle.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
      @Override
      public void onChanged(List<Purchase> purchases) {
        if (purchases != null) {
          registerPurchases(purchases);
        }
      }
    });

    // Launch billing flow when user clicks button to buy something.
    billingViewModel.buyEvent.observe(this, new Observer<BillingFlowParams>() {
      @Override
      public void onChanged(BillingFlowParams billingFlowParams) {
        if (billingFlowParams != null) {
          billingClientLifecycle
              .launchBillingFlow(PantallaInicial.this, billingFlowParams);
        }
      }
    });

    // Open the Play Store when event is triggered.
    billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, new Observer<String>() {
      @Override
      public void onChanged(String sku) {
        Utilidades
            .showMsg(PantallaInicial.this, "Visitando subscripciones en la Google Play Store");

        String url;
        if (sku == null) {
          // If the SKU is not specified, just open the Google Play subscriptions URL.
          url = Constants.PLAY_STORE_SUBSCRIPTION_URL;
        } else {
          // If the SKU is specified, open the deeplink for this SKU on Google Play.
          url = String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,
              sku, getApplicationContext().getPackageName());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
      }
    });

    // Update authentication UI.

    final Observer<FirebaseUser> fireaseUserObserver = new Observer<FirebaseUser>() {
      @Override
      public void onChanged(@Nullable final FirebaseUser firebaseUser) {
        invalidateOptionsMenu();
        if (firebaseUser == null) {
          //triggerSignIn();
          Utilidades
              .showMsg(PantallaInicial.this, "Usuario desautenticado");
        } else {
          MiAplicacion.MI_LOGIN_EMAIL = firebaseUser.getEmail();
          if (MiAplicacion.MI_LOGIN_EMAIL == null) {
            MiAplicacion.MI_LOGIN_EMAIL = "";
          }
          Utilidades
              .showMsg(PantallaInicial.this, "Usuario actual: " + firebaseUser.getDisplayName());
        }
      }
    };
    authenticationViewModel.firebaseUser.observe(this, fireaseUserObserver);

    // Update subscription information when user changes.
    authenticationViewModel.userChangeEvent.observe(this, new Observer<Void>() {
      @Override
      public void onChanged(Void aVoid) {
        subscriptionViewModel.userChanged();
        List<Purchase> purchases = billingClientLifecycle.purchaseUpdateEvent.getValue();
        if (purchases != null) {
          registerPurchases(purchases);
        }
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    MiAplicacion.runServiceRespaldo(this);

    MiAplicacion.stopServiceNotification(this);
  }


  @Override
  protected void onStop() {
    MiAplicacion.runServiceNotificacion(this);
    super.onStop();
  }


  /**
   * Register SKUs and purchase tokens with the server.
   */
  private void registerPurchases(List<Purchase> purchaseList) {
    for (Purchase purchase : purchaseList) {
      String sku = purchase.getSku();
      String purchaseToken = purchase.getPurchaseToken();

      subscriptionViewModel.registerSubscription(sku, purchaseToken);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  /**
   * Update menu based on sign-in state. Called in response to {@link #invalidateOptionsMenu}.
   */

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    boolean isSignedIn = authenticationViewModel.isSignedIn();
    menu.findItem(R.id.sign_in).setVisible(!isSignedIn);
    menu.findItem(R.id.sign_out).setVisible(isSignedIn);
    return true;
  }

  /**
   * Called when menu item is selected.
   */

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.sign_out:
        triggerSignOut();
        return true;
      case R.id.sign_in:
        triggerSignIn();
        return true;
      case R.id.refresh:
        refreshData();
        return true;
    }
    return true;
  }

  private void refreshData() {
    billingClientLifecycle.queryPurchases();
    subscriptionViewModel.manualRefresh();
  }

  /**
   * Sign in with FirebaseUI Auth.
   */
  private void triggerSignIn() {
    Utilidades.showMsg(PantallaInicial.this, "¡Intentando autenticar!");

    List<AuthUI.IdpConfig> providers = new ArrayList<>();
    // Configure the different methods users can sign in
    providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
    providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers).setTheme(R.style.MiTema)
            .build(),
        RC_SIGN_IN);
  }

  /**
   * Sign out with FirebaseUI Auth.
   */
  private void triggerSignOut() {
    subscriptionViewModel.unregisterInstanceId();
    AuthUI.getInstance().signOut(this)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            Utilidades.showMsg(PantallaInicial.this, "Usuario ha salido");

            authenticationViewModel.updateFirebaseUser();
          }
        });
  }

  /**
   * Receive Activity result, including sign-in result.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      // If sign-in is successful, update ViewModel.
      if (resultCode == RESULT_OK) {
        Utilidades.showMsg(PantallaInicial.this, "Autenticado con exito");

        authenticationViewModel.updateFirebaseUser();
      } else {
        Utilidades.showMsg(PantallaInicial.this, "Autenticacion fallida");

      }
    }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
   * sections/tabs/pages.
   */
  static class SectionsPagerAdapter extends FragmentPagerAdapter {

    SectionsPagerAdapter(@NonNull FragmentManager fm) {
      super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
      if (position == 0) {
        return new Inicio();
      }
      return TabFragment.newInstance(position);
    }

    @Override
    public int getCount() {
      return COUNT;
    }
  }
}
