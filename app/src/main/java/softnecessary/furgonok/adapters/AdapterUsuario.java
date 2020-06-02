package softnecessary.furgonok.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import softnecessary.furgonok.R;
import softnecessary.furgonok.pojo.serialized.Usuario;
import softnecessary.furgonok.utils.Utilidades;


public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.ViewHolder> {

  private List<Usuario> myItems;
  private ItemListener myListener;

  public AdapterUsuario(List<Usuario> items, ItemListener listener) {
    myItems = items;
    myListener = listener;
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }


  @androidx.annotation.NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_usuario, parent, false)); // TODO
  }

  @Override
  public int getItemCount() {
    return myItems.size();
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.tvApodo.setText(myItems.get(position).getApodo());
    String nombreUsuario1 = myItems.get(position).getNombreUsuario();
    holder.tvNombreUsuario.setText(Utilidades.getAsterisco(nombreUsuario1));
    String distancia = myItems.get(position).getDistancia();
    Utilidades util = new Utilidades();
    double distancia2 = util.stringToDouble(distancia, 0.0);
    String lugar = myItems.get(position).getLugar();
    if (!lugar.equals("")) {
      holder.tvLugar.setVisibility(View.VISIBLE);
    } else {
      holder.tvLugar.setVisibility(View.GONE);
    }
    holder.tvLugar.setText("Ubicación: ".concat(lugar.toLowerCase()));
    holder.tvDistancia.setText(String.format("Distancia: %.2f", distancia2));
    if (!distancia.equals("") || distancia2 != 0.0) {
      holder.tvDistancia.setVisibility(View.VISIBLE);
    } else {
      holder.tvDistancia.setVisibility(View.GONE);
    }

    holder.tvLongitudInicial.setText(myItems.get(position).getLongitudInicial());
    holder.tvLatitudInicial.setText(myItems.get(position).getLatitudInicial());
    holder.tvLongitudFinal.setText(myItems.get(position).getLongitudFinal());
    holder.tvLatitudFinal.setText(myItems.get(position).getLatitudFinal());
    holder.setData(myItems.get(position));
  }

  public interface ItemListener {

    void onItemClick(Usuario item);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // TODO - Your view members
    private Usuario item;
    private TextView tvNombreUsuario;
    private TextView tvApodo;
    private TextView tvDistancia;
    private TextView tvLatitudInicial;
    private TextView tvLongitudInicial;
    private TextView tvLatitudFinal;
    private TextView tvLongitudFinal;
    private TextView tvLugar;

    ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      // TODO instantiate/assign view members
      tvApodo = itemView.findViewById(R.id.tv_apodo);
      tvNombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);

      tvDistancia = itemView.findViewById(R.id.tv_distancia);
      tvLatitudInicial = itemView.findViewById(R.id.tv_latitud_inicial);
      tvLongitudInicial = itemView.findViewById(R.id.tv_longitud_inicial);
      tvLatitudFinal = itemView.findViewById(R.id.tv_latitud_final);
      tvLongitudFinal = itemView.findViewById(R.id.tv_longitud_final);
      tvLugar = itemView.findViewById(R.id.tv_lugar);
    }

    void setData(Usuario item) {
      this.item = item;
      // TODO set data to view
    }

    @Override
    public void onClick(View v) {
      if (myListener != null) {
        myListener.onItemClick(item);
      }
    }
  }


}
                                