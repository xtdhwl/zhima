package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.utils.ImeHelper;

/**
 * 搜索框
 * @ClassName: SearchBoxView
 * @Description: TODO
 * @author yusonglin
 * @date 2012-12-28 下午3:30:13
*/
public class SearchBoxView extends LinearLayout {

	private Context mContext;
	private String mSearchName;
	private View mParentView;
	
	private LinearLayout leftView;
	private LinearLayout mEditHintLayout;
	private EditText mRetrievalEdit;
	private ImageView mRemoveImage;
	protected String mRetrievalText;
	private PopupWindow mRetrievalPop;
	private LinearLayout mRetrievalKeyLayout;
	private TextView mRetrievalKeyText;
	private OnKeyClickListener mOnKeyClickListener;
	
	private SearchBoxView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param searchName
	 * @param parentView
	 */
	public SearchBoxView(Context context,String searchName,View parentView){
		super(context);
		this.mContext = context;
		this.mSearchName = searchName;
		this.mParentView = parentView;
		leftView = (LinearLayout) View.inflate(mContext, R.layout.retrieval_edittext_box, null);
		addView(leftView);
		initView();
		initPopView();
	}

	/**
	 * @Title: initPopView
	 * @Description: TODO
	 */
	private void initPopView() {
		View view = View.inflate(mContext, R.layout.retrieval_popupwindow_view, null);
		mRetrievalPop = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
		mRetrievalPop.setBackgroundDrawable(new ColorDrawable(0x00000000));
		mRetrievalPop.setAnimationStyle(R.style.customDialog_anim_style);
		mRetrievalPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		mRetrievalPop.setOutsideTouchable(false);
		mRetrievalPop.setFocusable(false);

		mRetrievalKeyLayout = (LinearLayout) view.findViewById(R.id.layout_retrieval_key);
		mRetrievalKeyText = (TextView) view.findViewById(R.id.txt_retrieval_key);
		
		mRetrievalKeyLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissPop();
				ImeHelper.hideIME(mRetrievalEdit);
				mRetrievalEdit.setText("");
				if (mOnKeyClickListener != null) {
					mOnKeyClickListener.onClick();
				}
			}
		});
		
		mRetrievalEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRetrievalEdit.requestFocus();
			}
		});
	}

	/**
	 * @Title: initView
	 * @Description: TODO
	 */
	private void initView() {

		mEditHintLayout = (LinearLayout) leftView.findViewById(R.id.layout_retrieval_edit_hint);
		TextView mHintText = (TextView) leftView.findViewById(R.id.txt_hint); 
		mHintText.setText("搜索"+mSearchName);
		mRetrievalEdit = (EditText) leftView.findViewById(R.id.edt_retrieval);
		mRemoveImage = (ImageView) leftView.findViewById(R.id.img_retrieval_remove);

		mRetrievalEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mRetrievalText = mRetrievalEdit.getText().toString();

				if (mRetrievalText == null || "".equals(mRetrievalText)) {
					mEditHintLayout.setVisibility(View.VISIBLE);
					mRemoveImage.setVisibility(View.GONE);
					dismissPop();
				} else {
					mEditHintLayout.setVisibility(View.GONE);
					mRemoveImage.setVisibility(View.VISIBLE);
					mRetrievalKeyText.setText("搜索   \"" + mRetrievalText + "\"");
					mRetrievalPop.setFocusable(false);
					showPopwindow();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		mRemoveImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRetrievalEdit.setText("");
			}
		});
	}
	
	
	/**
	 * 显示popwindow
	 */
	private void showPopwindow() {

		if (mRetrievalPop != null && !mRetrievalPop.isShowing()) {
			mRetrievalPop.showAsDropDown(this);
		}
	}

	/**
	 * 销毁popwindow
	 */
	private void dismissPop() {

		if (mRetrievalPop != null && mRetrievalPop.isShowing()) {
			mRetrievalPop.dismiss();
		}
	}
	
	/**
	 * @ClassName: OnKeyClickListener
	 * @Description: TODO
	 * @author yusonglin
	 * @date 2012-12-28 下午6:10:48
	*/
	public interface OnKeyClickListener{
		void onClick();
	}
	
	/**
	 * 设置关键字点击事件
	 * @Title: setOnKeyClickListener
	 * @Description: TODO
	 * @param onKeyClickListener
	 */
	public void setOnKeyClickListener(OnKeyClickListener onKeyClickListener){
		this.mOnKeyClickListener = onKeyClickListener;
	}
	
}
