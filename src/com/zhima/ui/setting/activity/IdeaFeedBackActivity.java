package com.zhima.ui.setting.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.service.AppLaunchService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;

public class IdeaFeedBackActivity extends BaseActivity {

	private EditText mIdeaEdit;
	
	private TextView mTextCount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		setContentView(R.layout.settting_idea_feedback_activity);
		
		setTopBar();
		findView();
		setUpView();
		
	}

	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View leftview = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) leftview.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) leftview.findViewById(R.id.txt_topbar_title);
		titleText.setText("意见反馈");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImeHelper.hideIME(mIdeaEdit);
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(leftview);
		
		View rightView = View.inflate(this, R.layout.topbar_rightview, null);
		RelativeLayout sendBtLayout = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		ImageView sendImage = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		sendImage.setImageResource(R.drawable.send);
		sendBtLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String content = mIdeaEdit.getText().toString().trim();
				if(TextUtils.isEmpty(content)){
					HaloToast.show(getApplicationContext(), R.string.content_not_empty);
					return ;
				}
				AppLaunchService.getInstance(IdeaFeedBackActivity.this).addFeedback(content, IdeaFeedBackActivity.this);
				startWaitingDialog("", "正在发送...");
			}
		});
		sendBtLayout.setVisibility(View.VISIBLE);
		topbar.setRightLayoutVisible(true);
		topbar.addRightLayoutView(rightView);
	}

	private void findView() {
		mIdeaEdit = (EditText) this.findViewById(R.id.edt_setting_ideaFeedback);
		mTextCount = (TextView)this.findViewById(R.id.txt_setting_idea_textCount);
	}

	private void setUpView() {
		setKeybordPopup();
		mIdeaEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mTextCount.setText("（"+mIdeaEdit.getText().toString().length()+"/140字）");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private void setKeybordPopup(){
		Timer timer = new Timer();
        timer.schedule(new TimerTask() {
           @Override
                public void run() {
                        ImeHelper.showIME(mIdeaEdit);
                     }
 
              }, 500); 
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
		ImeHelper.hideIME(mIdeaEdit);
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		super.onHttpResult(protocol);
		if(protocol.isHttpSuccess()){
			if(protocol.isHandleSuccess()){
				HaloToast.show(getApplicationContext(), "反馈成功,感谢您的宝贵意见！");
				mIdeaEdit.setText("");
				finish();
			}else{
				HaloToast.show(getApplicationContext(), "反馈失败,请重试!");
			}
		}else{
			HaloToast.show(getApplicationContext(),R.string.network_request_failed);
		}
		
		dismissWaitingDialog();
	}
}
