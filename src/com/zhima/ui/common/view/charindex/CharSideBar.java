package com.zhima.ui.common.view.charindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhima.R;

/**
 * 字母索引自定义view
 * @ClassName: SideBar
 * @Description: TODO
 * @author yusonglin
 * @date 2012-12-29 下午4:52:32
*/
public class CharSideBar extends View {
	private Context mContext;
	private char[] mChars;
	private SectionIndexer mSectionIndexter = null;
	private ListView mList;
	private TextView mDialogText;
//	private final int m_nItemHeight = 15;

	public CharSideBar(Context context) {
		super(context);
		init(context);
	}

	public CharSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		mChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
				'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				'X', 'Y', 'Z' };
		setTextView();
	}

	public CharSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setListView(ListView _list) {
		mList = _list;
		mSectionIndexter = (SectionIndexer) _list.getAdapter();
	}

	public void setTextView() {
		mDialogText = (TextView) LayoutInflater.from(mContext).inflate(
				R.layout.char_index_current_charname, null);
		mDialogText.setVisibility(View.INVISIBLE);

		WindowManager mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		// this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		// int idx = i / m_nItemHeight;
		int viewHeight = getHeight();
		int idx = i / (viewHeight / mChars.length);
		if (idx >= mChars.length) {
			idx = mChars.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText("" + mChars[idx]);
			if (mSectionIndexter == null) {
				mSectionIndexter = (SectionIndexer) mList.getAdapter();
			}
			int position = mSectionIndexter.getPositionForSection(mChars[idx]);
			if (position == -1) {
				return true;
			}
			mList.setSelection(position);
		} else {
			mDialogText.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
		paint.setColor(0xff595c61);
		paint.setTextSize(18);
		paint.setColor(getResources().getColor(R.color.blue_deep));
		paint.setTextAlign(Paint.Align.CENTER);
		Typeface face = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		paint.setTypeface(face);
		float widthCenter = getMeasuredWidth() / 2;
		int length = getHeight() / mChars.length;
		for (int i = 0; i < mChars.length; i++) {
			canvas.drawText(String.valueOf(mChars[i]), widthCenter,
					length * (i + 1)+5/* m_nItemHeight + (i * m_nItemHeight) */,
					paint);
		}
		super.onDraw(canvas);
	}
}
