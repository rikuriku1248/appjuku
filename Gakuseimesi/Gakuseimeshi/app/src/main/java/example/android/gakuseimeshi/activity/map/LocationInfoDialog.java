package example.android.gakuseimeshi.activity.map;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by riku on 2018/02/13.
 */

public class LocationInfoDialog extends DialogFragment {

    private AlertDialog.Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle bundle){
        return  new AlertDialog.Builder(getActivity())
                .setMessage("続行するには、端末の位置情報をONにしてください" + "\n" +
                        "(Googleの位置情報サービスを使用します)")
                .setPositiveButton("設定する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
