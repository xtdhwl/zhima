package com.zhima.ui.space.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 无网络下扫码结果
 * @ClassName: NoNetWorkActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-10-6 下午4:02:46
 */
public class NoNetWorkActivity extends BaseActivity {
	protected static final String TAG = "UnknownActivity";

	/** 扫描码 */
	private TextView mZMCodeText;

	private TextView mScanTimeText;

	/** 显示没有网络 */
	private TextView mNoNetText;

	// 短信分享内容
//	private String sms_message = "我正在用芝麻客扫描二维码,推荐给你！下载地址:http://www.zhima.net 快下载试试看吧！！";

	private String zmCode;

	private View mChildrenView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setSidebar();
		findView();
		setTopbar();
		Intent intent = getIntent();
		zmCode = intent.getStringExtra(ACTIVITY_EXTRA);
		setView();
	}

	/**
	 * 设置侧边栏
	 */
	public void setSidebar() {
		mChildrenView = View.inflate(this, R.layout.space_nonetwork_activity, null);

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "网络异常", View.GONE, null);

//		mSideBarView = new SideBarView(this,this, "网络异常");
//		mSideBarView.setChildView(mChildrenView);
//		setContentView(mSideBarView.getContentView());
//		
//		
//		final View tranView = (View) mSideBarView.getContentView().findViewById(
//				R.id.view_transparent);
//		tranView.setVisibility(View.GONE);
//		tranView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mSideBarView.scrollView();
//			}
//		});
//		tranView.setClickable(false);
//		mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {
//
//			@Override
//			public void onStateChange(boolean isMenuOut) {
//				if (isMenuOut) {
//					tranView.setVisibility(View.VISIBLE);
//					tranView.setClickable(true);
//				} else {
//					tranView.setVisibility(View.GONE);
//					tranView.setClickable(false);
//				}
//			}
//		});
	}

	private void setView() {
		mZMCodeText.setText(zmCode);
//		mScanTimeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

//	/** 分享 */
//	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + ""));
//			smsIntent.putExtra("sms_body", sms_message);
//			NoNetWorkActivity.this.startActivity(smsIntent);
//		}
//	};

	/** 复制剪贴板 */
	private View.OnClickListener overFlowTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (zmCode != null) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(zmCode);
				HaloToast.show(NoNetWorkActivity.this, getText(R.string.clipboard_msg).toString());
			}
		}
	};

	private void findView() {
		mZMCodeText = (TextView) mChildrenView.findViewById(R.id.txt_zmcode);
		mScanTimeText = (TextView) mChildrenView.findViewById(R.id.txt_unknownCode_scanTime);
		mNoNetText = (TextView) mChildrenView.findViewById(R.id.txt_no_net);
	}

	private void setTopbar() {
		ZhimaTopbar topBar = getTopbar();
		topBar.setRightLayoutVisible(true);

		View rightView = View.inflate(this, R.layout.topbar_rightview, null);

		topBar.addRightLayoutView(rightView);

		RelativeLayout rightButtonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		ImageView rightButton1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		rightButtonLayout1.setVisibility(View.VISIBLE);
		rightButton1.setImageResource(R.drawable.copy);
		rightButtonLayout1.setOnClickListener(overFlowTopbarClick);
	}
}
