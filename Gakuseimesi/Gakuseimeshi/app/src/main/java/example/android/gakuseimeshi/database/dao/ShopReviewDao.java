package example.android.gakuseimeshi.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import example.android.gakuseimeshi.database.basicData.ReviewData;
import example.android.gakuseimeshi.database.databaseHelper.SimpleDatabaseHelper;

/**
 * Created by Tomu on 2018/02/23.
 */

public class ShopReviewDao {
    private static final String TABLE_NAME = "shopreview";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COMMENT = "comment";
    private static final String COLUMN_IMAGE = "image";
    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_COMMENT, COLUMN_IMAGE};

    private SQLiteDatabase db = null;
    private SimpleDatabaseHelper helper = null;
    protected Context context;

    /*public ShopMapViewDao(SQLiteDatabase db){
        this.db = db;
    }*/
    public ShopReviewDao(Context context){
        this.context = context;
        helper = new SimpleDatabaseHelper(this.context);
    }

    /**
     * DBの読み書き
     * openDB()
     *
     * @return this 自身のオブジェクト)
     */
    public ShopReviewDao openDB() {
        db = helper.getWritableDatabase();        // DBの読み書き
        return this;
    }

    /**
     * DBの読み込み 今回は未使用
     * readDB()
     *
     * @return this 自身のオブジェクト
     */
    public ShopReviewDao readDB() {
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

    public void replaceDB(List<ReviewData> reviewDatas){
        for(int i=0; i < reviewDatas.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, reviewDatas.get(i).getId());
            contentValues.put(COLUMN_NAME, reviewDatas.get(i).getName());
            Log.d("COLUMN_NAME",reviewDatas.get(i).getName());
            Log.d("COLUMN_ID",String.valueOf(reviewDatas.get(i).getId()));
            contentValues.put(COLUMN_COMMENT, reviewDatas.get(i).getComment());
            contentValues.put(COLUMN_IMAGE, reviewDatas.get(i).getImage());
            String wherClause = "id = ?";
            String whereArgs[] = new String[1];
            whereArgs[0] = String.valueOf(reviewDatas.get(i).getId());
            int updateData;
            updateData = db.update(TABLE_NAME, contentValues, wherClause, whereArgs);
            Log.d("Error", "updatedata");
            if(updateData <= 0) {
                Log.d("Error", "add data");
                db.insert(TABLE_NAME, "{}", contentValues);
            }
        }
    }


    /**
     * 全データの取得   ----------------①
     * @return
     */
    public List<ReviewData> findAll(){

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
    public List<ReviewData> searchId(int id){
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
     * 名前検索
     * @param name
     * @return
     */
    public List<ReviewData> searchName(String name){
        String whereText = COLUMN_NAME + " LIKE ?";
        String[] name_str = {"%" + name +"%"};
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                whereText,
                name_str,
                null,
                null,
                null);
        return getDatabaseData(cursor);
    }



    public List<ReviewData> getDatabaseData(Cursor cursor){
        List<ReviewData> reviewDataList = new ArrayList<ReviewData>();
        Log.d("cursor", "aaaa");
        while(cursor.moveToNext()) {
            Log.d("cursor", String.valueOf(cursor.getString(1)));
            ReviewData reviewData = new ReviewData();
            reviewData.setId(cursor.getInt(0));
            reviewData.setName(cursor.getString(1));
            reviewData.setComment(cursor.getString(2));
            reviewData.setImage(cursor.getString(3));
            reviewDataList.add(reviewData);
        }

        return reviewDataList;
    }
}
