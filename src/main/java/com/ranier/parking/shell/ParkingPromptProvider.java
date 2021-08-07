package com.ranier.parking.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class ParkingPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("CAR-PARKING-SYSTEM:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
    }

}
