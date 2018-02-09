package example.android.samplemap3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
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
    private int duration;

    //コンストラクタ
    public DetailAnimation(View view, int preY, int newY, int duration){
        this.view = view;
        this.preY = preY;
        this.newY = newY;
        this.detail_fragment_container = MapsActivity2.detail_fragment_container;
        this.duration = duration;
    }

    public void setAnimation(){
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

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
