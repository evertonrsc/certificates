package br.org.sbc;

import br.org.sbc.model.Attendee;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.util.List;

public class CsvImporterAttendees {
    private final String csvFile;

    public CsvImporterAttendees(String csvFile) {
        this.csvFile = csvFile;
    }

    public List<Attendee> loadAttendees() {
        try (Reader reader = new FileReader(csvFile)) {
            CsvToBean<Attendee> csvToBean = new CsvToBeanBuilder<Attendee>(reader)
                    .withType(Attendee.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
