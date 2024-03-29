package com.cyno.reminder.calender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * An {@linkplain android.widget.ImageView} to pragmatically set the color of arrows
 * using a {@linkplain android.graphics.ColorFilter}
 */
class DirectionButton extends ImageView {

    public DirectionButton(Context context) {
        this(context, null);
    }

    public DirectionButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundResource(getThemeSelectableBackgroundId(context));
    }

    public void setColor(int color) {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @SuppressLint("NewApi")
	@Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.1f);
    }

    private static int getThemeSelectableBackgroundId(Context context) {
            //Get selectableItemBackgroundBorderless defined for AppCompat
        int colorAttr = context.getResources().getIdentifier(
                "selectableItemBackgroundBorderless", "attr", context.getPackageName());

        if(colorAttr == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.selectableItemBackgroundBorderless;
            } else {
                colorAttr = android.R.attr.selectableItemBackground;
            }
        }

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.resourceId;
    }
}
