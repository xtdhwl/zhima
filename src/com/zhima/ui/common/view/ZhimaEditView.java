package com.zhima.ui.common.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zhima.R;
import com.zhima.base.utils.ImeHelper;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.FaceAdapter;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.FaceHelper.FaceHolder;

/**
 * @ClassName ZhimaEditView
 * @Description 自定义表情文字输入view
 * @author jiang
 * @date 2012-12-28 下午03:07:26
 */
public class ZhimaEditView extends LinearLayout {

	/** 最大输入文字个数 */
	protected static final int MAX_TEXT_NUM = 140;

	/** 输入内容 */
	private EditText mContentText;
	/** 表情栏 */
	private ViewFlow mViewFlow;
	/** 表情布局 */
	private ViewGroup mFaceGroup;
	/** 发送按钮 */
	private Button sendBtn;
	private Context mContext;

	public ZhimaEditView(Context context) {
		this(context, null);
	}

	public ZhimaEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		((BaseActivity) mContext).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.zhima_edit_view, this);

		mContentText = (EditText) findViewById(R.id.edt_content);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mFaceGroup = (ViewGroup) findViewById(R.id.layout_face);
		sendBtn = (Button) findViewById(R.id.btn_send);

		setView();
		setListener();
	}

	/**
	 * @Title: getEditText
	 * @Description:获取输入框
	 * @param
	 * @return EditText
	 */
	public EditText getEditText() {
		if (mContentText != null) {
			return mContentText;
		}
		return new EditText(mContext);
	}

	/**
	 * @Title: getFaceGroup
	 * @Description:获取表情区域的view
	 * @param
	 * @return ViewGroup
	 */
	public ViewGroup getFaceGroup() {
		if (mFaceGroup != null) {
			return mFaceGroup;
		}
		return null;
	}

	/**
	 * @Title: getSendBtn
	 * @Description:获取发送的按钮组件
	 * @param
	 * @return Button
	 */
	public Button getSendBtn() {
		if (sendBtn != null) {
			return sendBtn;
		}
		return new Button(mContext);
	}

	/**
	 * @Title: setTextMaxCount
	 * @Description:TODO
	 * @param 设置edittext最大输入字数
	 * @return void
	 */
	public void setTextMaxCount(int maxCount) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(maxCount);
		mContentText.setFilters(filters);
	}

	/**
	 * @Title: setListener
	 * @Description:设置监听器
	 * @param
	 * @return void
	 */
	private void setListener() {
		mContentText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mFaceGroup.setVisibility(View.GONE);
			}
		});
		findViewById(R.id.img_face).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				faceClick(v);
			}
		});
	}

	/**
	 * @Title: setView
	 * @Description:初始化view
	 * @param
	 * @return void
	 */
	private void setView() {

		mContentText.requestFocus();
		// 设置最大输入字数
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(MAX_TEXT_NUM);
		mContentText.setFilters(filters);

		// 添加表情
		FaceAdapter faceAdapter = new FaceAdapter(mContext, FaceHelper.getInstance(mContext).getFaceHolderList());
		faceAdapter.setOnClickListener(faceOnClickListener);
		mViewFlow.setAdapter(faceAdapter);
		CircleFlowIndicator indicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(indicator);
	}

	/** 表情点击事件 */
	private View.OnClickListener faceOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			FaceHolder face = (FaceHolder) v.getTag();
			String symbol = face.getSymbol();
			SpannableString span = new SpannableString(symbol);
			span.setSpan(new ImageSpan(face.getBitmap()), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			mContentText.getText().insert(mContentText.getSelectionStart(), span);
		}
	};

	/** 底部笑脸按钮事件 */
	private void faceClick(View v) {
		int visib = mFaceGroup.getVisibility();
		if (visib == View.GONE) {
			ImeHelper.hideIME(mContentText);
			mFaceGroup.setVisibility(View.VISIBLE);
		} else {
			ImeHelper.showIME(mContentText);
			mFaceGroup.setVisibility(View.GONE);
		}

	}

}
