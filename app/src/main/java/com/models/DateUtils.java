package com.models;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static String formatDate(String isoDate) {
        return format(isoDate, "dd/MM/yyyy");
    }

    public static String formatMonthYear(String isoDate) {
        return format(isoDate, "MM/yyyy");
    }

    private static String format(String isoDate, String outputFormat) {
        try {
            // Leitura da data ISO em UTC
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // trata a string como UTC

            Date date = inputFormat.parse(isoDate);

            // Formatação também em UTC (evita mudar o dia/mês local)
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());
            outputFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            return outputFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }
}
