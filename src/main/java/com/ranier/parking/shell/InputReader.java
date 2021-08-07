package com.ranier.parking.shell;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.springframework.util.StringUtils;

public class InputReader {

    private LineReader lineReader;
    private ShellHelper shellHelper;

    public InputReader(LineReader lineReader, ShellHelper shellHelper) {
        this.lineReader = lineReader;
        this.shellHelper = shellHelper;
    }

    /**
     * Prompts user for input
     * 
     * @param prompt
     * @return
     * 
     */
    public String prompt(String prompt) {
        try {
            return lineReader.readLine(prompt + ": ");
        } catch (UserInterruptException e) {
            shellHelper.printError("Input has been interrupted!, please try again");
            throw e;
        }
    }

    /**
     * Loops until one value from the list of options is selected, printing each
     * option on its own line.
     *
     */
    public String selectFromList(String headingMessage, String promptMessage, Map<String, String> options,
            boolean ignoreCase, String defaultValue) {
        String answer;
        Set<String> allowedAnswers = new HashSet<>(options.keySet());
        if (defaultValue != null && !defaultValue.equals("")) {
            allowedAnswers.add("");
        }
        shellHelper.print(String.format("%s: ", headingMessage));
        do {
            for (Map.Entry<String, String> option : options.entrySet()) {
                String defaultMarker = null;
                if (defaultValue != null) {
                    if (option.getKey().equals(defaultValue)) {
                        defaultMarker = "*";
                    }
                }
                if (defaultMarker != null) {
                    shellHelper
                            .printInfo(String.format("%s [%s] %s ", defaultMarker, option.getKey(), option.getValue()));
                } else {
                    shellHelper.print(String.format("  [%s] %s", option.getKey(), option.getValue()));
                }
            }
            answer = lineReader.readLine(String.format("%s: ", promptMessage));
        } while (!containsString(allowedAnswers, answer, ignoreCase) && "" != answer);

        if (!StringUtils.hasText(answer) && allowedAnswers.contains("")) {
            return defaultValue;
        }
        return answer;
    }

    private boolean containsString(Set<String> l, String s, boolean ignoreCase) {
        if (!ignoreCase) {
            return l.contains(s);
        }
        Iterator<String> it = l.iterator();
        while (it.hasNext()) {
            if (it.next().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
}
