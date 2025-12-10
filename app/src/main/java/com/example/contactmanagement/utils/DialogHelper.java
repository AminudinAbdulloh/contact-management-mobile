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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class DialogHelper {

    private static Dialog loadingDialog;

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

    /**
     * Show error dialog with custom message
     */
    public static Dialog showErrorDialog(
            Context context,
            String message
    ) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvErrorMessage = dialog.findViewById(R.id.tvErrorMessage);
        Button btnErrorOk = dialog.findViewById(R.id.btnErrorOk);

        tvErrorMessage.setText(message);

        btnErrorOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        return dialog;
    }

    /**
     * Show loading dialog with custom title and message
     */
    public static Dialog showLoadingDialog(
            Context context,
            String title
    ) {
        // Dismiss previous dialog if exists
        dismissLoadingDialog();

        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        TextView tvLoadingTitle = loadingDialog.findViewById(R.id.tvLoadingTitle);

        tvLoadingTitle.setText(title);

        loadingDialog.show();
        return loadingDialog;
    }

    /**
     * Dismiss loading dialog
     */
    public static void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * Check if loading dialog is showing
     */
    public static boolean isShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    // Method 1: Logika penerjemah error (REUSABLE LOGIC)
    public static String getReadableErrorMessage(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            return "Server sedang sibuk. Periksa koneksi Anda atau coba beberapa saat lagi.";
        } else if (t instanceof UnknownHostException || t instanceof IOException) {
            return "Tidak ada koneksi internet. Pastikan WiFi atau Data Seluler aktif.";
        } else {
            return "Terjadi kesalahan sistem (" + t.getMessage() + ").";
        }
    }

    // Method 2: Versi overload untuk langsung menampilkan dialog dari Throwable
    public static void showFailureDialog(Context context, Throwable t) {
        String friendlyMessage = getReadableErrorMessage(t);
        showErrorDialog(context, friendlyMessage);
    }
}