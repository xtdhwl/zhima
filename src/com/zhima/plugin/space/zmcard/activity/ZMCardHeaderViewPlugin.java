package com.zhima.plugin.space.zmcard.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.space.common.viewplugin.SpaceHeaderViewPlugin;
import com.zhima.ui.common.view.SpacePagerAdapter;
import com.zhima.ui.common.view.SpaceViewPager;

public class ZMCardHeaderViewPlugin extends BaseViewPlugin {

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

	private TextView mSignatureView;

	private View mMultimediaView;

//	private TextView mLoveView;

	private SeekBar mSeekBar;

	public ZMCardHeaderViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_zmcard_header, null);
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

		mSignatureView = (TextView) mPluginView.findViewById(R.id.txt_zmcard_signature);
		mMultimediaView = mPluginView.findViewById(R.id.layout_zmcard_multimedia);
		mSeekBar = (SeekBar) mPluginView.findViewById(R.id.sbv_zmcard);
	}

	//-------------------------------------------
	//get set
	public void setAdapter(SpacePagerAdapter adapter) {
		viewPager.setAdapter(adapter);
	}

	public void setDefalutBackgroundResource(int resId) {
		viewPager.setBackgroundResource(resId);
	}

	public void setName(String name) {
		mNameView.setText(name);
	}

//	public void setLoveCount(String count) {
//		mLoveView.setText(count);
//	}

	public void setSignature(String str) {
		mSignatureView.setText(str);
	}

	public void setOwnerView(CharSequence str) {
		oWnerView.setText(str);
	}

	public void setZMID(String zmid) {
		mZMIDView.setText(zmid);
	}

	public ImageView getPhotoView() {
		return mPhotoView;
	}

	public SeekBar getSeekBar() {
		return mSeekBar;
	}

	public void setNameClickListener(OnClickListener listener) {
		oWnerView.setOnClickListener(listener);
	}

	public void setPhotoClicker(OnClickListener listener) {
		mPhotoView.setOnClickListener(listener);
	}

	public void setOnClickListener(OnClickListener listener) {
		mMultimediaView.setOnClickListener(listener);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}

}
