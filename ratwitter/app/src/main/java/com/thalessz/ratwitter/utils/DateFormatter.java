package com.thalessz.ratwitter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatter {
    public static String formatDate(String inputDate) {
        // Formato de entrada atualizado para ISO 8601
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Define o fuso horário como UTC

        // Formato de saída
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a yyyy-MM-dd");
        outputFormat.setTimeZone(TimeZone.getDefault()); // Define o fuso horário de saída como o padrão do sistema

        try {
            Date date = inputFormat.parse(inputDate); // Converte a string de entrada em um objeto Date
            return outputFormat.format(date); // Formata e retorna a data no formato desejado
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Retorna null em caso de erro
        }
    }
}