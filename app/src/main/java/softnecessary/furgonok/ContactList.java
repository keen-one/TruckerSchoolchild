package softnecessary.furgonok;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import softnecessary.furgonok.adapters.ContactAdapter1;
import softnecessary.furgonok.adapters.ContactAdapter1.ItemListener;
import softnecessary.furgonok.adapters.ContactAdapter1.ItemLongListener;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.utils.MisPreferencias;
import softnecessary.furgonok.utils.Utilidades;

public class ContactList extends Fragment {

  // --Commented out by Inspection (20-10-2019 08:34):public static final String TAG = ContactList.class.getSimpleName();
  private ContactAdapter1 mAdapter;
  private Gson gson = new Gson();
  private ToggleButton btnOrden;
  private EditText etApodo;
  private MisPreferencias pref;
  private ImageView imgBus;
  private FragmentActivity activity;
  private IncomingChatMessageListener listenerEntrada = new IncomingChatMessageListener() {
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
      String sender = from.asEntityBareJidString();
      if (sender != null) {
        if (Utilidades.setNuevoMensaje(sender, true)) {
          Runnable runnableUIMsg = new Runnable() {
            @Override
            public void run() {
              mAdapter.notifyDataSetChanged();
            }
          };
          if (activity != null) {
            activity.runOnUiThread(runnableUIMsg);
          }


        }
      }

    }
  };

  public ContactList() {
  }

  @Override
  public void onStop() {
    FragmentActivity activity = getActivity();
    if (activity != null) {
      MiAplicacion.runServiceNotificacion(activity);
    }

    super.onStop();
  }

  /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
    }*/
/*
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
*/
  @Override
  public void onStart() {
    super.onStart();
    FragmentActivity activity = getActivity();
    if (activity != null) {
      MiAplicacion.runServiceRespaldo(activity);
      MiAplicacion.stopServiceNotification(activity);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_contact_list, container, false);

    AppCompatActivity context = (AppCompatActivity) getActivity();
    if (context != null) {
      ActionBar actionbar = context.getSupportActionBar();
      if (actionbar != null) {
        context.getSupportActionBar().setTitle(getString(R.string.amigos));
      }
    }

    pref = new MisPreferencias(getActivity());
    RecyclerView contactsRecyclerView = view.findViewById(R.id.contact_list_recycler_view);
    LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
    contactsRecyclerView.setLayoutManager(linearLayout);
    imgBus = view.findViewById(R.id.imgBus);
    btnOrden = view.findViewById(R.id.btn_orden);
    btnOrden.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reorder, 0, 0, 0);
    OnClickListener clickToggle = new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (MiAplicacion.listContact.size() > 0) {
          if (btnOrden.isChecked()) {
            Collections.sort(MiAplicacion.listContact, new Comparator<Contact>() {
              @Override
              public int compare(Contact o1, Contact o2) {
                if (o1.getApodo().compareToIgnoreCase(o2.getApodo()) < 0) {
                  return -1;
                } else {
                  return 0;
                }
              }
            });
            mAdapter.notifyDataSetChanged();
          } else {

            Collections.sort(MiAplicacion.listContact, new Comparator<Contact>() {
              @Override
              public int compare(Contact o1, Contact o2) {
                if (o1.getJid().compareToIgnoreCase(o2.getJid()) < 0) {
                  return -1;
                } else {
                  return 0;
                }

              }
            });
            mAdapter.notifyDataSetChanged();
          }
        }

      }
    };
    btnOrden.setOnClickListener(clickToggle);
    Button btnInviteFriend = view.findViewById(R.id.btn_invite_user);
    OnClickListener clickInviteFriend = new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AdicionContacto.class);
        startActivity(intent);
      }
    };
    btnInviteFriend.setOnClickListener(clickInviteFriend);
    ImageButton btnUpdateAmigos = view.findViewById(R.id.btnUpdateAmigos);
    OnClickListener clickUpdateAmigos = new OnClickListener() {
      @Override
      public void onClick(View v) {
        actualizarListaAmigos(true);
      }
    };
    btnUpdateAmigos.setOnClickListener(clickUpdateAmigos);
    //ContactModel model = ContactModel.get(this);

    //ArrayList<Contact> contacts = new ArrayList<>();

    ContactAdapter1.ItemListener listener = new ItemListener() {
      @Override
      public void onItemClick(Contact item) {
        FragmentActivity activity = getActivity();
        if (activity != null) {

          if (Utilidades.setNuevoMensaje(item.getJid(), false)) {
            Runnable runnableUi15 = new Runnable() {
              @Override
              public void run() {
                mAdapter.notifyDataSetChanged();
              }
            };
            activity.runOnUiThread(runnableUi15);
          }
          Intent intent = new Intent(activity, ChatActivity.class);
          intent.putExtra("DATA", gson.toJson(item, Contact.class));
          startActivity(intent);
        }
      }
    };
    ContactAdapter1.ItemLongListener listenerLong = new ItemLongListener() {
      @Override
      public void onItemLongClick(Contact item) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
          AlertDialog.Builder diag = new Builder(activity);
          diag.setTitle("Cambio apodo del amigo");
          etApodo = new EditText(activity);
          etApodo.setPadding(20, 20, 20, 20);
          etApodo.setGravity(Gravity.CENTER);
          etApodo.setHint("Apodo");
          etApodo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
          LinearLayout.LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT,
              LayoutParams.WRAP_CONTENT);
          etApodo.setLayoutParams(param);
          diag.setView(etApodo);
          DialogInterface.OnClickListener clickNeg = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          };
          diag.setNegativeButton("Cerrar", clickNeg);
          DialogInterface.OnClickListener clickPos = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Runnable runnable = new Runnable() {
                @Override
                public void run() {
                  String apodoNew = etApodo.getText().toString().trim();
                  if (MiAplicacion.conexionXMPP != null && !apodoNew.equals("")) {
                    if (!MiAplicacion.conexionXMPP.isAuthenticated()) {
                      logearMensajeria(activity);
                    }
                    if (MiAplicacion.conexionXMPP.isAuthenticated()) {
                      cambiarApodoActual(apodoNew, activity, item);
                    }

                  }
                  if (MiAplicacion.conexionXMPP == null && !apodoNew.equals("")) {
                    logearMensajeria(activity);
                    if (MiAplicacion.conexionXMPP != null) {
                      if (MiAplicacion.conexionXMPP.isAuthenticated()) {
                        cambiarApodoActual(apodoNew, activity, item);
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
          diag.setPositiveButton("Cambiar nombre", clickPos);
          diag.show();
          //etApodo.setHeight();
        }

      }
    };

    if (BuildConfig.DEBUG) {
      mAdapter = new ContactAdapter1(MiAplicacion.listContact, listener, listenerLong,
          getActivity(),
          getActivity());
    } else {
      if (MiAplicacion.miStatePurchase != PurchaseState.PURCHASED) {
        ArrayList<Contact> listaVacia = new ArrayList<>(1);
        mAdapter = new ContactAdapter1(listaVacia, null, null, getActivity(),
            getActivity());
        imgBus.setVisibility(View.VISIBLE);
      } else {
        mAdapter = new ContactAdapter1(MiAplicacion.listContact, listener, listenerLong,
            getActivity(),
            getActivity());
      }
    }
    contactsRecyclerView.setAdapter(mAdapter);
    contactsRecyclerView.setHasFixedSize(true);

    //String username = pref.loadNombreUsuario();
    //String passwordChat = pref.loadPasswordChat();
    //if (!username.equals("") && !passwordChat.equals("")) {

    //}

    actualizarListaAmigos(false);
    if (BuildConfig.DEBUG || MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {
      activity = getActivity();
      if (activity != null) {
        Runnable runnableMsgRecibido = new Runnable() {
          @Override
          public void run() {

            if (MiAplicacion.conexionXMPP != null) {
              if (MiAplicacion.conexionXMPP.isAuthenticated()) {
                ChatManager chatManager = ChatManager.getInstanceFor(MiAplicacion.conexionXMPP);
                chatManager.addIncomingListener(listenerEntrada);
              }

            }
          }
        };
        Thread thread = new Thread(runnableMsgRecibido);
        if (!thread.isAlive()) {
          thread.start();
        }
      }


    }
    return view;
  }

  private void actualizarListaAmigos(boolean bandera) {
    if (BuildConfig.DEBUG || MiAplicacion.miStatePurchase == PurchaseState.PURCHASED) {
      if (bandera && MiAplicacion.listContact.size() > 0) {
        MiAplicacion.listContact.clear();
      }

      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          String tipo = pref.loadTypeUser();
          /*String username=pref.loadNombreUsuario();
          String passwordChat=pref.loadPasswordChat();
          if(!username.equals("")&&!passwordChat.equals("")){
            MyXMPP xmpp = new MyXMPP(getActivity());
            AbstractXMPPConnection connInit = xmpp.init();
            xmpp.setConexionListener(connInit, true);
            AbstractXMPPConnection connConnect = xmpp.conectar(connInit);
            AbstractXMPPConnection connLoggin = xmpp.logear2(connConnect, username, passwordChat);
          }*/

          //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, passwordChat);
          AbstractXMPPConnection connLoggin = MiAplicacion.conexionXMPP;
          if (connLoggin != null) {

            Utilidades util = new Utilidades();
            FragmentActivity activity = getActivity();
            util.getAmigos(connLoggin, tipo, activity);
            Runnable runnableUI = new Runnable() {
              @Override
              public void run() {
                if (MiAplicacion.listContact.size() > 0) {
                  imgBus.setVisibility(View.GONE);
                } else {
                  imgBus.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();

              }
            };

            if (activity != null) {
              activity.runOnUiThread(runnableUI);
            }
          }
        }
      };
      Thread thread = new Thread(runnable);
      if (!thread.isAlive()) {
        thread.start();
      }
    }

  }

  private void logearMensajeria(FragmentActivity activity) {
    MisPreferencias pref = new MisPreferencias(activity);
    String username = pref.loadNombreUsuario();
    String password = pref.loadPasswordChat();
    if (!password.equals("") && !username.equals("")) {
      MyXMPP xmpp = new MyXMPP(null);
      AbstractXMPPConnection connInit = xmpp.init();
      xmpp.setConexionListener(connInit, true);
      AbstractXMPPConnection connConnect = xmpp.conectar(connInit);

      xmpp.logear2(connConnect, username, password);

    }
  }

  private void cambiarApodoActual(String apodoNew, FragmentActivity activity, Contact item) {
    Roster roster = Utilidades.obtenerRoster(MiAplicacion.conexionXMPP, activity);
    if (roster != null) {

      try {
        RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(item.getJid()));
        entry.setName(apodoNew);
      } catch (XmppStringprepException e) {
        Utilidades.showMsg(activity, "Error, apodo invalido");
        return;
      } catch (InterruptedException e) {
        Utilidades.showMsg(activity, "Error, conexión interrumpida");
        return;
      } catch (XMPPErrorException | NoResponseException e) {
        Utilidades.showMsg(activity, e.getLocalizedMessage());
        return;
      } catch (NotConnectedException e) {
        Utilidades.showMsg(activity, "Error, desconectado");
        return;
      }
      for (int cont = 0; cont < MiAplicacion.listContact.size(); cont++) {
        Contact contacto = MiAplicacion.listContact.get(cont);
        if (contacto.getApodo().equalsIgnoreCase(item.getApodo()) && contacto.getJid()
            .equals(item.getJid())) {
          contacto.setApodo(apodoNew);

          MiAplicacion.listContact.set(cont, contacto);

          Runnable runnableUI = new Runnable() {
            @Override
            public void run() {
              mAdapter.notifyDataSetChanged();
            }
          };
          activity.runOnUiThread(runnableUI);
          Utilidades.showMsg(activity, "¡Apodo cambiado con exito!");
          return;
        }
      }
    }
    Utilidades.showMsg(activity, "Fue imposible cambiar el nombre, intenta mas tarde");
  }

}
