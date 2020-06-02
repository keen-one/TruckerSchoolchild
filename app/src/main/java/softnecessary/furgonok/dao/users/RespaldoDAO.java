package softnecessary.furgonok.dao.users;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import softnecessary.furgonok.dbmsg.MsgHelper;
import softnecessary.furgonok.pojo.simple.MensajeChat;
import softnecessary.furgonok.utils.Contantes;

public final class RespaldoDAO {

  private MsgHelper helper;

  public RespaldoDAO(Context context) {
    helper = new MsgHelper(context, Contantes.DB_NAME, null, 2);
  }

  public final void ingresar(MensajeChat msg) {
    SQLiteDatabase db = helper.getWritableDatabase();
    ContentValues content = new ContentValues();
    content.put(Contantes.COLUMN_MENSAJE, msg.getMensaje());
    content.put(Contantes.COLUMN_SENDER, msg.getSender());
    content.put(Contantes.COLUMN_TIMESTAMP, msg.getTimestamp());
    content.put(Contantes.COLUMN_TYPE, msg.getTipo());
    db.insertWithOnConflict(Contantes.TABLE_NAME, Contantes.COLUMN_ID, content,
        SQLiteDatabase.CONFLICT_REPLACE);
    db.close();
  }

  public final List<MensajeChat> consultar(String sender) {
    List<MensajeChat> list = new ArrayList<>();
    SQLiteDatabase db = helper.getReadableDatabase();
    String sql =
        "SELECT * FROM " + Contantes.TABLE_NAME + " WHERE " + Contantes.COLUMN_SENDER + "=?";
    Cursor cursor = db.rawQuery(sql, new String[]{sender});
    if (cursor.moveToFirst()) {
      do {
        String mensaje = cursor.getString(cursor.getColumnIndex(Contantes.COLUMN_MENSAJE));
        String tipo = cursor.getString(cursor.getColumnIndex(Contantes.COLUMN_TYPE));
        String sender1 = cursor.getString(cursor.getColumnIndex(Contantes.COLUMN_SENDER));
        long timestamp = cursor.getLong(cursor.getColumnIndex(Contantes.COLUMN_TIMESTAMP));
        MensajeChat msg = new MensajeChat();
        msg.setMensaje(mensaje);
        msg.setSender(sender1);
        msg.setTimestamp(timestamp);
        msg.setTipo(tipo);
        if (!list.contains(msg)) {
          list.add(msg);
        }
      } while (cursor.moveToNext());
    }
    cursor.close();
    db.close();
    return list;
  }

  public final void borrarMas250Filas(String sender) {
    String[] argument = new String[]{sender};
    SQLiteDatabase db = helper.getReadableDatabase();
    long numLength = DatabaseUtils
        .queryNumEntries(db, Contantes.TABLE_NAME, Contantes.COLUMN_SENDER + " = ?", argument);
    long limit = 250;
    db.close();
    if (numLength > limit) {
      long diferencia = numLength - limit;
      String sql = "DELETE FROM " + Contantes.TABLE_NAME + " LIMIT " + diferencia + " WHERE "
          + Contantes.COLUMN_SENDER + "= ? ORDER BY " + Contantes.COLUMN_TIMESTAMP + " ASC;";
      SQLiteDatabase db2 = helper.getWritableDatabase();
      db2.execSQL(sql, argument);
      db2.close();
    }

  }
}
