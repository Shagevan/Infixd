/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Dialogs;

import android.view.View;

/**
 * Created by yasiru on 8/20/17.
 */

public abstract class InfxDialog {
    protected View context;
    protected View.OnClickListener mainActionListner;

    public InfxDialog(View parentView, View.OnClickListener mainActionListner) {
        this.context = parentView;
        this.mainActionListner = mainActionListner;
    }

    public InfxDialog(View parentView) {
        this.context = parentView;
    }

    public abstract void show();

    public abstract void dismiss();

}
