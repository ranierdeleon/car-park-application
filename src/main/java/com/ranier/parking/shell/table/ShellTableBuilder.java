package com.ranier.parking.shell.table;

import java.sql.Timestamp;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.CellMatchers;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

public class ShellTableBuilder {
    public static String build(TableModel model) {

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.oldschool);
        tableBuilder.on(CellMatchers.ofType(Timestamp.class))
                .addFormatter(new LocalDateFormatter("dd MMM yyyy hh:mm:ss a"));
        return tableBuilder.build().render(80);
    }
}
