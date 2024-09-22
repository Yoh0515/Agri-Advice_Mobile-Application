package com.example.finalthesis;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.MediaController;

public class FixedMediaController extends MediaController {

    public FixedMediaController(Context context) {
        super(context);
    }

    public void setAnchorView(ViewGroup view) {
        super.setAnchorView(view);
        // Adjust the layout parameters to place the controller at the bottom
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        requestLayout();
    }
}
