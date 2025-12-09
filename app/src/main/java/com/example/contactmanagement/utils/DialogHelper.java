package com.example.contactmanagement.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.contactmanagement.R;

public class DialogHelper {

    public interface OnDialogActionListener {
        void onPositiveClick();
        void onNegativeClick();
    }

    /**
     * Show a confirmation dialog with custom title and message
     */
    public static Dialog showConfirmationDialog(
            Context context,
            String title,
            String message,
            OnDialogActionListener listener
    ) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
        Button btnPositive = dialog.findViewById(R.id.btnDialogPositive);
        Button btnNegative = dialog.findViewById(R.id.btnDialogNegative);

        tvTitle.setText(title);
        tvMessage.setText(message);

        btnPositive.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPositiveClick();
            }
            dialog.dismiss();
        });

        btnNegative.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNegativeClick();
            }
            dialog.dismiss();
        });

        dialog.show();
        return dialog;
    }

    /**
     * Show a success dialog with custom message
     */
    public static Dialog showSuccessDialog(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tvSuccessMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        tvMessage.setText(message);

        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        return dialog;
    }

    /**
     * Show a success dialog with custom message and callback
     */
    public static Dialog showSuccessDialog(
            Context context,
            String message,
            Runnable onDismiss
    ) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tvSuccessMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        tvMessage.setText(message);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        dialog.show();
        return dialog;
    }
}