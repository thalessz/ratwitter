package com.thalessz.ratwitter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatter {
    public static String formatDate(String inputDate) {
        // Formato de entrada atualizado
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Formato de saída
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a yyyy-MM-dd");

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}