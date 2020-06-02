package softnecessary.furgonok.dbmsg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import softnecessary.furgonok.utils.Contantes;

public class MsgHelper extends SQLiteOpenHelper {

  private String tabla;

  public MsgHelper(@Nullable Context context,
      @Nullable String name,
      @Nullable CursorFactory factory, int version) {
    super(context, name, factory, version);
    tabla = "CREATE TABLE IF NOT EXISTS " + Contantes.TABLE_NAME + " ("
        + Contantes.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + Contantes.COLUMN_MENSAJE + " VARCHAR NOT NULL,"
        + Contantes.COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
        + Contantes.COLUMN_TYPE + " VARCHAR NOT NULL,"
        + Contantes.COLUMN_SENDER + " VARCHAR NOT NULL"
        + ");";
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(tabla);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + Contantes.TABLE_NAME);
    onCreate(db);
  }

}
