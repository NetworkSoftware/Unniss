package pro.network.unniss.app;

import android.content.Context;
import android.util.AttributeSet;

public class ProportionalImageView extends androidx.appcompat.widget.AppCompatImageView {

    public ProportionalImageView(Context context) {
        super(context);
    }

    public ProportionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProportionalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = (PreferenceBean.getInstance().width);
        int h = 9 * w / 16;
        setMeasuredDimension(w, h);
    }
}