package com.store.architecture.utils;

import java.util.Calendar;
import java.util.Date;

import com.google.cloud.Timestamp;

public class DateUtils {
	public static Timestamp timestampFrom(Date date) {
		return Timestamp.of(date);
	}

	public static Date dateFrom(Timestamp timestamp) {
		return new Date(timestamp.getSeconds() * 1000);
	}

	public static Date addDays(Date aDate, int days) {
		return org.apache.commons.lang.time.DateUtils.addDays(aDate, days);
	}

	public static Date addHours(Date aDate, int hours) {
		return org.apache.commons.lang.time.DateUtils.addHours(aDate, hours);
	}

	public static boolean isSameDate(Date aDate, Date anotherDate) {
		try {
			return org.apache.commons.lang.time.DateUtils.isSameInstant(aDate, anotherDate);
		} catch (IllegalArgumentException e) {
			return aDate == anotherDate;
		}
	}

	public static Date dateWithoutTime(Date date) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.DATE);
	}
}
