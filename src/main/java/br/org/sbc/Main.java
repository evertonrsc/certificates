package br.org.sbc;

import br.org.sbc.model.Attendee;
import br.org.sbc.model.Certificate;

import java.util.List;

public class Main {
    public static final String REGISTRANTS_FILE = "resources/data/inscritos-sbrc2025.csv";
    public static final String TEMPLATE_FILE = "resources/templates/certificado-sbrc2025.pdf";
    public static final String FONT_FILE = "resources/fonts/OpenSans-SemiBold.ttf";
    public static final String OUTPUT = "certificado-sbrc2025";
    public static final float FONT_SIZE = 18;
    public static final float POSITION_Y = 305;

    public static void main(String[] args) {
        CsvImporterAttendees csvImporter = new CsvImporterAttendees(REGISTRANTS_FILE);
        List<Attendee> attendeeList = csvImporter.loadAttendees();

        CertificateGenerator cg = new CertificateGenerator(new Certificate(TEMPLATE_FILE, FONT_FILE),
                OUTPUT, FONT_SIZE, POSITION_Y);
        for (Attendee attendee : attendeeList) {
            cg.generate(attendee);
            break;
        }
    }
}