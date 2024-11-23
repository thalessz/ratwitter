package com.thalessz.ratwitter.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.TimeZone

object DateFormatter {
    @JvmStatic
    fun formatDate(inputDate: String): String? {
        // Formato de entrada atualizado para ISO 8601
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Define o fuso horário como UTC

        // Formato de saída
        val outputFormat = SimpleDateFormat("hh:mm a yyyy-MM-dd")
        outputFormat.timeZone =
            TimeZone.getDefault() // Define o fuso horário de saída como o padrão do sistema

        try {
            val date =
                inputFormat.parse(inputDate) // Converte a string de entrada em um objeto Date
            return outputFormat.format(date) // Formata e retorna a data no formato desejado
        } catch (e: ParseException) {
            e.printStackTrace()
            return null // Retorna null em caso de erro
        }
    }
}