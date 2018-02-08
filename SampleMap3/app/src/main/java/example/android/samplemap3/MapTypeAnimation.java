package example.android.samplemap3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by riku on 2018/02/05.
 */

public class MapTypeAnimation implements Animator.AnimatorListener{

    //アニメーションさせたいオブジェクトを格納
    private View view;
    //
    private int preX;
    private int newX;
    private FrameLayout fragment_container;
    private int duration;

    //コンストラクタ
    public MapTypeAnimation(View view, int preX, int newX, int duration){
        this.view = view;
        this.preX = preX;
        this.newX = newX;
        this.fragment_container = MapsActivity2.fragment_container;
        this.duration = duration;
    }

    public void setAnimation(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", preX, newX);
        objectAnimator.addListener(this);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
        Log.d("MapTypeAnimation", "setAnimation");
    }

    @Override
    public void onAnimationStart(Animator animator) {
        Log.d("MapTypeAnimation", "onAnimationStart");

    }

    @Override
    public void onAnimationEnd(Animator animator) {
/*
        RelativeLayout.LayoutParams f_lp = (RelativeLayout.LayoutParams) MapsActivity2.fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams f_mlp = f_lp;
        f_mlp.setMargins(-MapsActivity2.maps_view_width, f_mlp.topMargin, MapsActivity2.maps_view_width, f_mlp.bottomMargin);
        MapsActivity2.fragment_container.setLayoutParams(f_mlp);
*/
        Log.d("MapTypeAnimation", "onAnimationEnd");
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
