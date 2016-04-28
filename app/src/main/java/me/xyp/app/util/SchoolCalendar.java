package me.xyp.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by cc on 16/4/28.
 */
public class SchoolCalendar {
    Calendar firstDay = new GregorianCalendar(2016, Calendar.FEBRUARY, 29);
    Calendar currentTime;

    public SchoolCalendar() {
        currentTime = new GregorianCalendar();
    }

    public SchoolCalendar(Date date) {
        this();
        currentTime.setTime(date);
    }

    public SchoolCalendar(long timestamp) {
        this(new Date(timestamp * 1000));
    }

    public SchoolCalendar(int year, int month, int day) {
        currentTime = new GregorianCalendar(year, month, day);
    }

    public SchoolCalendar(int week, int weekDay) {
        currentTime = (Calendar) firstDay.clone();
        currentTime.add(Calendar.DATE, (week - 1) * 7);
        weekDay = weekDay == 0 ? 7 : weekDay;
        currentTime.add(Calendar.DATE, weekDay - getDayOfWeek());
    }

    /**
     * 取这学期过去了多少天
     *
     * @return
     */
    public int getDayOfTerm() {
        int days = getDeltaT(currentTime, firstDay);
        if (days >= 0) {
            days++;
        }
        return days;
    }

    /**
     * 取当前第几周
     *
     * @return
     */
    public int getWeekOfTerm() {
        Calendar currentTime2 = (Calendar) currentTime.clone();
        int beWeekDay = firstDay.get(Calendar.DAY_OF_WEEK);
        beWeekDay = beWeekDay == 1 ? 8 : beWeekDay;
        int enWeekDay = currentTime2.get(Calendar.DAY_OF_WEEK);
        enWeekDay = enWeekDay == 1 ? 8 : enWeekDay;
        currentTime2.add(Calendar.DATE, beWeekDay - enWeekDay);
        int weeks = getDeltaT(currentTime2, firstDay) / 7;
        if (weeks >= 0) {
            weeks++;
        }
        return weeks;
    }

    private int getDeltaT(Calendar end, Calendar begin) {
        Calendar mbegin = (Calendar) begin.clone();
        mbegin.set(Calendar.HOUR_OF_DAY, 0);
        mbegin.set(Calendar.MINUTE, 0);
        mbegin.set(Calendar.SECOND, 0);
        mbegin.set(Calendar.MILLISECOND, 0);
        Calendar mend = (Calendar) end.clone();
        mend.set(Calendar.HOUR_OF_DAY, 0);
        mend.set(Calendar.MINUTE, 0);
        mend.set(Calendar.SECOND, 0);
        mend.set(Calendar.MILLISECOND, 0);
        return (int) ((mend.getTimeInMillis() - mbegin.getTimeInMillis()) / (1000 * 86400));
    }


    /**
     * 日期加减
     *
     * @param day
     */
    public SchoolCalendar addDay(int day) {
        currentTime.add(Calendar.DATE, day);
        return this;
    }


    /**
     * 格式化输出日期
     * 年:y		月:M		日:d		时:h(12制)/H(24值)	分:m		秒:s		毫秒:S
     *
     * @param formatString
     */
    public String getString(String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        String date = format.format(currentTime.getTime());
        return date;
    }


    /**
     * 格式化解析日期文本
     * 年:y		月:M		日:d		时:h(12制)/H(24值)	分:m		秒:s		毫秒:S
     *
     * @param formatString
     */
    public SchoolCalendar parse(String formatString, String content) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        try {
            currentTime.setTime(format.parse(content));
            return this;
        } catch (ParseException e) {
            return null;
        }
    }

    public SchoolCalendar setDate(int year, int month, int day) {
        currentTime.set(year, month, day);
        return this;
    }

    public Calendar getCalendar() {
        return currentTime;
    }

    public Date getDate() {
        return currentTime.getTime();
    }

    public int getDayOfWeek() {
        int weekDay = currentTime.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            return 7;
        } else {
            return weekDay - 1;
        }
    }

    public int getDay() {
        return currentTime.get(Calendar.DATE);
    }

    public int getMonth() {
        return currentTime.get(Calendar.MONTH) + 1;
    }

    public int getYear() {
        return currentTime.get(Calendar.YEAR);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SchoolCalendar) {
            if (getDayOfTerm() == ((SchoolCalendar) o).getDayOfTerm()) {
                return true;
            }
        }
        return false;
    }
}
