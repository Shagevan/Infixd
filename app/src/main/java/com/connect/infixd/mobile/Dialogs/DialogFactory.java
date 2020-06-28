/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Dialogs;


import android.view.View;

import com.connect.infixd.mobile.R;

/**
 * Creates a notification dialog
 */
public class DialogFactory {
    public static final String CONNECTION_ERROR = "CONNECTION_ERROR";
    public static final String REPORT_ERROR = "REPORT_ERROR";
    private static final DialogFactory instance = new DialogFactory();

    private DialogFactory() {

    }

    public static DialogFactory getInstance() {
        return instance;
    }

    public InfxDialog make(String type, View parent, View.OnClickListener mainActionListener) {
        InfxSnackBarDialog dialog;
        switch (type) {
            case CONNECTION_ERROR:
                dialog = new InfxSnackBarDialog(parent, mainActionListener);
                dialog.setActionText(parent.getResources().getString(R.string.dialog_connection_error_action));
                dialog.setMessageText(parent.getResources().getString(R.string.dialog_connection_error_message));
                break;
            case REPORT_ERROR:
                dialog = new InfxSnackBarDialog(parent, mainActionListener);
                dialog.setActionText(parent.getResources().getString(R.string.dialog_report_action));
                dialog.setMessageText(parent.getResources().getString(R.string.dialog_report_message));
                break;
            default:
                dialog = null;
        }
        return dialog;
    }

    public InfxDialog make(String message, View parent) {
        InfxSnackBarDialog dialog;
        dialog = new InfxSnackBarDialog(parent);
        dialog.setMessageText(message);
        return dialog;
    }
}
