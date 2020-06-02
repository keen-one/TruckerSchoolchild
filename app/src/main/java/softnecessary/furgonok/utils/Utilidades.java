package softnecessary.furgonok.utils;


import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_COMP_INTERSECT;
import static org.bytedeco.javacpp.opencv_imgproc.compareHist;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.opencv.core.CvType;
import softnecessary.furgonok.MiAplicacion;
import softnecessary.furgonok.PantallaInicial;
import softnecessary.furgonok.R;
import softnecessary.furgonok.messaging.MyXMPP;
import softnecessary.furgonok.pojo.serialized.Usuario;
import softnecessary.furgonok.pojo.simple.Contact;
import softnecessary.furgonok.pojo.simple.UsuarioInvitado;


public final class Utilidades {

  //private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";


  public Utilidades() {

  }

  /*
  public static String getRandomString(final int sizeOfRandomString) {
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(sizeOfRandomString);
    for (int i = 0; i < sizeOfRandomString; ++i) {
      sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
    }
    return sb.toString();
  }*/

  public static String getAsterisco(String nombreUsuario) {

    if (nombreUsuario == null) {
      return "";
    } else {
      if (nombreUsuario.trim().equals("")) {
        return "";
      }
    }
    if (nombreUsuario.contains("@" + MyXMPP.DOMAIN)) {
      String miNombreUsuario = nombreUsuario.substring(0, nombreUsuario.indexOf("@"));
      return getAsterisco(miNombreUsuario);
    }

    String nombreUsuarioNuevo = "****";
    char[] arrayChar = nombreUsuario.toCharArray();
    for (int cont = 0; cont < arrayChar.length; cont++) {
      char letra = arrayChar[cont];
      if (cont >= (arrayChar.length - 4)) {
        nombreUsuarioNuevo = nombreUsuarioNuevo.concat(String.valueOf(letra));
      }
    }
    return nombreUsuarioNuevo;
  }

  public static boolean setNuevoMensaje(String jidLLegado,
      boolean valor) {
    for (int index = 0; index < MiAplicacion.listContact.size(); index++) {
      Contact newObjMsg = MiAplicacion.listContact.get(index);
      if (jidLLegado != null) {
        if (jidLLegado.equals(newObjMsg.getJid())) {
          if (valor != newObjMsg.isTieneMensaje()) {
            newObjMsg.setTieneMensaje(valor);
            MiAplicacion.listContact.set(index, newObjMsg);
            return true;
          }


        }
      }

    }
    return false;
  }

  public static String getRealPathFromURI2(Uri uri, Context context) {

    File targetFile = new File(context.getExternalCacheDir(), "ic_licencia_conducir.png");

    try (InputStream initialStream = context.getContentResolver().openInputStream(uri)) {

      if (initialStream != null) {

        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        Files.write(targetFile.toPath(), buffer);
      }
    } catch (IOException ignored) {

    }

    return targetFile.exists() ? targetFile.getPath() : "";
  }

  public static void showMsg(FragmentActivity activity, String mensaje) {
    if (mensaje != null) {

      if (activity != null && !mensaje.equals("")) {

        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            SnackBarUtils snack = SnackBarUtils.getInstance();

            snack.showSnackBar(activity, mensaje);


          }
        };
        activity.runOnUiThread(runnable);
      }
    }
  }

  public static String getTextFromImage2(Context context, Bitmap bitmap) {
    final TessBaseAPI baseApi = new TessBaseAPI();
    baseApi.setDebug(true);
    boolean success = baseApi.init(getPathData(context), "spa");
    if (success) {

      baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
          "ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0123456789");
      //baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, ".-/!@#$%^&*()_+=[]}{;:'\"\\|~`,<>?");
      baseApi.setPageSegMode(PageSegMode.PSM_AUTO);
      //baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

      //Bitmap bitmapSrc1 = rotacion(bitmap1);

      baseApi.setImage(bitmap);
      final String outputText = baseApi.getUTF8Text();
      baseApi.end();

      if (!bitmap.isRecycled()) {
        bitmap.recycle();
      }
      return outputText;

    } else {

      return "";
    }


  }
  /*
  public static String getTextFromImage(Context context, Bitmap bitmap) {

    TessBaseAPI baseApi = new TessBaseAPI();
    boolean success = baseApi.init(getPathData(context), "spa");
    if (success) {
      baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
          "ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0123456789");
      baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?");

      Bitmap bitmapSrc1 = toGrayscale(bitmap);
      //Bitmap bitmapSrc = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_licencia_conducir);
      //Bitmap bitmapImage = toGrayscale(bitmapSrc);
      //Bitmap bitmapImage2 = rotacion(bitmapImage, 90);
      bitmapSrc1 = rotacion(bitmapSrc1);
      baseApi.setImage(bitmapSrc1);
      //baseApi.setImage(bitmapImage);
      String resultado = baseApi.getUTF8Text();
      baseApi.clear();
      baseApi.end();
      //bitmapImage.recycle();
      bitmapSrc1.recycle();
      //bitmapSrc.recycle();
      //bitmapImage2.recycle();
      return resultado;
    } else {
      return "nada";
    }

  }*/

  private static String getPathData(Context context) {
    String DATA_PATH = "";
    if (context.getExternalCacheDir() != null) {
      DATA_PATH = context.getExternalCacheDir().getPath() + "/AndroidOCR/";
    }

    String lang = "spa";
    AssetManager assetManager = context.getAssets();

    String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

    for (String path : paths) {
      File dir = new File(path);
      if (!dir.exists()) {
        if (!dir.mkdirs()) {

        } else {

        }
      }
    }

    if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
      try {
        InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
        OutputStream out = new FileOutputStream(
            new File(DATA_PATH + "tessdata/", lang + ".traineddata"));

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
          out.write(buf, 0, len);
        }
        in.close();
        out.close();


      } catch (IOException ignored) {

      }
    }
    return DATA_PATH;
  }

  //@RequiresApi(api = VERSION_CODES.Q)
  public static String insertImage(ContentResolver cr,
      Bitmap source,
      String title,
      String description) {

    ContentValues values = new ContentValues();
    values.put(Images.Media.TITLE, title);
    values.put(Images.Media.DISPLAY_NAME, title);
    values.put(Images.Media.DESCRIPTION, description);
    values.put(Images.Media.MIME_TYPE, "image/jpeg");
    // Add the date meta data to ensure the image is added at the front of the gallery
    values.put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
    values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());

    Uri url = null;
    String stringUrl = null;    /* value to be returned */

    try {
      url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

      if (source != null) {
        if (url != null) {

          try (OutputStream imageOut = cr.openOutputStream(url)) {
            source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
          }
        }
        //long id = ContentUris.parseId(url);
        // Wait until MINI_KIND thumbnail is generated.

        //Bitmap miniThumb = cr.loadThumbnail(url, id, Images.Thumbnails.MINI_KIND, null);
        //Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id, Images.Thumbnails.MINI_KIND, null);
        // This is for backward compatibility.
        //storeThumbnail(cr, miniThumb, id, 50F, 50F,Images.Thumbnails.MICRO_KIND);
      } else {
        if (url != null) {
          cr.delete(url, null, null);
        }

        url = null;
      }
    } catch (Exception e) {
      if (url != null) {
        cr.delete(url, null, null);
        url = null;
      }
    }

    if (url != null) {
      stringUrl = url.toString();
    }

    return stringUrl;
  }

  /*
  private static final Bitmap storeThumbnail(
      ContentResolver cr,
      Bitmap source,
      long id,
      float width,
      float height,
      int kind) {

    // create the matrix to scale it
    Matrix matrix = new Matrix();

    float scaleX = width / source.getWidth();
    float scaleY = height / source.getHeight();

    matrix.setScale(scaleX, scaleY);

    Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
        source.getWidth(),
        source.getHeight(), matrix,
        true
    );

    ContentValues values = new ContentValues(4);
    values.put(Images.Thumbnails.KIND,kind);
    values.put(Images.Thumbnails.IMAGE_ID,(int)id);
    values.put(Images.Thumbnails.HEIGHT,thumb.getHeight());
    values.put(Images.Thumbnails.WIDTH,thumb.getWidth());

    Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

    try {
      OutputStream thumbOut = cr.openOutputStream(url);
      thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
      thumbOut.close();
      return thumb;
    } catch (FileNotFoundException ex) {
      return null;
    } catch (IOException ex) {
      return null;
    }
  }*/
  public static Bitmap toGrayscale(Bitmap bmpOriginal) {
    int width;
    int height;

    height = bmpOriginal.getHeight();
    width = bmpOriginal.getWidth();

    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmpGrayscale);
    c.drawColor(Color.WHITE);
    Paint paint = new Paint();
    ColorMatrix cm = new ColorMatrix();
    cm.setSaturation(0);
    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
    paint.setColorFilter(f);
    c.drawBitmap(bmpOriginal, 0, 0, paint);
    return bmpGrayscale;
  }

  private static Bitmap rotacion(Bitmap source) {
    Matrix matrix = new Matrix();
    matrix.postRotate(90);
    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
  }
  /*
  public static Bitmap rotacion1(Bitmap bitmapOrg) {

    int width = bitmapOrg.getWidth();

    int height = bitmapOrg.getHeight();

    int newWidth = 200;

    int newHeight = 200;

    // calculate the scale - in this case = 0.4f

    float scaleWidth = ((float) newWidth) / width;

    float scaleHeight = ((float) newHeight) / height;

    Matrix matrix = new Matrix();

    matrix.postScale(scaleWidth, scaleHeight);
    matrix.postRotate((float) 90);

    return Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
  }*/

  public static boolean compareImage(String img1, String img2) {
    Mat A = imread(img1, IMREAD_GRAYSCALE);
    Mat B = imread(img2, IMREAD_GRAYSCALE);

    Mat resizeimage = new Mat();
    Size sz = new Size(A.arrayWidth(), A.arrayHeight());
    resize(B, resizeimage, sz);

    A.convertTo(A, CvType.CV_32F);
    resizeimage.convertTo(resizeimage, CvType.CV_32F);

    try {
      double total = compareHist(resizeimage, resizeimage, CV_COMP_INTERSECT);
      total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
      double valor = compareHist(resizeimage, A, CV_COMP_INTERSECT);
      valor = new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
      double resultado = valor * 100 / total;
      return Double.compare(resultado, 40) > 0 && Double.compare(resultado, 92) < 0;
    } catch (Exception ignored) {

    }
    return false;
  }

  public static int reconocerCara(FragmentActivity activity, String directory, String objFile) {
    try {

      String testImg = directory + File.separator + objFile;

      Mat testImage = imread(testImg, IMREAD_GRAYSCALE);

      File root = new File(directory);

      FilenameFilter imgFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          name = name.toLowerCase();
          return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
        }
      };

      File[] imageFiles = root.listFiles(imgFilter);

      MatVector images = null;
      if (imageFiles != null) {
        images = new MatVector(imageFiles.length);
      }

      Mat labels = null;
      if (imageFiles != null) {
        labels = new Mat(imageFiles.length, 1, CV_32SC1);
      }
      IntBuffer labelsBuf = null;
      if (labels != null) {
        labelsBuf = labels.createBuffer();
      }

      int counter = 0;

      if (imageFiles != null) {
        for (File image : imageFiles) {
          if (image.exists()) {

            Mat img = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
          }
        }
      }

      //FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
      // FaceRecognizer faceRecognizer = EigenFaceRecognizer.create();
      FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();

      faceRecognizer.train(images, labels);

      IntPointer label = new IntPointer(1);
      DoublePointer confidence = new DoublePointer(1);
      faceRecognizer.predict(testImage, label, confidence);

      return label.get(0);
    } catch (Exception e) {
      Utilidades.showMsg(activity, e.getLocalizedMessage());
    }
    return -1;
  }

  public static Roster obtenerRoster(AbstractXMPPConnection conn, FragmentActivity activity) {
    if (conn != null) {
      if (conn.isAuthenticated()) {

        Roster roster = Roster.getInstanceFor(conn);
        if (!roster.isLoaded()) {
          try {
            roster.reloadAndWait();
          } catch (NotLoggedInException | NotConnectedException | InterruptedException e) {
            Utilidades.showMsg(activity, e.getLocalizedMessage());
          }
        }

        roster.setSubscriptionMode(SubscriptionMode.accept_all);
        return roster;
      }

    }
    return null;

  }

  /*
  public MiApodo getApodoByUsername(String username, String tipo, FragmentActivity activity,
      Context context) {
    SnackBarUtils snack = SnackBarUtils.getInstance();

    MisPreferencias pref = new MisPreferencias(context);
    int miId = pref.loadID();
    String email = pref.loadCorreoLogin();
    String password = pref.loadPasswordLogin();
    String authToken = pref.loadToken();
    MiApodo miApodo = new MiApodo();
    if (miId != -1 && !email.equals("") && !password.equals("") && !authToken.equals("")) {

      if (tipo.equals(PantallaInicial.APODERADO)) {
        ApoderadoDAO dao = new ApoderadoDAO(
            activity.getString(R.string.huella), activity.getString(R.string.key_pass));
        Object objeto = dao.getApodoByUsername(miId, authToken, username);
        if (objeto instanceof Mensaje) {
          String mensaje = ((Mensaje) objeto).getMsg();
          snack.showSnackBar(activity, mensaje);

        } else if (objeto instanceof MiApodo) {
          miApodo = (MiApodo) objeto;
        }

      } else if (tipo.equals(PantallaInicial.CONDUCTOR)) {
        ConductorDAO dao = new ConductorDAO(
            activity.getString(R.string.huella), activity.getString(R.string.key_pass));
        Object objeto = dao.getApodoByUsername(miId, authToken, username);
        if (objeto instanceof Mensaje) {
          String mensaje = ((Mensaje) objeto).getMsg();
          snack.showSnackBar(activity, mensaje);
        } else if (objeto instanceof MiApodo) {
          miApodo = (MiApodo) objeto;
        }

      }

    } else {
      snack.showSnackBar(activity, "Usuario inautenticado");

    }
    return miApodo;
  }
*/
  /*
  private void initRoster(AbstractXMPPConnection connection, Roster roster) throws InterruptedException, SmackException, XmppStringprepException {
    roster.reload();
    while (true) {
      final Stanza sentPacket = connection.stanza
      if (sentPacket instanceof RosterPacket && ((IQ) sentPacket).getType() == Type.get) {
        // setup the roster get request
        final RosterPacket rosterRequest = (RosterPacket) sentPacket;
        assertSame("The <query/> element MUST NOT contain any <item/> child elements!",
            0,
            rosterRequest.getRosterItemCount());

        // prepare the roster result
        final RosterPacket rosterResult = new RosterPacket();
        rosterResult.setTo(connection.getUser());
        rosterResult.setType(Type.result);
        rosterResult.setStanzaId(rosterRequest.getStanzaId());

        // prepare romeo's roster entry
        final Item romeo = new Item(JidCreate.entityBareFrom("romeo@example.net"), "Romeo");
        romeo.addGroupName("Friends");
        romeo.setItemType(ItemType.both);
        rosterResult.addRosterItem(romeo);

        // prepare mercutio's roster entry
        final Item mercutio = new Item(JidCreate.entityBareFrom("mercutio@example.com"), "Mercutio");
        mercutio.setItemType(ItemType.from);
        rosterResult.addRosterItem(mercutio);

        // prepare benvolio's roster entry
        final Item benvolio = new Item(JidCreate.entityBareFrom("benvolio@example.net"), "Benvolio");
        benvolio.setItemType(ItemType.both);
        rosterResult.addRosterItem(benvolio);

        // simulate receiving the roster result and exit the loop
        connection.processStanza(rosterResult);
        break;
      }
    }
    roster.waitUntilLoaded();
    rosterListener.waitUntilInvocationOrTimeout();
  }*/
  public static String replaceIpWithServer(String string) {
    String PATTERN = "((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)";
    return string.replaceAll(PATTERN, "server");
  }

  public final Notification shoWNotification(Context context, String mensaje) {

    NotificationManager notificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = context.getString(R.string.app_name);
    NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId,
        NotificationManager.IMPORTANCE_DEFAULT);
    notificationChannel.setDescription(channelId);
    notificationChannel.setSound(null, null);
    if (notificationManager != null) {
      notificationManager.createNotificationChannel(notificationChannel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

    builder.setAutoCancel(true);

    builder.setSmallIcon(R.drawable.bus_escolar);
    Bitmap miBitmap = BitmapFactory
        .decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
    bigText.bigText(mensaje);
    builder.setStyle(bigText);
    builder.setColor(context.getColor(R.color.amarillo));
    builder.setBadgeIconType(R.mipmap.ic_launcher_round);
    builder.setLargeIcon(miBitmap);
    builder.setContentTitle(context.getString(R.string.app_name));
    builder.setContentText(mensaje);
    builder.setDefaults(NotificationCompat.DEFAULT_ALL);
    return builder.build();
  }

  public final void getAmigos(AbstractXMPPConnection conn1, String tipo,
      FragmentActivity activity) {

    Roster roster = Utilidades.obtenerRoster(conn1, activity);
    if (roster != null) {

      Set<RosterEntry> rosterEntries1 = roster.getEntries();

      for (RosterEntry rosterEntry : rosterEntries1) {
        String jid = rosterEntry.getJid().asUnescapedString();
        if (MiAplicacion.miUsername != null) {
          if (!MiAplicacion.miUsername.equals("")) {
            String actualUsername =
                MiAplicacion.miUsername.contains("@" + MyXMPP.DOMAIN) ? MiAplicacion.miUsername
                    : MiAplicacion.miUsername.concat("@").concat(MyXMPP.DOMAIN);

            if (!jid.equals(actualUsername)) {
              if (!rosterEntry.isSubscriptionPending()) {
                MiAplicacion.listContact = addUserToList(roster, tipo, rosterEntry);
              } else {
                if (rosterEntry.getName() != null) {
                  if (!rosterEntry.getName().equals("")) {
                    UsuarioInvitado invitado = new UsuarioInvitado();
                    invitado.setNombreUsuario(jid);
                    invitado.setApodo(rosterEntry.getName());
                    if (!MiAplicacion.listUsuarioInvitado.contains(invitado)) {
                      if (findInvitado(jid)) {
                        MiAplicacion.listUsuarioInvitado.add(invitado);
                      }

                    }
                  }
                }


              }
            }
          }
        }

      }
      roster.addRosterListener(new RosterListener() {
        @Override
        public void entriesAdded(Collection<Jid> addresses) {
          MiAplicacion.listContact = addUserToList(roster, tipo,
              roster.getEntry(addresses.iterator().next().asBareJid()));
        }

        @Override
        public void entriesUpdated(Collection<Jid> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<Jid> addresses) {
          for (Contact contact : MiAplicacion.listContact) {
            if (contact.getJid()
                .equals(addresses.iterator().next().asBareJid().asUnescapedString())) {
              MiAplicacion.listContact = removeItem(contact);
            }
          }
        }

        @Override
        public void presenceChanged(Presence presence) {
          Presence.Type tipo = presence.getType();
          if (tipo == Presence.Type.available || tipo == Presence.Type.unavailable) {

            MisPreferencias pref = new MisPreferencias(activity);
            String nombreUsuario = pref.loadNombreUsuario();
            String jidFrom = presence.getFrom().asUnescapedString();
            String jidTo = presence.getTo().asUnescapedString();
            String presencia = presence.getStatus();
            if (presencia == null) {
              presencia = "En linea";
            } else {

              if (presencia.equals("En linea") || presencia.equals("")) {
                presencia = "En linea";
              } else {
                presencia = "Desconectado";
              }
            }
            if (!nombreUsuario.equals("")) {
              String nombreUsuario2 = nombreUsuario.contains("@" + MyXMPP.DOMAIN) ? nombreUsuario
                  : nombreUsuario.concat("@").concat(MyXMPP.DOMAIN);
              for (int cont = 0; cont < MiAplicacion.listContact.size(); cont++) {
                Contact contact = MiAplicacion.listContact.get(cont);
                if (contact.getJid().equalsIgnoreCase(jidFrom) && !jidFrom.equals(nombreUsuario2)) {
                  contact.setPresencia(presencia);
                  MiAplicacion.listContact.set(cont, contact);
                }
                if (contact.getJid().equalsIgnoreCase(jidTo) && !jidTo.equals(nombreUsuario2)) {
                  contact.setPresencia(presencia);
                  MiAplicacion.listContact.set(cont, contact);
                }
              }
            }

          }
        }
      });
    }
  }

  private ArrayList<Contact> removeItem(Contact contact) {

    MiAplicacion.listContact.remove(contact);

    return MiAplicacion.listContact;
  }

  private ArrayList<Contact> addUserToList(Roster roster, String tipo, RosterEntry rosterEntry) {
    if (!rosterEntry.isSubscriptionPending()
        && rosterEntry.getName() != null) {
      if (!rosterEntry.getName().equals("")) {
        String user = rosterEntry.getJid().asUnescapedString();

        if (MiAplicacion.miUsername != null) {
          if (!MiAplicacion.miUsername.equals("")) {
            String actualUsername =
                MiAplicacion.miUsername.contains("@" + MyXMPP.DOMAIN) ? MiAplicacion.miUsername
                    : MiAplicacion.miUsername.concat("@").concat(MyXMPP.DOMAIN);

            if (!user.equals(actualUsername)) {

              Contact contact = new Contact();
              BareJid bareJid = null;
              try {
                bareJid = JidCreate.bareFrom(user);
              } catch (XmppStringprepException ignored) {

              }
              if (bareJid != null) {
                Presence presenciaUser = roster.getPresence(bareJid);
                contact.setPresencia(
                    presenciaUser.getType() == Presence.Type.available ? "En linea"
                        : "Desconectado");
              }

              contact.setApodo(rosterEntry.getName());
              contact.setNombreUsuario(user.substring(0, user.indexOf("@")));
              contact.setJid(user);
              if (tipo.equals(PantallaInicial.APODERADO)) {
                contact.setResource(R.drawable.ic_driver2);
              } else if (tipo.equals(PantallaInicial.CONDUCTOR)) {
                contact.setResource(R.mipmap.ic_agent);
              }

              if (tipo.equals(PantallaInicial.APODERADO)) {
                if (!MiAplicacion.listContact.contains(contact) && !user
                    .equals("staff@im.koderoot.net")) {
                  if (findContact(contact.getJid())) {
                    if (MiAplicacion.listContact.size() == 0) {
                      MiAplicacion.listContact.add(contact);
                    }
                    if (MiAplicacion.listContact.size() == 1) {
                      MiAplicacion.listContact.set(0, contact);
                    }

                  }

                }


              } else if (tipo.equals(PantallaInicial.CONDUCTOR)) {
                if (!MiAplicacion.listContact.contains(contact) && !user
                    .equals("staff@im.koderoot.net")) {
                  if (findContact(contact.getJid())) {
                    MiAplicacion.listContact.add(contact);
                  }

                }

              }
            }
          }
        }
      }

    }
    return MiAplicacion.listContact;
  }

  private boolean findContact(String jid) {
    for (Contact contact : MiAplicacion.listContact) {
      if (contact.getJid().equals(jid)) {
        return false;
      }
    }
    return true;
  }

  public final boolean findUsuario(String nombreUsuario, List<Usuario> arrayList) {
    for (Usuario usuario : arrayList) {
      if (nombreUsuario.equals(usuario.getNombreUsuario())) {
        return false;
      }
    }
    return true;
  }

  public final double stringToDouble(String string, double defaultValor) {
    double valor;
    try {
      valor = Double.parseDouble(string);
    } catch (Exception e) {
      valor = defaultValor;
    }
    return valor;
  }

  //devuelve en kilometros
  public final double distance(double lat1, double lon1, double lat2, double lon2) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1))
        * Math.sin(deg2rad(lat2))
        + Math.cos(deg2rad(lat1))
        * Math.cos(deg2rad(lat2))
        * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    return (dist);
  }

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }

  private boolean findInvitado(String nombreUsuario) {
    for (UsuarioInvitado invitado : MiAplicacion.listUsuarioInvitado) {
      if (invitado.getNombreUsuario().equals(nombreUsuario)) {
        return false;
      }
    }
    return true;
  }

  public final void confirmarEliminarAmigo(String username, FragmentActivity activity,
      Context context) {
    AlertDialog.Builder dialog = new Builder(context);
    dialog.setTitle("Elimina el contacto de amigos");
    TextView textView2 = new TextView(context);
    TextView textView1 = new TextView(context);
    textView1.setText("El usuario ".concat(username).concat(" sera quitado de la lista"));

    textView2.setText("¿Esta seguro que desea quitar el contacto?");
    textView1.setGravity(Gravity.CENTER);
    textView2.setGravity(Gravity.CENTER);
    int padding = 40;
    textView1.setPadding(padding, padding, padding, padding);
    textView2.setPadding(padding, padding, padding, padding);
    LinearLayout linearLayout = new LinearLayout(context);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setGravity(Gravity.CLIP_VERTICAL);
    linearLayout.setPadding(2, 2, 2, 2);
    LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams linearParam1 = new LinearLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams linearParam2 = new LinearLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT);
    linearLayout.setLayoutParams(linearParam);
    linearLayout.addView(textView1, 0, linearParam1);
    linearLayout.addView(textView2, 1, linearParam2);
    dialog.setView(linearLayout);
    DialogInterface.OnClickListener clickPos = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            removeActualAmigo(username, activity);
          }
        };
        Thread thread = new Thread(runnable);
        if (!thread.isAlive()) {
          thread.start();
        }
      }
    };
    dialog.setPositiveButton("Quitar contacto", clickPos);
    DialogInterface.OnClickListener clickNeg = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    };
    dialog.setNegativeButton("Cerrar", clickNeg);
    dialog.show();
  }

  public final void removeActualAmigo(String username, FragmentActivity activity) {
    String nombreUsuario = username.contains("@" + MyXMPP.DOMAIN) ? username
        : username.concat("@").concat(MyXMPP.DOMAIN);
    if (nombreUsuario.equals("staff@im.koderoot.net")) {
      return;
    }

    SnackBarUtils snack = SnackBarUtils.getInstance();
    //String myUsername = pref.loadNombreUsuario();
    //String passwordChat = pref.loadPasswordChat();

    //MyXMPP xmpp = new MyXMPP(activity);
    //AbstractXMPPConnection conn = xmpp.logear(myUsername, passwordChat);
    AbstractXMPPConnection conn = MiAplicacion.conexionXMPP;
    if (conn == null) {
      return;
    }
    BareJid jid = null;
    try {
      jid = JidCreate.bareFrom(nombreUsuario);
    } catch (XmppStringprepException ignored) {
      Utilidades.showMsg(activity, "Nombre de usuario inválido");
    }
    if (jid != null) {
      RosterPacket paquete = new RosterPacket();
      paquete.setType(Type.set);
      RosterPacket.Item item = new RosterPacket.Item(jid, null);
      item.setItemType(ItemType.remove);
      paquete.addRosterItem(item);
      try {
        conn.createStanzaCollectorAndSend(paquete);
      } catch (NotConnectedException | InterruptedException e) {
        snack.showSnackBar(activity, e.getLocalizedMessage());
        return;
      }
      snack.showSnackBar(activity, "Amigo eliminado de contactos");
    } else {
      snack.showSnackBar(activity, "Nombre de usuario inválido");
    }


  }

  public final String generateUsername() {
    return UUID.randomUUID().toString().replace("-", "");
  }


}
