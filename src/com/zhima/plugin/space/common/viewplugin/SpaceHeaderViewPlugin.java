package com.zhima.plugin.space.common.viewplugin;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.ui.common.view.SpacePagerAdapter;
import com.zhima.ui.common.view.SpaceViewPager;

/**
 * @ClassName: SpaceHeaderViewPlugin
 * @Description: 空间头部预览
 * @author luqilong
 * @date 2013-1-6 下午1:20:21
 */
public class SpaceHeaderViewPlugin extends BaseViewPlugin {

	private static final String TAG = SpaceHeaderViewPlugin.class.getSimpleName();

	private SpaceViewPager viewPager;
	/** 头像 */
	private ImageView mPhotoView;
	/** 姓名 */
	private TextView mNameView;
	/** zmid */
	private TextView mZMIDView;
	/** owner */
	private TextView oWnerView;

//	private TextView mLoveView;

	public SpaceHeaderViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_space_header, null);
		viewPager = (SpaceViewPager) mPluginView.findViewById(R.id.vpag_space_header_album);
		//适配
		int widht = GraphicUtils.mScreenWidth;
		int height = GraphicUtils.mScreenHeight;

		LayoutParams layoutParams = viewPager.getLayoutParams();
		layoutParams.width = widht;
		layoutParams.height = widht / 4 * 3;
		viewPager.setLayoutParams(layoutParams);

		mPhotoView = (ImageView) mPluginView.findViewById(R.id.img_space_header_photo);
		mNameView = (TextView) mPluginView.findViewById(R.id.txt_space_header_name);
		mZMIDView = (TextView) mPluginView.findViewById(R.id.txt_space_header_zmid);
		oWnerView = (TextView) mPluginView.findViewById(R.id.txt_space_header_owner);
	}

	//-------------------------------------------
	//get set
	public void setAdapter(SpacePagerAdapter adapter) {
		viewPager.setAdapter(adapter);
	}

	public void setName(String name) {
		mNameView.setText(name);
	}

//	public void setLoveCount(String count) {
//		mLoveView.setText(count);
//	}

	public void setOwnerView(String str) {
		oWnerView.setText(str);
	}

	public void setZMID(String zmid) {
		mZMIDView.setText(zmid);
	}

	public ImageView getPhotoView() {
		return mPhotoView;
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}

}
