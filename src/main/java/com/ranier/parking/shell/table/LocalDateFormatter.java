package com.ranier.parking.shell.table;

import org.springframework.shell.table.Formatter;

import java.text.SimpleDateFormat;
import java.sql.Timestamp;

/**
 * Utility tool to format timestamp. Note: currently supports only
 * java.sql.Timestamp
 */
public class LocalDateFormatter implements Formatter {

    private String pattern;

    public LocalDateFormatter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String[] format(Object value) {
        Timestamp timestamp = (Timestamp) value;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return new String[] { format.format(timestamp) };
    }
}