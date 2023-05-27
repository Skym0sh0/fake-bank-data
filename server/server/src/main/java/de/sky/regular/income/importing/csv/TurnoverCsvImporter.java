package de.sky.regular.income.importing.csv;

import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class TurnoverCsvImporter {
    public void parseForPreview(InputStream is) throws Exception {
        System.out.println("parse for preview");

        System.out.println(
                new String(is.readAllBytes())
        );
    }
}
