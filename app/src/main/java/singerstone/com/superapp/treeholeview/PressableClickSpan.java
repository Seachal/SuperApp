package singerstone.com.superapp.treeholeview;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import androidx.annotation.CallSuper;

public abstract class PressableClickSpan extends ClickableSpan {

    private boolean mPressed = false;

    public void setPressed(boolean pressed) {
        mPressed = pressed;
    }

    protected abstract int getPressedColor();

    @Override
    @CallSuper
    public void updateDrawState(TextPaint ds) {
        ds.bgColor = mPressed ? getPressedColor() : Color.TRANSPARENT;
    }
}
