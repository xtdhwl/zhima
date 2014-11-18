package com.zhima.ui.common.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;

import com.zhima.R;

public class CommonViewUtils {	

	public static final CustomLoadDialog getWaitDlg(Context context,
			String title, String content) {
		return getWaitDlg(context, title, content, true);
	}

	public static final CustomLoadDialog getWaitDlg(Context context, int title,
			int content) {
		String t = title > 0 ? context.getString(title) : null;
		String c = content > 0 ? context.getString(content) : null;
		return getWaitDlg(context, t, c);
	}

	public static final CustomLoadDialog getWaitDlg(Context context,
			String title, String content, boolean cancelable) {
		
//		ProgressDialog dialog = new ProgressDialog(context);
//		if (!TextUtils.isEmpty(title)) {
//			dialog.setTitle(title);
//		}
//		dialog.setMessage(content);
//		dialog.setIndeterminate(true);
//		dialog.setCancelable(cancelable);
		
		CustomLoadDialog dialog = new CustomLoadDialog(context);
		dialog.setTitle(title);
		dialog.setContent(content);
		
		return dialog;
	}

	public final static void getEditDialogText(Context c, int positiveButton,
			int title, EditCallback callback, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setPositiveButton(
				positiveButton).show(callback);
	}

	public final static View getEditDialogText(Context c, int title,
			EditCallback callback, int maxLength) {
		return new EditDialogBuilder(c, title, maxLength).show(callback);
	}

	public final static void getEditDialogText(Context c, String title,
			EditCallback callback, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).show(callback);
	}

	public final static void getEditDialogText(Context c, int title,
			String oldName, EditCallback callback, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setContent(oldName).show(
				callback);
	}

	public final static void getEditDialogTextEnableNoInput(Context c,
			int title, String oldName, EditCallback callback, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setContent(oldName)
				.setMinLength(0).show(callback);
	}

	public final static void getEditDialogText(Context c, String title,
			String oldName, EditCallback callback, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setContent(oldName).show(
				callback);
	}

	public final static void getEditDialogText(Context c, int title,
			String oldName, EditCallback callback, int maxLength, int minLength) {
		new EditDialogBuilder(c, title, maxLength).setContent(oldName)
				.setMinLength(minLength).show(callback);
	}

	public final static void getEditDialogText(Context c, int title,
			EditCallback callback, int inputType, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setInputType(inputType)
				.show(callback);
	}

	public final static void getEditDialogText(Context c, String title,
			EditCallback callback, int inputType, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setInputType(inputType)
				.show(callback);
	}

	public final static void getEditDialogText(Context c, int title,
			EditCallback callback, int inputType, String oldName, int maxLength) {
		new EditDialogBuilder(c, title, maxLength).setContent(oldName)
				.setInputType(inputType).show(callback);
	}

	public final static void showConfirmDialog(Context context, int message,
			DialogCallback callback) {
		showConfirmDialog(context, message, R.string.dialog_title, callback);
	}

	public final static void showConfirmDialog(Context context, String message,
			DialogCallback callback) {
		showConfirmDialog(context, message,
				context.getString(R.string.dialog_title), callback);
	}

	public final static void showConfirmDialog(Context context, int message,
			int title, final DialogCallback callback) {
		new AlertDialog.Builder(context).setMessage(message).setTitle(title)
				.setPositiveButton(R.string.ok, callback)
				.setNegativeButton(R.string.cancel, callback)
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						if (callback != null) {
							callback.onCancel(dialog);
						}
					}
				}).show();
	}

	public final static void showConfirmDialog(Context context, String message,
			String title, DialogCallback callback) {
		new AlertDialog.Builder(context).setMessage(message).setTitle(title)
				.setPositiveButton(R.string.ok, callback)
				.setNegativeButton(R.string.cancel, callback).show();
	}

	public final static void showConfirmEditDialog(Context context, int res,
			final DialogCallback callback) {
		showConfirmDialog(context, res, R.string.dialog_title, callback);
	}

	public final static void showInfoDialog(Context context, int title,
			int message) {
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage(message);
		if (title > 0) {
			dlg.setTitle(title);
		}
		dlg.setPositiveButton(R.string.ok, null);
		dlg.show();
	}

	public final static void showInfoDialog(Context context, String title,
			String message) {
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setMessage(message);
		if (title.length() > 0) {
			dlg.setTitle(title);
		}
		dlg.setPositiveButton(R.string.ok, null);
		dlg.show();
	}

	public final static void showInfoDialog(Context c, int title, int message,
			DialogInterface.OnClickListener l) {
		Builder dlg = new AlertDialog.Builder(c);
		dlg.setMessage(message);
		if (title > 0) {
			dlg.setTitle(title);
		}
		dlg.setPositiveButton(R.string.ok, l);
		dlg.show();
	}

	public final static void showInfoDialog(Context c, int title,
			String message, DialogInterface.OnClickListener l) {
		Builder dlg = new AlertDialog.Builder(c);
		dlg.setMessage(message);
		if (title > 0) {
			dlg.setTitle(title);
		}
		dlg.setPositiveButton(R.string.ok, l);
		dlg.show();
	}

	public final static AlertDialog showSingleSelectDialog(Context context, int title,
			int array, DialogInterface.OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setItems(array, listener).create();
		dialog.show();
		return dialog;
	}

	public final static AlertDialog showSingleSelectDialog(Context context, int title,
			String[] item, DialogInterface.OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setItems(item, listener).create();
		dialog.show();
		return dialog;
	}

	public final static AlertDialog showSingleSelectDialog(Context context,
			String[] item, DialogInterface.OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(R.string.please_select)
				.setItems(item, listener).create();
		dialog.show();
		return dialog;
	}

	public final static AlertDialog showSingleSelectDialog(Context context,
			String title, String[] item,
			DialogInterface.OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setItems(item, listener).create();
		dialog.show();
		return dialog;
	}

	public static Dialog getImageDlg(Context c, Bitmap bitmap) {
		Dialog dlg = new Dialog(c);
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ImageView image = new ImageView(c);
		image.setImageBitmap(bitmap);
		LayoutParams l = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		dlg.setContentView(image, l);
		dlg.show();
		return dlg;
	}
}
