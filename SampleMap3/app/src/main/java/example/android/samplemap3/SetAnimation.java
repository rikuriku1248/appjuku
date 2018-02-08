package example.android.samplemap3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Animatable;
import android.view.View;

/**
 * Created by riku on 2018/02/05.
 */

public class SetAnimation implements Animator.AnimatorListener{
    //アニメーションさせたいオブジェクトを格納
    private View view;
    private int preValue;
    private int newValue;
    private int x_or_y;

    public SetAnimation(View view, int preValue, int newValue, int x_or_y){
        this.view = view;
        this.preValue = preValue;
        this.newValue = newValue;
        this.x_or_y = x_or_y;
    }

    private void setAnimationProperty(){
        if(x_or_y == 0) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this.view, "translationX", preValue, newValue);
        }else if(x_or_y == 1){
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this.view, "translationY", preValue, newValue);
        }

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
