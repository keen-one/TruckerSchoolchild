package softnecessary.furgonok.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import softnecessary.furgonok.R;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.utils.Utilidades;


public class ContactAdapter1 extends RecyclerView.Adapter<ContactAdapter1.ViewHolder> {


  private ArrayList<Contact> myItems;

  private ItemListener myListener;
  private FragmentActivity activity;
  private Context context;
  private ItemLongListener listenerLong;


  public ContactAdapter1(ArrayList<Contact> items, ItemListener listener,
      ItemLongListener listenerLong, FragmentActivity activity,
      Context context) {
    this.myItems = items;
    this.context = context;
    this.myListener = listener;
    this.listenerLong = listenerLong;
    this.activity = activity;

  }

  /*
  void filter(String text) {
    if (myItems.size() > 0) {
      myItems.clear();
    }
    if (text.trim().equals("")) {

      myItems.addAll(itemsCopy);

    } else {
      text = text.toLowerCase();
      for (Contact item : itemsCopy) {
        if (item.getNombreUsuario().toLowerCase().contains(text)) {
          myItems.add(item);
        }
      }
    }
    notifyDataSetChanged();

  }*/

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

// --Commented out by Inspection START (20-10-2019 08:34):
//  public void setListener(ItemListener listener) {
//    myListener = listener;
//  }
// --Commented out by Inspection STOP (20-10-2019 08:34)

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_contact, parent, false)); // TODO
  }

  @Override
  public int getItemCount() {
    return myItems.size();
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    //holder.setData(myItems.get(position));
    Contact item = myItems.get(position);

    holder.bindContact(item);
    holder.imExcla.setVisibility(View.GONE);
    if (item.isTieneMensaje()) {
      holder.imExcla.setVisibility(View.VISIBLE);
    } else {
      holder.imExcla.setVisibility(View.GONE);
    }

    holder.contactTextView.setText(item.getJid());
    holder.tvApodo.setText(item.getApodo());
    String nombreUsuario = item.getNombreUsuario();
    String presencia = item.getPresencia();
    holder.tvDisponible.setText(presencia);
    holder.tvDisponible.setTextColor(
        presencia.equals("En linea") ? activity.getColor(R.color.amarillo)
            : activity.getColor(R.color.blanco));
    holder.tvNombreUsuario.setText(Utilidades.getAsterisco(nombreUsuario));
    holder.cvImgProfile.setBackgroundResource(item.getResource());
    holder.btnEliminarAmigo.setLongClickable(true);
    OnLongClickListener clickLongRemove = new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Utilidades util = new Utilidades();
        util.confirmarEliminarAmigo(item.getNombreUsuario(), activity, context);
        return true;
      }
    };
    holder.btnEliminarAmigo.setOnLongClickListener(clickLongRemove);
    if (!presencia.equals("En linea")) {
      this.myListener = null;
    }
  }

  public interface ItemListener {

    void onItemClick(Contact item);
  }

  public interface ItemLongListener {

    void onItemLongClick(Contact item);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
      View.OnLongClickListener {

    private TextView contactTextView;
    // TODO - Your view members
    private Contact item;
    private CardView cvImgProfile;
    private TextView tvApodo;
    private TextView tvNombreUsuario;
    private Button btnEliminarAmigo;

    private ImageView imExcla;
    private TextView tvDisponible;

    ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      itemView.setOnLongClickListener(this);

      contactTextView = itemView.findViewById(R.id.contact_jid);
      tvApodo = itemView.findViewById(R.id.tv_apodo);
      tvNombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
      cvImgProfile = itemView.findViewById(R.id.cv_img_profile);
      btnEliminarAmigo = itemView.findViewById(R.id.btnEliminarAmigo);
      imExcla = itemView.findViewById(R.id.img_excla);
      tvDisponible = itemView.findViewById(R.id.tvDisponible);
    }


    @Override
    public void onClick(View v) {
      if (myListener != null) {
        myListener.onItemClick(item);
      }
    }

    void bindContact(Contact contact) {
      item = contact;


    }

    @Override
    public boolean onLongClick(View v) {
      if (listenerLong != null) {
        listenerLong.onItemLongClick(item);
        return true;
      }
      return false;
    }
  }


}
                                