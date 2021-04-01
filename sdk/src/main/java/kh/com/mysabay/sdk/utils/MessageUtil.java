package kh.com.mysabay.sdk.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.lang3.StringUtils;

import kh.com.mysabay.sdk.R;

/**
 * Created by PhirumTan on 26/01/2016.
 */
public class MessageUtil {

    public static void displayToast(Context context, int msg) {
        displayToast(context, context.getString(msg));
    }

    public static void displayToast(Context context, String msg) {
        if (context == null) return;

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflate == null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            View v = inflate.inflate(R.layout.view_custom_toast, null);
            AppCompatTextView tv = v.findViewById(R.id.txt_message);
            tv.setText(msg);

            Toast toast = new Toast(context);
            toast.setView(v);
            toast.setGravity(Gravity.BOTTOM, 0, 200);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void displaySneakBar(View coordinatorLayout, int message, String action, final View.OnClickListener listener) {
        if (coordinatorLayout != null) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
            if (!StringUtils.isBlank(action)) {
                snackbar.setAction(action, view -> {
                    if (listener != null) {
                        listener.onClick(view);
                    }
                });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                AppCompatTextView textView = new AppCompatTextView(coordinatorLayout.getContext());
                textView.setTextColor(Color.WHITE);
                snackbar.setDuration(4000);
            }
            snackbar.show();
        } else LogUtil.error("Error", "coordinateLayout null");
    }

    public static void displaySneakBar(View coordinatorLayout, int message) {
        displaySneakBar(coordinatorLayout, message, null, null);
    }

    public static void displayDialog(Context context, int msg) {
        displayDialog(context, context.getString(msg));
    }

    public static void displayDialog(Context context, String msg) {
        new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmer(context), FontUtils.getTypefaceKhmer(context))
                .content(msg)
                .backgroundColorRes(R.color.colorBackground)
                .contentColorRes(R.color.secondary)
                .positiveColorRes(R.color.colorYellow)
                .positiveText(R.string.label_close)
                .onPositive((dialog, which) -> dialog.dismiss())
                .build().show();

    }

    public static void displayDialog(Context context, String msg, int color) {
        new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmer(context), FontUtils.getTypefaceKhmer(context))
                .content(msg)
                .backgroundColorRes(color)
                .contentColorRes(R.color.secondary)
                .positiveColorRes(R.color.colorYellow)
                .positiveText(R.string.label_close)
                .onPositive((dialog, which) -> dialog.dismiss())
                .build().show();

    }

    public static void displayDialog(Context context, int title, String msg, int txtPos, MaterialDialog.SingleButtonCallback posListener) {
        displayDialog(context, title, msg, 0, txtPos, null, posListener);
    }

    public static void displayDialog(Context context, String msg, int color, String txtPos, MaterialDialog.SingleButtonCallback posListener) {
        new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmerBold(context), FontUtils.getTypefaceKhmer(context))
                .titleColorRes(R.color.colorYellow)
                .backgroundColorRes(color)
                .content(msg).canceledOnTouchOutside(false)
                .positiveColorRes(R.color.colorYellow)
                .contentColorRes(R.color.secondary)
                .positiveText(txtPos)
                .onPositive((dialog, which) -> {
                    if (posListener != null)
                        posListener.onClick(dialog, which);
                    else
                        dialog.dismiss();
        }).build().show();
    }

    public static void displayDialog(Context context, String title, String msg, String txtNeg, String txtPos, int color,
                                     MaterialDialog.SingleButtonCallback negListener,
                                     MaterialDialog.SingleButtonCallback posListener) {
        new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmerBold(context), FontUtils.getTypefaceKhmer(context))
                .title(title)
                .titleColorRes(R.color.colorYellow)
                .backgroundColorRes(color)
                .content(msg).canceledOnTouchOutside(false)
                .positiveColorRes(R.color.colorYellow)
                .contentColorRes(R.color.secondary)
                .negativeText(txtNeg)
                .positiveText(txtPos)
                .negativeColorRes(R.color.colorYellow)
                .onNegative((dialog, which) -> {
                    if (negListener != null)
                        negListener.onClick(dialog, which);
                    else
                        dialog.dismiss();
                }).onPositive((dialog, which) -> {
            if (posListener != null)
                posListener.onClick(dialog, which);
            else
                dialog.dismiss();
        }).build().show();
    }

    public static void displayDialog(Context context, int title, String msg, int txtNeg, int txtPos,
                                     MaterialDialog.SingleButtonCallback negListener,
                                     MaterialDialog.SingleButtonCallback posListener) {
        new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmerBold(context), FontUtils.getTypefaceKhmer(context))
                .title((title > 0 && StringUtils.isBlank(context.getString(title))) ? 0 : title)
                .content(msg).canceledOnTouchOutside(false)
                .negativeText(txtNeg > 0 ? txtNeg : R.string.label_close)
                .positiveText(txtPos == 0 ? R.string.ok : txtPos)
                .onNegative((dialog, which) -> {
                    if (negListener != null)
                        negListener.onClick(dialog, which);
                    else
                        dialog.dismiss();
                }).onPositive((dialog, which) -> {
            if (posListener != null)
                posListener.onClick(dialog, which);
            else
                dialog.dismiss();
        }).build().show();
    }
}
