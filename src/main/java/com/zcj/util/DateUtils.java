package com.zcj.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DateUtils {
	public final static String formatYYMMDD = "yyyy-MM-dd";

	public final static String formatHHmm = "HH:mm";
	
	public final static String formatHHmmss = "HH:mm:ss";
	
	public final static String formatYYMMDDHHmmss = "yyyy-MM-dd HH:mm:ss";

	public static void main(String[] args) throws Exception {
//		Calendar cal = Calendar.getInstance();
		String start = "2016-01-01";
		String end = "2016-12-30";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dBegin = sdf.parse(start);
		Date dEnd = sdf.parse(end);
		List<Date> lDate = findDates(dBegin, dEnd);
		List<Date> holidays=new ArrayList<Date>();
		holidays.add(sdf.parse("2016-10-10"));
		holidays.add(sdf.parse("2016-10-11"));
		holidays.add(sdf.parse("2016-10-12"));
		holidays.add(sdf.parse("2016-10-13"));
		holidays.add(sdf.parse("2016-02-15"));
		for (Date date : lDate) {
			System.out.println(sdf.format(date)+"是节假日或休息日"+checkHoliday(date, holidays,true,true));
		}
	}

	// 获取某段时间内的所有日期 以天为单位
	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List lDate = new ArrayList();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}
	
	/**
	 * 
	 * 验证日期是否是节假日
	 * 
	 * @param calendar
	 *            传入需要验证的日期
	 * @return return boolean 返回类型 返回true是节假日，返回false不是节假日 throws
	 */
	public static boolean checkHoliday(Date date, List<Date> holidays,boolean isSkipHolidays,boolean isSkipRestDay){

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);// 设置当前时间

		List<Calendar> holidayList = new ArrayList<Calendar>();
		if(holidays!=null&&holidays.size()>0){
			for (Date d : holidays) {
				Calendar ca = Calendar.getInstance();
				ca.setTime(d);
				holidayList.add(ca);
			}
		}

		// 判断日期是否是周六周日
		if(isSkipRestDay){
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				return true;
			}
		}
		// 判断日期是否是节假日
		if(isSkipHolidays){
			if(holidayList.size()>0){
				for (Calendar ca : holidayList) {
					if (ca.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
							&& ca.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
							&& ca.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	// 获取某一天的零点
	public static Date getDateZero(Date date) {
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		// 将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		// 将秒至0
		calendar.set(Calendar.SECOND, 0);
		// 将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// 获取某一天的二十三点五十九分五十九秒
	public static Date getDateTwentyFour(Date date) {
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		// 将小时至23
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		// 将分钟至59
		calendar.set(Calendar.MINUTE, 59);
		// 将秒至59
		calendar.set(Calendar.SECOND, 59);
		// 将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
