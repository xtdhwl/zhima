package com.zhima.ui.usercenter.data.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.usercenter.adapter.MySpaceAdapter;

/**
 * @ClassName: MySpaceListActivity
 * @Description: 我的空间列表
 * @author luqilong
 * @date 2013-1-15 下午1:55:28
 */
public class MySpaceActivity extends BaseActivity {

	private ListView mSpaceView;
	private MySpaceAdapter mSpaceAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.myzhima_space_activity);
		findView();

		//获取数据 
		setView();
	}

	private void setView() {
		// TODO Auto-generated method stub
//		mSpaceAdapter = new MySpaceAdapter(this, 0, null);
//		mSpaceView.setAdapter(mSpaceAdapter);

	}

	private void findView() {
		// TODO Auto-generated method stub
		mSpaceView = (ListView) findViewById(R.id.lstv_space);
	}
}
