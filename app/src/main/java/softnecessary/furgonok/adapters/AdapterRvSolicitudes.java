package softnecessary.furgonok.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.R;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.pojo.simple.UsuarioInvitado;
import softnecessary.furgonok.utils.Utilidades;


public class AdapterRvSolicitudes extends
    RecyclerView.Adapter<AdapterRvSolicitudes.ViewHolder> {

  private List<UsuarioInvitado> myItems;
  private ItemListener myListener;
  private Context context;
  private AppCompatActivity activity;

  public AdapterRvSolicitudes(List<UsuarioInvitado> items, ItemListener listener,
      AppCompatActivity activity, Context context) {
    myItems = items;
    myListener = listener;
    this.context = context;
    this.activity = activity;
  }


  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }


  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_invitado, parent, false)); // TODO
  }

  @Override
  public int getItemCount() {
    return myItems.size();
  }

  private void forceWrapContent(View v) {
    // Start with the provided view
    View current = v;

    // Travel up the tree until fail, modifying the LayoutParams
    do {
      // Get the parent
      ViewParent parent = current.getParent();

      // Check if the parent exists
      if (parent != null) {
        // Get the view
        try {
          current = (View) parent;
        } catch (ClassCastException e) {
          // This will happen when at the top view, it cannot be cast to a View
          break;
        }

        // Modify the layout
        current.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
      }
    } while (current.getParent() != null);

    // Request a layout to be re-done
    current.requestLayout();
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    UsuarioInvitado invitado = myItems.get(position);
    holder.setData(invitado);
    String apodo = myItems.get(position).getApodo();
    holder.tvApodo.setText(apodo);
    String usernameObtenido = myItems.get(position).getNombreUsuario();

    holder.tvNombreUsuario.setText(Utilidades.getAsterisco(usernameObtenido));
    holder.cvProfile.setBackgroundResource(R.drawable.profile_login_yellow);
    OnLongClickListener clickLongEliminar = new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        TextView tvMensaje = new TextView(context);
        tvMensaje.setText(R.string.confirm_remove_request);
        tvMensaje.setPadding(40, 40, 40, 40);

        tvMensaje.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams linearParam1 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvMensaje.setLayoutParams(linearParam1);
        dialog.setTitle("Denegación de solicitud de amistad");
        dialog.setView(tvMensaje);
        forceWrapContent(tvMensaje);
        OnClickListener clickPos = new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Runnable runnable = new Runnable() {
              @Override
              public void run() {
                String usuario = usernameObtenido.contains("@" + MyXMPP.DOMAIN) ? usernameObtenido
                    : usernameObtenido.concat("@").concat(MyXMPP.DOMAIN);

                AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
                if (conn != null) {

                  if (conn.isAuthenticated()) {
                    Roster roster = Utilidades.obtenerRoster(conn, activity);
                    if (roster != null) {

                      Presence subscribed = new Presence(Type.unsubscribe);
                      BareJid jid = null;
                      try {
                        jid = JidCreate.bareFrom(usuario);
                      } catch (XmppStringprepException ignored) {
                        Utilidades.showMsg(activity, "Nombre de usuario inválido");
                      }
                      if (jid != null) {
                        subscribed.setTo(jid);

                        try {
                          conn.sendStanza(subscribed);
                        } catch (NotConnectedException | InterruptedException e) {
                          Utilidades.showMsg(activity, e.getLocalizedMessage());
                        }
                        if (!roster.contains(jid)) {
                          myItems.remove(position);
                          Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                              notifyDataSetChanged();
                            }
                          };
                          activity.runOnUiThread(runnable);

                        }

                      }
                    }
                  }

                }
              }
            };
            Thread thread = new Thread(runnable);
            if (!thread.isAlive()) {
              thread.start();
            }

          }
        };
        dialog.setPositiveButton("Confirmar", clickPos);
        OnClickListener clickNeg = new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        };
        dialog.setNegativeButton("Cerrar", clickNeg);

        dialog.show();

        return true;
      }
    };
    holder.btnEliminar.setOnLongClickListener(clickLongEliminar);


  }

  interface ItemListener {

    void onItemClick(UsuarioInvitado item);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // TODO - Your view members
    private UsuarioInvitado item;
    private TextView tvNombreUsuario;
    private TextView tvApodo;
    private Button btnEliminar;
    private CardView cvProfile;
    //private Button btnAceptar;

    ViewHolder(View itemView) {
      super(itemView);

      itemView.setOnClickListener(this);
      // TODO instantiate/assign view members
      tvNombreUsuario = itemView.findViewById(R.id.tv_nombreUsuario);

      btnEliminar = itemView.findViewById(R.id.btn_eliminar);
      cvProfile = itemView.findViewById(R.id.cv_img_profile);
      btnEliminar = itemView.findViewById(R.id.btn_eliminar);
      //btnAceptar = itemView.findViewById(R.id.btn_aceptar);
      tvApodo = itemView.findViewById(R.id.tv_apodo);
    }

    void setData(UsuarioInvitado item) {
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
                                