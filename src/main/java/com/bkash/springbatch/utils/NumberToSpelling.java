package com.bkash.springbatch.utils;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.CurrencyAmount;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class NumberToSpelling {

    private final static String COUNTRY_CODE = "BD";
    private final static String LANGUAGE = "en";
    private final static String FRACTION_UNIT_NAME = "Paisa";

    private static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MMM");
    private static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat MONTH_YEAR_DATE_FORMAT = new SimpleDateFormat("MMM yyyy");



    public static String generateDateRange(String dateInString) {
        return getFullDateRangeOfMonth(dateInString);
    }

    public static String generateBalanceInWord(String amountString) {
        return translateNumberToWord(amountString).trim();
    }

    private static String translateNumberToWord(String amount) {

        StringBuffer result = new StringBuffer();

        Locale locale = new Locale(LANGUAGE, COUNTRY_CODE);
        Currency currency = Currency.getInstance(locale);

        String inputArr[] = new BigDecimal(amount).abs().toPlainString().split("\\.+");
        RuleBasedNumberFormat rule = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);

        int i = 0;
        for (String input : inputArr) {
            CurrencyAmount crncyAmt = new CurrencyAmount(new BigDecimal(input), currency);
            if (i++ == 0) {
                result.append(rule.format(crncyAmt)).append(" Taka and "); // currency.getDisplayName()
            } else {
                result.append(rule.format(crncyAmt)).append(" " + FRACTION_UNIT_NAME);
            }
        }

        return capitalizeFirstLetters(result.toString());
    }

    private static String getFullDateRangeOfMonth(String dateInString) {

        Date currentDate = new Date(System.currentTimeMillis());
        Date givenDate = null;

        try {
            givenDate = YEAR_MONTH_FORMAT.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedGivenDate = YEAR_MONTH_FORMAT.format(givenDate);
        String formattedCurrentDate = YEAR_MONTH_FORMAT.format(currentDate);

        if (formattedCurrentDate.equalsIgnoreCase(formattedGivenDate)){
            return "01 " + MONTH_YEAR_DATE_FORMAT.format(givenDate) + " to " + FULL_DATE_FORMAT.format(currentDate);
        } else {
            Date lastDateOfMonth = getLastDateOfMonth(givenDate);
            return "01 " + MONTH_YEAR_DATE_FORMAT.format(givenDate) + " to " + FULL_DATE_FORMAT.format(lastDateOfMonth);
        }
    }


    private static String getLastDateOfMonth(String dateInString) {
        Date currentDate = new Date(System.currentTimeMillis());
        Date givenDate = null;

        try {
            givenDate = YEAR_MONTH_FORMAT.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedGivenDate = YEAR_MONTH_FORMAT.format(givenDate);
        String formattedCurrentDate = YEAR_MONTH_FORMAT.format(currentDate);

        if (formattedCurrentDate.equalsIgnoreCase(formattedGivenDate)){
            return FULL_DATE_FORMAT.format(currentDate);
        } else {
            Date lastDateOfMonth = getLastDateOfMonth(givenDate);
            return FULL_DATE_FORMAT.format(lastDateOfMonth);
        }
    }

    public static String convertToCurrencyFormat(String numberSt) {
        try {
            Double number = Double.parseDouble(numberSt);
            return NumberFormat.getInstance().format(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return numberSt;
        }
    }

    private static Date getLastDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }

    private static String capitalizeFirstLetters(String sentence) {
        String result = "";
        for(String v : sentence.split(" ")) {
            result += v.substring(0, 1).toUpperCase() + v.substring(1) + " ";
        }

        return result;
    }
}