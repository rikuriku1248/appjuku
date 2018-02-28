package example.android.gakuseimeshi.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import example.android.gakuseimeshi.database.basicData.LocationInformation;
import example.android.gakuseimeshi.database.databaseHelper.SimpleDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomu on 2017/12/19.
 */

public class ShopLocationDao {
    private static final String TABLE_NAME = "shoplocation";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_LATITUDE, COLUMN_LONGITUDE};

    private SQLiteDatabase db = null;
    private SimpleDatabaseHelper helper = null;
    protected Context context;

    /*public ShopMapViewDao(SQLiteDatabase db){
        this.db = db;
    }*/
    public ShopLocationDao(Context context){
        this.context = context;
        helper = new SimpleDatabaseHelper(this.context);
    }

    /**
     * DBの読み書き
     * openDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopLocationDao openDB() {
        db = helper.getWritableDatabase();        // DBの読み書き
        return this;
    }

    /**
     * DBの読み込み 今回は未使用
     * readDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopLocationDao readDB() {
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

    public void replaceDB(List<LocationInformation> locationInformations){
        for(int i = 0; i < locationInformations.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, locationInformations.get(i).getName());
            contentValues.put(COLUMN_LATITUDE, String.valueOf(locationInformations.get(i).getLatitude()));
            contentValues.put(COLUMN_LONGITUDE, String.valueOf(locationInformations.get(i).getLongitude()));
            String wherClause = "name = ?";
            String whereArgs[] = new String[1];
            whereArgs[0] = String.valueOf(locationInformations.get(i).getName());
            //whereArgs[1] = String.valueOf(locationInformations.get(i).getLongitude());
            int updateData;
            updateData = db.update(TABLE_NAME, contentValues, wherClause, whereArgs);
            if(updateData <= 0){
                db.insert(TABLE_NAME, "0", contentValues);
            }
        }
    }


    /**
     * 全データの取得   ----------------①
     * @return
     */
    public List<LocationInformation> findAll(){

        Log.d("Error","lodao1");
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                null,
                null,
                null,
                null,
                null);
        Log.d("Error",String.valueOf(cursor.getCount()));
        return getDatabaseData(cursor);
    }

    /**
     * id検索
     * @param id
     * @return
     */
    public List<LocationInformation> searchId(int id){
        String whereText = COLUMN_ID + " = ?";
        String[] id_str = {String.valueOf(id)};
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                whereText,
                id_str,
                null,
                null,
                null);
        return getDatabaseData(cursor);
    }

    public List<LocationInformation> getDatabaseData(Cursor cursor){
        List<LocationInformation> locationList = new ArrayList<LocationInformation>();
        while(cursor.moveToNext()) {
            LocationInformation locationInformation = new LocationInformation();
            locationInformation.setId(cursor.getInt(0));
            locationInformation.setName(cursor.getString(1));
            locationInformation.setLatitude(cursor.getFloat(2));
            locationInformation.setLongitude(cursor.getFloat(3));
            locationList.add(locationInformation);
        }

        return locationList;
    }
}
