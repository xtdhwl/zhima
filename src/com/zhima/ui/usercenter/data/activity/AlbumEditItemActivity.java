package com.zhima.ui.usercenter.data.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.utils.ImeHelper;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.usercenter.data.controller.AlbumTitleEditView;

/**
 * @ClassName: AlbumEditItemActivity
 * @Description: 我的相册编辑
 * @author luqilong
 * @date 2013-1-19 上午11:25:24
 */
public class AlbumEditItemActivity extends BaseActivity {

	public int mType;
	/** 编辑标题 */
	public static final int album_edit_title = 0;
	
	private ViewGroup mViewGroup;
	private AlbumTitleEditView mLatticeView;
	
	private String mContent;
	private String mTitleStr;

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImeHelper.showIME(mLatticeView.getEditText());
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_center_lattice_item_edit_activity);

		//需要type
		Intent intent = getIntent();
		mType = intent.getIntExtra("type", -1);
		mContent = intent.getStringExtra("content");
		mTitleStr = getTitleStr();
		initLatticeView();

		setTopbar();
		findView();
		
		mViewGroup.addView(mLatticeView.getPluginView());
		setView();
	}

	private void setView() {
		// TODO Auto-generated method stub
		mLatticeView.setContent(mContent);
	}

	private void findView() {
		// TODO Auto-generated method stub
		mViewGroup = (ViewGroup)findViewById(R.id.layout_edit);
		
	}

	private String getTitleStr() {
		// TODO Auto-generated method stub
		switch (mType) {
		case album_edit_title:
			return "编辑标题";
		default:
			throw new IllegalStateException("没有对应要编辑的类型");
		}
	}

	private void initLatticeView() {
		// TODO Auto-generated method stub
		switch (mType) {
		case album_edit_title:
			mLatticeView = new AlbumTitleEditView(this);
			break;
		default:
			throw new IllegalStateException("没有对应要编辑的类型");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 500);
	}

	@Override
	public void onDestroy() {
		ImeHelper.hideIME(mLatticeView.getEditText());
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		ImeHelper.hideIME(mLatticeView.getEditText());
		super.onBackPressed();
	};

	private OnClickListener saveTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EditText et = mLatticeView.getEditText();
			ImeHelper.hideIME(et);
			String content = et.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				HaloToast.show(getApplicationContext(), R.string.content_not_empty);
			} else {
				//返回activity
				Intent data = new Intent();
				data.putExtra("content", content);
				setResult(RESULT_OK, data);
				finish();
			}
		}
	};

	private void setTopbar() {
		// TODO Auto-generated method stub
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ImeHelper.hideIME(mLatticeView.getEditText());
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.send);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(mTitleStr);
	}
}
