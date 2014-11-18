/* 
 * @Title: DateUtils.java
 * Created by liubingsr on 2012-8-11 下午3:49:16 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.utils;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.zhima.R;

/**
 * @ClassName: DateUtils
 * @Description: 时间相关
 * @author liubingsr
 * @date 2012-8-11 下午3:49:16
 * 
 */
public final class DateUtils {
	/**
	 * @Title: getIntervalYear
	 * @Description: 计算两个时间相差的年数
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return 相差的年数(endTime - beginTime)。如果beginTime > endTime，则返回相差年数的负值
	 */
	public static int getIntervalYear(long beginTime, long endTime) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(beginTime);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(endTime);
		return cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
	}
	
	public static int getIntervalDay(long beginTime, long endTime) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(beginTime);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(endTime);
		return cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * @Title: isSampleDay
	 * @Description: 是否是同一天
	 * @param time1 the first date(UTC milliseconds)
	 * @param time2 the second date(UTC milliseconds)
	 * @return true
	 */
	public static boolean isSameDay(long time1, long time2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(time2);
		return isSameDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if two dates are on the same day ignoring time.
	 * </p>
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException if either date is <code>null</code>
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if two calendars represent the same day ignoring time.
	 * </p>
	 * @param cal1 the first calendar, not altered, not null
	 * @param cal2 the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException if either calendar is <code>null</code>
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
				.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * <p>
	 * Checks if a date is today.
	 * </p>
	 * @param date the date, not altered, not null.
	 * @return true if the date is today.
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isToday(Date date) {
		return isSameDay(date, Calendar.getInstance().getTime());
	}

	/**
	 * <p>
	 * Checks if a calendar date is today.
	 * </p>
	 * @param cal the calendar, not altered, not null
	 * @return true if cal date is today
	 * @throws IllegalArgumentException if the calendar is <code>null</code>
	 */
	public static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	/**
	 * <p>
	 * Checks if the first date is before the second date ignoring time.
	 * </p>
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if the first date day is before the second date day.
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isBeforeDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isBeforeDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if the first calendar date is before the second calendar date
	 * ignoring time.
	 * </p>
	 * @param cal1 the first calendar, not altered, not null.
	 * @param cal2 the second calendar, not altered, not null.
	 * @return true if cal1 date is before cal2 date ignoring time.
	 * @throws IllegalArgumentException if either of the calendars are
	 *             <code>null</code>
	 */
	public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
			return true;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
			return false;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
			return true;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
			return false;
		return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * <p>
	 * Checks if the first date is after the second date ignoring time.
	 * </p>
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if the first date day is after the second date day.
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isAfterDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isAfterDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if the first calendar date is after the second calendar date
	 * ignoring time.
	 * </p>
	 * @param cal1 the first calendar, not altered, not null.
	 * @param cal2 the second calendar, not altered, not null.
	 * @return true if cal1 date is after cal2 date ignoring time.
	 * @throws IllegalArgumentException if either of the calendars are
	 *             <code>null</code>
	 */
	public static boolean isAfterDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
			return false;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
			return true;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
			return false;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
			return true;
		return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * <p>
	 * Checks if a date is after today and within a number of days in the
	 * future.
	 * </p>
	 * @param date the date to check, not altered, not null.
	 * @param days the number of days.
	 * @return true if the date day is after today and within days in the future
	 *         .
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isWithinDaysFuture(Date date, int days) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return isWithinDaysFuture(cal, days);
	}

	/**
	 * <p>
	 * Checks if a calendar date is after today and within a number of days in
	 * the future.
	 * </p>
	 * @param cal the calendar, not altered, not null
	 * @param days the number of days.
	 * @return true if the calendar date day is after today and within days in
	 *         the future .
	 * @throws IllegalArgumentException if the calendar is <code>null</code>
	 */
	public static boolean isWithinDaysFuture(Calendar cal, int days) {
		if (cal == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar today = Calendar.getInstance();
		Calendar future = Calendar.getInstance();
		future.add(Calendar.DAY_OF_YEAR, days);
		return (isAfterDay(cal, today) && !isAfterDay(cal, future));
	}

	/** Returns the given date with the time set to the start of the day. */
	public static Date getStart(Date date) {
		return clearTime(date);
	}

	/** Returns the given date with the time values cleared. */
	public static Date clearTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * Determines whether or not a date has any time values (hour, minute,
	 * seconds or millisecondsReturns the given date with the time values
	 * cleared.
	 */

	/**
	 * Determines whether or not a date has any time values.
	 * @param date The date.
	 * @return true iff the date is not null and any of the date's hour, minute,
	 *         seconds or millisecond values are greater than zero.
	 */
	public static boolean hasTime(Date date) {
		if (date == null) {
			return false;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (c.get(Calendar.HOUR_OF_DAY) > 0) {
			return true;
		}
		if (c.get(Calendar.MINUTE) > 0) {
			return true;
		}
		if (c.get(Calendar.SECOND) > 0) {
			return true;
		}
		if (c.get(Calendar.MILLISECOND) > 0) {
			return true;
		}
		return false;
	}

	/** Returns the given date with time set to the end of the day */
	public static Date getEnd(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	/**
	 * Returns the maximum of two dates. A null date is treated as being less
	 * than any non-null date.
	 */
	public static Date max(Date d1, Date d2) {
		if (d1 == null && d2 == null)
			return null;
		if (d1 == null)
			return d2;
		if (d2 == null)
			return d1;
		return (d1.after(d2)) ? d1 : d2;
	}

	/**
	 * Returns the minimum of two dates. A null date is treated as being greater
	 * than any non-null date.
	 */
	public static Date min(Date d1, Date d2) {
		if (d1 == null && d2 == null)
			return null;
		if (d1 == null)
			return d2;
		if (d2 == null)
			return d1;
		return (d1.before(d2)) ? d1 : d2;
	}

	/** The maximum date possible. */
	public static Date MAX_DATE = new Date(Long.MAX_VALUE);

	/**
	 * @Title: getConstellation
	 * @Description: 根据月与日得到星座
	 * @param m 月份
	 * @param d 日份
	 * @return 星座
	 */
	public static String getAstro(Context context, int month, int day) {
//		星座     日期(公历)    英文名 
//		魔羯座 (12/22 - 1/19) Capricorn 
//		水瓶座 (1/20 - 2/18) Aquarius 
//		双鱼座 (2/19 - 3/20) Pisces 
//		牡羊座 (3/21 - 4/20) Aries 
//		金牛座 (4/21 - 5/20) Taurus 
//		双子座 (5/21 - 6/21) Gemini 
//		巨蟹座 (6/22 - 7/22) Cancer 
//		狮子座 (7/23 - 8/22) Leo 
//		处女座 (8/23 - 9/22) Virgo 
//		天秤座 (9/23 - 10/22) Libra 
//		天蝎座 (10/23 - 11/21) Scorpio 
//		射手座 (11/22 - 12/21) Sagittarius

		final String[] astroArr = context.getResources().getStringArray(R.array.astros);
		//月份                                1   2   3   4   5   6   7   8   9   10  11  12                          
		final int[] constellationEdgeDay = { 19, 18, 20, 19, 20, 21, 22, 22, 22, 23, 22, 21 };
		if (day > 31 || day < 1)
			return "";
		else {
			switch (month) {
			case 1:
				if (day <= constellationEdgeDay[0])
					return astroArr[0];
				else
					return astroArr[1];
			case 2:
				if (day <= constellationEdgeDay[1])
					return astroArr[1];
				else
					return astroArr[2];
			case 3:
				if (day <= constellationEdgeDay[2])
					return astroArr[2];
				else
					return astroArr[3];
			case 4:
				if (day <= constellationEdgeDay[3])
					return astroArr[3];
				else
					return astroArr[4];
			case 5:
				if (day <= constellationEdgeDay[4])
					return astroArr[4];
				else
					return astroArr[5];
			case 6:
				if (day <= constellationEdgeDay[5])
					return astroArr[5];
				else
					return astroArr[6];
			case 7:
				if (day <= constellationEdgeDay[6])
					return astroArr[6];
				else
					return astroArr[7];
			case 8:
				if (day <= constellationEdgeDay[7])
					return astroArr[7];
				else
					return astroArr[8];
			case 9:
				if (day <= constellationEdgeDay[8])
					return astroArr[8];
				else
					return astroArr[9];
			case 10:
				if (day <= constellationEdgeDay[9])
					return astroArr[9];
				else
					return astroArr[10];
			case 11:
				if (day <= constellationEdgeDay[10])
					return astroArr[10];
				else
					return astroArr[11];
			case 12:
				if (day <= constellationEdgeDay[11])
					return astroArr[11];
				else
					return astroArr[0];
			default:
				return "";
			}
		}
	}
}
