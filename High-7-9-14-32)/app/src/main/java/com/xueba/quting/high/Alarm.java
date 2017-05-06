package com.xueba.quting.high;

import android.app.AlarmManager;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by quting on 2015/7/2.
 */
public class Alarm
{
    private Calendar calendar;
    private AlarmManager alarmManager;
    public Calendar getCalendar()
    {
        return calendar;
    }
    public AlarmManager getAlarmManager()
    {
        return alarmManager;
    }
    public void setCalendar(Calendar argcalendar)
    {
        calendar=argcalendar;
    }
    public void setAlarmManager( AlarmManager argalarmManager)
    {
        alarmManager=argalarmManager;
    }

}
