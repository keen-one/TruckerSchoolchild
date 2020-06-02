package softnecessary.furgonok.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import softnecessary.furgonok.BuildConfig;
import softnecessary.furgonok.R;

public final class SimpleEula {

  private Activity mActivity;

  public SimpleEula(Activity context) {
    mActivity = context;
  }


  public final void show() {
    if (mActivity == null) {
      return;
    }

    // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
    String EULA_PREFIX = "eula_";
    String miVersion = BuildConfig.VERSION_NAME;
    String eulaKey = EULA_PREFIX.concat(miVersion);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
    boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
    if (!hasBeenShown) {

      // Show the Eula
      String title = mActivity.getString(R.string.app_name) + " v" + miVersion;

      AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
          .setTitle(title)
          .setMessage(getTextFromFile())
          .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              // Mark this version as read.
              SharedPreferences.Editor editor = prefs.edit();
              editor.putBoolean(eulaKey, true);
              editor.apply();
              dialogInterface.dismiss();
            }
          })
          .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
              // Close the activity as they have declined the EULA
              mActivity.finish();
            }

          });
      builder.create().show();
    }
  }

  private String getTextFromFile() {
    BufferedReader buffer = null;
    try {
      InputStreamReader inStream = new InputStreamReader(
          mActivity.getAssets().open("eula_licencia.txt"), StandardCharsets.UTF_8);
      buffer = new BufferedReader(inStream);
      String linea;
      StringBuilder builder = new StringBuilder();
      if (builder.length() <= 0) {

        while ((linea = buffer.readLine()) != null) {
          builder.append(linea).append("\n");
        }

      }
      return builder.toString();

    } catch (IOException ignored) {

    } finally {
      if (buffer != null) {
        try {
          buffer.close();
        } catch (IOException ignored) {

        }
      }
    }

    return "";
  }
}
