package softnecessary.furgonok.utils;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.material.snackbar.Snackbar;

public final class SnackBarUtils {

  private static SnackBarUtils mInstance = null;
  private Snackbar mSnackBar;

  private SnackBarUtils() {

  }

  public static SnackBarUtils getInstance() {
    if (mInstance == null) {
      mInstance = new SnackBarUtils();
    }
    return mInstance;
  }

  public final void showSnackBar(Activity activity, String message) {
    if (activity != null) {

      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          mSnackBar = Snackbar
              .make(activity.findViewById(android.R.id.content), message,
                  Snackbar.LENGTH_INDEFINITE);
          mSnackBar.setAction("Cerrar", new OnClickListener() {
            @Override
            public void onClick(View v) {
              mSnackBar.dismiss();
            }
          });
          // Changing action button text color
          //View sbView = mSnackBar.getView();
          //TextView textView = (TextView) sbView.findViewById(android.R.id.);
          //textView.setTextColor(Color.YELLOW);
          mSnackBar.show();
        }
      };
      activity.runOnUiThread(runnable);
    }
  }
}