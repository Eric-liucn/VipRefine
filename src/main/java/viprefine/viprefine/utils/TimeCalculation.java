package viprefine.viprefine.utils;

import java.time.*;

public class TimeCalculation {

    public static String getDuration(Instant instant){
        LocalDateTime localDate = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime expireLocalDate = LocalDateTime.ofInstant(instant,zoneId);
        Duration duration = Duration.between(localDate,expireLocalDate);
        Duration absDuration = duration.abs();
        int totalSecond =(int) absDuration.getSeconds();
        int days = totalSecond/(3600*24);
        int hours = (totalSecond%(3600*24))/(3600);
        int minutes = (totalSecond%(3600))/60;
        return String.format("&d%d&e天&d%d&e小时&d%d&e分钟",days,hours,minutes);
    }

    public static String getTodayTimeLabel(){
        LocalDate localDate = LocalDate.now();
        return localDate.getYear()+"_"+localDate.getMonthValue()+"_"+localDate.getDayOfMonth();
    }
}
