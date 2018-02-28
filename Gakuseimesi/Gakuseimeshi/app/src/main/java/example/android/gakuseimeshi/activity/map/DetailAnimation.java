package example.android.gakuseimeshi.activity.map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by riku on 2018/02/09.
 */

public class DetailAnimation implements Animator.AnimatorListener{

    //アニメーションさせたいオブジェクトを格納
    private View view;
    private int preY;
    private int newY;
    private FrameLayout detail_fragment_container;
    //アニメーションの再生時間
    private int duration;
    private int animationMode = 0;

    //コンストラクタ
    protected DetailAnimation(View view, int preY, int newY, int duration, int animationMode){
        this.view = view;
        this.preY = preY;
        this.newY = newY;
        this.detail_fragment_container = MapsActivity2.detail_fragment_container;
        this.duration = duration;
        this.animationMode = animationMode;
    }

    protected void setAnimation(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", preY, newY);
        objectAnimator.addListener(this);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
        Log.d("DetailAnimation", "setAnimation");
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        //DetailFragmentのサイズを半分にし, ListViewが全て見えるようにする
        if(animationMode == 2) {
            ViewGroup.LayoutParams params = MapsActivity2.detail_fragment_view.getLayoutParams();
            params.height = MapsActivity2.maps_view_height / 2;
            MapsActivity2.detail_fragment_view.setLayoutParams(params);
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        Log.d("DetailAnimation", "onAnimationCancel");
    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
