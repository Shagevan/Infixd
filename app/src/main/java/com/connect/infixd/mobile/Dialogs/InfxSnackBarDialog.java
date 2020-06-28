/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Dialogs;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by yasiru on 8/20/17.
 */

public class InfxSnackBarDialog extends InfxDialog {
    private Snackbar mSnackbar;
    private String messageText;
    private String actionText;

    public InfxSnackBarDialog(View view, View.OnClickListener mainActionListner) {
        super(view, mainActionListner);
        this.mSnackbar = Snackbar.make(view, "Infix Message!", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mainActionListner != null)
                            mainActionListner.onClick(v);
                        mSnackbar.dismiss();
                    }
                });
        mSnackbar.setActionTextColor(Color.WHITE);
    }

    public InfxSnackBarDialog(View view) {
        super(view);
        this.mSnackbar = Snackbar.make(view, "Infix Message!", Snackbar.LENGTH_SHORT);
        mSnackbar.setActionTextColor(Color.WHITE);
    }

    @Override
    public void show() {
        mSnackbar.show();
    }

    @Override
    public void dismiss() {
        mSnackbar.dismiss();
    }

    public InfxSnackBarDialog setMessageText(String text) {
        mSnackbar.setText(text);
        this.messageText = text;
        return this;
    }

    public InfxSnackBarDialog setActionText(String text) {
        mSnackbar.setAction(text, this.mainActionListner);
        this.messageText = text;
        return this;
    }
}
