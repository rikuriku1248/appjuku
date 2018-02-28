package example.android.gakuseimeshi.gurunavi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Tomu on 2018/01/26.
 */

public class UploadAsyncTask extends AsyncTask<String, Void, String> {

    private Activity m_Activity;
    private Context m_context;
    public ProgressDialog m_ProgressDialog;



    public UploadAsyncTask(Activity activity, Context context) {
        // 呼び出し元のアクティビティ
        m_Activity = activity;
        m_context = context;
    }

    /*
     * 実行前の事前処理
     */
    @Override
    protected void onPreExecute() {

        // プログレスダイアログの生成
        this.m_ProgressDialog = new ProgressDialog(this.m_Activity);
        // プログレスダイアログの設定
        this.m_ProgressDialog.setMessage("Update Database");  // メッセージをセットUpload
        // プログレスダイアログの表示
        this.m_ProgressDialog.setCancelable(true);
        this.m_ProgressDialog.show();
        SearchKitFoodArea searchKitFoodArea = new SearchKitFoodArea(m_context);
        searchKitFoodArea.execute();
        SearchShopReview searchShopReview = new SearchShopReview(m_context);
        searchShopReview.execute();
        return;
    }

    @Override
    protected String doInBackground(String... ImagePath) {
        /*try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        //ここにdoInBackground終了後に行う処理を書く

        // プログレスダイアログを閉じる
        if (this.m_ProgressDialog != null && this.m_ProgressDialog.isShowing()) {
            this.m_ProgressDialog.dismiss();
        }

    }
    /*
    * キャンセル時の処理
    */
    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (this.m_ProgressDialog != null) {
            // プログレスダイアログ表示中の場合
            if (this.m_ProgressDialog.isShowing()) {

                // プログレスダイアログを閉じる
                this.m_ProgressDialog.dismiss();
            }
        }

        return;
    }


}