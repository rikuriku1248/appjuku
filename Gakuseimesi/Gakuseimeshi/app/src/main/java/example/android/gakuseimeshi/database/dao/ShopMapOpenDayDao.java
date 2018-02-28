package example.android.gakuseimeshi.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import example.android.gakuseimeshi.database.basicData.MapOpenDay;
import example.android.gakuseimeshi.database.databaseHelper.SimpleDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomu on 2017/12/19.
 */

public class ShopMapOpenDayDao {
    private static final String TABLE_NAME = "shopopentime";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_OPENTIME1 = "opentime1";
    private static final String COLUMN_OPENTIME2 = "opentime2";
    private static final String COLUMN_OPENTIME3 = "opentime3";
    private static final String COLUMN_HOLIDAY = "holiday";
    private static final String[] COLUMNS = {COLUMN_NAME, COLUMN_OPENTIME1, COLUMN_OPENTIME2, COLUMN_OPENTIME3, COLUMN_HOLIDAY};

    private SQLiteDatabase db = null;
    private SimpleDatabaseHelper helper = null;
    protected Context context;

    /*public ShopMapViewDao(SQLiteDatabase db){
        this.db = db;
    }*/
    public ShopMapOpenDayDao(Context context){
        this.context = context;
        helper = new SimpleDatabaseHelper(this.context);
    }

    /**
     * DBの読み書き
     * openDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopMapOpenDayDao openDB() {
        db = helper.getWritableDatabase();        // DBの読み書き
        return this;
    }

    /**
     * DBの読み込み 今回は未使用
     * readDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopMapOpenDayDao readDB() {
        db = helper.getReadableDatabase();        // DBの読み込み
        return this;
    }

    /**
     * DBを閉じる
     * closeDB()
     */
    public void closeDB() {
        db.close();     // DBを閉じる
        db = null;
    }


    /**
     * 全データの取得   ----------------①
     * @return
     */
    public List<MapOpenDay> findAll(){
        List<MapOpenDay> mapOpenDays = new ArrayList<MapOpenDay>();
        Log.d("Error","opendayDao");
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                null,
                null,
                null,
                null,
                null);
        Log.d("Error",String.valueOf(cursor.getCount()));
        while(cursor.moveToNext()) {
            MapOpenDay mapOpenDay = new MapOpenDay();
            mapOpenDay.setName(cursor.getString(0));
            mapOpenDay.setOpentime1(cursor.getString(1));
            mapOpenDay.setHoliday(cursor.getString(2));
            mapOpenDays.add(mapOpenDay);
        }
        return mapOpenDays;
    }
}
