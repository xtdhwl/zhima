package com.zhima.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhima.R;

/**
 * @className FindPwdCodeActivity
 * @Description 忘记密码  输入发送到邮箱或者手机的验证码
 * @author yusonglin 
 * @date 2012-07-24
 */
public class FindPwdCodeActivity extends LoginBaseActivity implements OnClickListener {
	/**标题栏左边按钮*/
	private TextView mLeftButton;
	/**标题栏右边按钮*/
	private TextView mRightButton;
	/** activity标题*/
	private TextView mTitleText;
	private ImageView mTitleImg;
	
	private EditText mAuthcodeEdit;
	private Button mCheckingBt;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_findpwd_authcode_activity);
		
		findView();
		setUpView();
	}

	private void findView() {
//		mLeftButton = (TextView) this.findViewById(R.id.txt_diarybase_left);
//		mRightButton = (TextView) this.findViewById(R.id.txt_diarybase_right);
//		mTitleText = (TextView) this.findViewById(R.id.txt_diarybase_center);
//		mTitleImg = (ImageView) this.findViewById(R.id.img_diarybase_lock);
		
		mAuthcodeEdit = (EditText) this.findViewById(R.id.edt_findPwd_authcode);
		mCheckingBt = (Button) this.findViewById(R.id.btn_findPwd_checking);
	}

	private void setUpView() {
		mLeftButton.setVisibility(View.INVISIBLE);
		mRightButton.setVisibility(View.INVISIBLE);
		mTitleImg.setVisibility(View.INVISIBLE);
		mTitleText.setText(R.string.findPwd);
		
		mCheckingBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_findPwd_checking:
			String authcode = mAuthcodeEdit.getText().toString().trim();
			if(TextUtils.isEmpty(authcode)){
				Toast.makeText(this,R.string.authCode_isWrong, 0).show();
				return;
			}
			
			Intent intent = new Intent(this,SetNewPwdActivity.class);
			startActivity(intent);
			break;

		}
	}
	
}
