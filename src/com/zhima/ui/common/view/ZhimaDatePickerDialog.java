package com.zhima.ui.common.view;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;

import com.zhima.R;

/**
 * @ClassName:ZhimaDatePickerDialog
 * @Description 通用的日期选择框
 * @author jiangwei
 * @date 2012-8-13 9:02:47
 */
public class ZhimaDatePickerDialog extends CustomDialog {

	private Context mContext;
	private static int START_YEAR = 1900, END_YEAR = 2100;

	private WheelView wv_year, wv_month, wv_day;

	public ZhimaDatePickerDialog(Context context, View parentView) {
		super(context, parentView);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.setTitle(R.string.select_date);
		setMiddleLayout(getDateView());
	}

	public void setDate(Date date) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		setDate(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH) + 1, cal1.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * @Title: setDate
	 * @Description:TODO
	 * @param
	 * @return void
	 */
	public void setDate(int year, int month, int day) {

		if (wv_year != null) {
			wv_year.setCurrentItem(year - START_YEAR);
		}
		if (wv_month != null) {
			wv_month.setCurrentItem(month - 1);
		}
		if (wv_day != null) {
			wv_day.setCurrentItem(day - 1);
		}
	}

	private View getDateView() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 找到dialog的布局文件
		View view = View.inflate(mContext, R.layout.zhima_datepicker_layout, null);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		// wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		// wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		// wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;

		textSize = 30;

		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		return view;

	}

	/**
	 * 获取日期的
	 * 
	 * @return 返回“年月日”格式的字符串
	 */
	public String getDateStr() {
		String dateStr = getYear() + getMonth() + getDay();
		return dateStr;
	}

	/**
	 * 获取日期的
	 * 
	 * @return 返回“年-月-日”格式的字符串
	 */
	public String getDateSignStr() {
		String dateStr = getYear() + "-" + getMonth() + "-" + getDay();
		return dateStr;
	}

	/**
	 * 获取年份
	 * 
	 * @return 返回年份的字符串
	 */
	public String getYear() {
		String yearStr = String.valueOf(wv_year.getCurrentItem() + START_YEAR);
		return yearStr;
	}

	/**
	 * 获取月份
	 * 
	 * @return 返回月份的字符串
	 */
	public String getMonth() {
		// 如果是个数,则显示为"02"的样式
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String monthStr = decimal.format((wv_month.getCurrentItem() + 1));
		return monthStr;
	}

	/**
	 * 获取day
	 * 
	 * @return 返回day的字符串
	 */
	public String getDay() {
		// 如果是个数,则显示为"02"的样式
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String dayStr = decimal.format((wv_day.getCurrentItem() + 1));
		return dayStr;
	}
}
