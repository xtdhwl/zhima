package com.zhima.base.utils;

import android.content.Context;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.space.activity.PhotoActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 图片相关工具类
 * 
 * @ClassName: ImageHelper
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-31 下午7:08:35
 */
public class ImageHelper {

	/**
	 * 下载网络图片到本地
	 * @Title: downloadImage
	 * @Description: TODO
	 * @param context
	 * @param imageUrl 图片路径
	 * @return
	 */
	public static boolean downloadImage(Context context, String imageUrl) {

		boolean saveFalg = false;

		String localImagePath = HttpImageLoader.getInstance(context).getLocalImagePath(imageUrl, ImageScaleType.ORIGINAL);
		if (localImagePath != null) {
			String filePath = null;
			long dateTaken = System.currentTimeMillis();
			String filename = dateTaken + "."+ FileHelper.getFileExtension(imageUrl);
			filePath = FileHelper.getSysDcmiPath(filename);
			if (filePath != null) {
				saveFalg = FileHelper.copyFile(localImagePath, filePath);
				if (saveFalg) {
					/* Uri bitmapUri = */MediaStoreHelper.insertImage(
							context.getContentResolver(), filename, filename,
							dateTaken, FileHelper.getMIME(imageUrl), filePath);
				}
			} else {
				HaloToast.show(context, R.string.sd_error);
			}
		}

		return saveFalg;
	}

}
