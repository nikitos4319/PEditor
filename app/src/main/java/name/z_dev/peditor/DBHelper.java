package name.z_dev.peditor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Home on 07.08.2015.
 */
class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "posteditorDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table accountstable ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "url text,"
                + "login text,"
                + "password text,"
                + "apikey text"+ ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}