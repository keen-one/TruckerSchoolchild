package softnecessary.furgonok;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class Inicio extends Fragment {


  private OnClickListener listenerLogin = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(getContext(), LoginPantalla.class);
      startActivity(intent);
    }
  };
  private OnClickListener listenerApod = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(getContext(), RegistroApoderado.class);
      startActivity(intent);
    }
  };
  private OnClickListener listenerCon = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(getActivity(), RegistroConductor.class);
      startActivity(intent);
    }
  };

  public Inicio() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pantalla_principal, container, false);

    Button buttonLogin = view.findViewById(R.id.btn_login);
    Button buttonRegistroApod = view.findViewById(R.id.btn_registro_apoderado);
    Button buttonRegistroCond = view.findViewById(R.id.btn_registro_conductor);
    buttonLogin.setOnClickListener(listenerLogin);
    buttonRegistroApod.setOnClickListener(listenerApod);
    buttonRegistroCond.setOnClickListener(listenerCon);
    return view;
  }

}
