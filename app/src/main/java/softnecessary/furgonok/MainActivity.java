package softnecessary.furgonok;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import java.io.File;
import softnecessary.furgonok.utils.MisPreferencias;


public class MainActivity extends AppCompatActivity {

  private MisPreferencias pref;
  private DrawerLayout miDrawer;
  private NavigationView nav;

  private NavigationView.OnNavigationItemSelectedListener listenerNav = new OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      int idItem = item.getItemId();
      Fragment fragment = new ContactList();
      if (idItem == R.id.item_chat) {

        fragment = new ContactList();
      } else if (idItem == R.id.item_perfil) {

        fragment = new Perfil();
      } else if (idItem == R.id.item_nav) {

        String path = pref.cargarPathMapa();
        File file = new File(path);
        if (file.exists() && path.endsWith(".map")) {
          fragment = new NavegacionMain();
        } else {
          Intent intent = new Intent(MainActivity.this, DownloadManager.class);
          startActivity(intent);
        }

      }

      getSupportFragmentManager().beginTransaction().replace(R.id.miContainer, fragment)
          .addToBackStack(null).commit();
      miDrawer.closeDrawers();
      return true;

    }
  };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    if (item.getItemId() == R.id.launch_loggout) {

      MiAplicacion.salirDeTodo(this, this);
      return true;
    } else if (item.getItemId() == R.id.launch_admin_descarga) {
      Intent intent = new Intent(this, DownloadManager.class);
      startActivity(intent);
      return true;
    }
    return false;
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


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    pref = new MisPreferencias(MainActivity.this);
    String tipoUser = pref.loadTypeUser();
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //Toolbar toolbar=findViewById(R.id.toolbar);
    toolbar.setTitle("Menu " + tipoUser.toLowerCase());

    miDrawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, miDrawer, toolbar,
        R.string.openDrawer, R.string.closeDrawer);
    miDrawer.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    OnClickListener clickNav = new OnClickListener() {
      @Override
      public void onClick(View v) {
        miDrawer.openDrawer(GravityCompat.START);
      }
    };
    toolbar.setNavigationOnClickListener(clickNav);
    nav = findViewById(R.id.mi_navegacion);
    nav.setNavigationItemSelectedListener(listenerNav);
    if (savedInstanceState == null) {
      nav.setCheckedItem(R.id.item_perfil);
      nav.setSelected(true);
      listenerNav.onNavigationItemSelected(nav.getMenu().findItem(R.id.item_perfil));
    }
    Intent esteIntent = getIntent();
    String data = esteIntent.getStringExtra("DATA");

    if (data != null) {

      cambiarNav(data);
    }
  }

  private void cambiarNav(String data) {

    nav.setCheckedItem(R.id.item_nav);
    nav.setSelected(true);
    NavegacionMain miNav = new NavegacionMain();
    Bundle bundle = new Bundle();
    bundle.putString("DATA", data);

    miNav.setArguments(bundle);
    getSupportFragmentManager().beginTransaction().replace(R.id.miContainer, miNav)
        .addToBackStack(null).commit();
    //listenerNav.onNavigationItemSelected(nav.getMenu().findItem(R.id.item_nav));

  }
}
