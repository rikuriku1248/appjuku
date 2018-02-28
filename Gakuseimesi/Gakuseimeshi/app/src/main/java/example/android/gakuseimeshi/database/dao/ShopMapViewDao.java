package example.android.gakuseimeshi.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import example.android.gakuseimeshi.database.basicData.MapData;
import example.android.gakuseimeshi.database.databaseHelper.SimpleDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomu on 2017/12/14.
 */

public class ShopMapViewDao {
    private static final String TABLE_NAME = "shopmapview";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NAMEKANA = "name_kana";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_TEL = "tel";
    private static final String COLUMN_OPENTIME = "opentime";
    private static final String COLUMN_HOLIDAY = "holiday";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_FAVORITE = "favorite";
    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_NAMEKANA, COLUMN_ADDRESS, COLUMN_TEL, COLUMN_OPENTIME, COLUMN_HOLIDAY, COLUMN_IMAGE, COLUMN_FAVORITE};

    private SQLiteDatabase db = null;
    private SimpleDatabaseHelper helper = null;
    protected Context context;

    /*public ShopMapViewDao(SQLiteDatabase db){
        this.db = db;
    }*/
    public ShopMapViewDao(Context context){
        this.context = context;
        helper = new SimpleDatabaseHelper(this.context);
    }

    /**
     * DBの読み書き
     * openDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopMapViewDao openDB() {
        db = helper.getWritableDatabase();        // DBの読み書き
        return this;
    }

    /**
     * DBの読み込み 今回は未使用
     * readDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopMapViewDao readDB() {
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

    public void replaceDB(List<MapData> mapDatas){
        for(int i = 0; i < mapDatas.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, mapDatas.get(i).getName());
            contentValues.put(COLUMN_NAMEKANA, mapDatas.get(i).getNameKana());
            contentValues.put(COLUMN_ADDRESS, mapDatas.get(i).getAddress());
            contentValues.put(COLUMN_TEL, mapDatas.get(i).getTel());
            contentValues.put(COLUMN_OPENTIME, mapDatas.get(i).getOpentime());
            contentValues.put(COLUMN_HOLIDAY, mapDatas.get(i).getHoliiday());
            contentValues.put(COLUMN_IMAGE, mapDatas.get(i).getImage());
            //String wherClause = "name = ? AND tel = ?";
            String wherClause = "name = ? AND name_kana = ?";
            String whereArgs[] = new String[2];
            whereArgs[0] = String.valueOf(mapDatas.get(i).getName());
            whereArgs[1] = String.valueOf(mapDatas.get(i).getNameKana());
            Log.d("Error", mapDatas.get(i).getImage());
            int updateData = db.update(TABLE_NAME, contentValues, wherClause, whereArgs);
            if(updateData <= 0){
                contentValues.put(COLUMN_FAVORITE, 0);
                Log.d("Error",String.valueOf(contentValues));
                db.insert(TABLE_NAME, "0", contentValues);
            }
        }
    }


    /**
     * 全データの取得   ----------------①
     * @return
     */
    public List<MapData> findAll(){
        List<MapData> mapDataList = new ArrayList<MapData>();
        Log.d("Error","dao1");
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
    public List<MapData> searchId(int id){
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


    /**
     * 検索結果をListで取得
     * @param cursor
     * @return
     */
    private List<MapData> getDatabaseData(Cursor cursor) {
        List<MapData> mapDataList = new ArrayList<MapData>();
        while(cursor.moveToNext()) {
            MapData mapData = new MapData();
            mapData.setId(cursor.getInt(0));
            mapData.setName(cursor.getString(1));
            mapData.setNameKana(cursor.getString(2));
            mapData.setAddress(cursor.getString(3));
            mapData.setTel(cursor.getString(4));
            mapData.setOpentime(cursor.getString(5));
            mapData.setHoliday(cursor.getString(6));
            mapData.setImage(cursor.getString(7));
            mapData.setFavorite(cursor.getInt(8));
            mapDataList.add(mapData);
        }
        Log.d("Error",String.valueOf(cursor.getCount()));
        return mapDataList;
    }

}
