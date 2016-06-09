package com.mbh.mbutils;

import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created By MBH on 2016-06-09.
 */
public class MBDateTimeUtils {
    public interface OnDateChangedListener {
        void dateChangedSuccessfully();
        void tabletDateSameAsKioskDate();
    }

    public static String getCurrentTimeStamp() {
        return fromDate(new Date());
    }

    public static String fromDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(date);
    }

    public static Date fromString(String dateAsString) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date d = simpleDateFormat.parse(dateAsString);
            return d;
        } catch (Exception ex) {
            return null;
        }
    }
    private static void changeSystemTime(String year, String month, String day, String hour, String minute, String second) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String command = "date -s " + year + month + day + "." + hour + minute + second + "\n";
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSystemDate(String t_time, OnDateChangedListener onDateChangedListener) {
        try {
            if (t_time.equals("") || t_time.length() < 5) {
                return;
            }
            String[] separated = t_time.split("-");

            if (separated.length < 6) {
                return;
            }

            String t_year = separated[0];
            String t_month = separated[1];
            String t_day = separated[2];
            String t_hour = separated[3];
            String t_minute = separated[4];
            String t_second = separated[5];

            if (t_month.length() == 1) {
                t_month = "0" + t_month;
            }

            if (t_day.length() == 1) {
                t_day = "0" + t_day;
            }
            if (t_hour.length() == 1) {
                t_hour = "0" + t_hour;
            }
            if (t_minute.length() == 1) {
                t_minute = "0" + t_minute;
            }
            if (t_second.length() == 1) {
                t_second = "0" + t_second;
            }

            Calendar c = Calendar.getInstance();
            int c_year = c.get(Calendar.YEAR);
            int c_month = c.get(Calendar.MONTH);
            int c_day = c.get(Calendar.DAY_OF_MONTH);
            int c_hour = c.get(Calendar.HOUR_OF_DAY);
//            int c_minute = c.get(Calendar.MINUTE);
            if (c_year != Integer.parseInt(t_year)
                    || c_month + 1 != Integer.parseInt(t_month)
                    || c_day != Integer.parseInt(t_day)
                    || c_hour != Integer.parseInt(t_hour)
//                    || (c_minute - Integer.parseInt(t_minute) > 5 || c_minute
//                    - Integer.parseInt(t_minute) < -5)
                    ) {

                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(
                        process.getOutputStream());
                // os.writeBytes("date -s 20120419.024012; \n");
                os.writeBytes("date -s " + t_year + t_month + t_day
                        + "." + t_hour + t_minute + t_second
                        + "; \n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();

                if (onDateChangedListener != null) {
                    onDateChangedListener.dateChangedSuccessfully();
                }
                process.waitFor();
            }else {
                if (onDateChangedListener != null) {
                    onDateChangedListener.tabletDateSameAsKioskDate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String diffBetween(Date start, Date end) {
        long diff = end.getTime() - start.getTime();

        int hours = (int) ((diff / 1000) / 3600);
        int minutes = (int) (((diff / 1000) / 60) % 60);
        int seconds = (int) ((diff / 1000) % 60);

        return String.format("%02d:%02d:%02d", hours , minutes, seconds );
    }

    public static String checkNowIfBetween(Date start, Date end) {
        Date now = Calendar.getInstance().getTime();
//        if ( start.compareTo(now) * now.compareTo(end) > 0 )
//        if ( !now.before(start) && !now.after(end)) // include end points
        if ( now.after(start) && now.before(end)) // exclude end points
            return "YES";
        else
            return "NO";
    }
}
