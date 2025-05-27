import br.org.sbc.CertificateGenerator;
import br.org.sbc.CsvImporterAttendees;
import com.google.api.AuthService;
import com.google.api.GMailService;
import br.org.sbc.model.Attendee;
import br.org.sbc.model.Certificate;

import java.io.File;
import java.util.List;

public class Main {
    private static final String REGISTRANTS_FILE = "resources/data/inscritos-sbrc2025.csv";
    private static final String TEMPLATE_FILE = "resources/templates/certificado-sbrc2025.pdf";
    private static final String FONT_FILE = "resources/fonts/OpenSans-SemiBold.ttf";
    private static final String OUTPUT = "certificado-sbrc2025";
    private static final float FONT_SIZE = 18;
    private static final float POSITION_Y = 305;
    private static final String EMAIL_FROM = "sbrc2025@ufrn.br";
    private static final String EMAIL_SUBJECT = "[SBRC 2025] Certificado de Participação";

    public static void main(String[] args) {
        CsvImporterAttendees csvImporter = new CsvImporterAttendees(REGISTRANTS_FILE);
        List<Attendee> attendeeList = csvImporter.loadAttendees();

        GMailService gmailService = new GMailService(new AuthService());

        CertificateGenerator cg = new CertificateGenerator(new Certificate(TEMPLATE_FILE, FONT_FILE),
                OUTPUT, FONT_SIZE, POSITION_Y);
        for (Attendee attendee : attendeeList) {
            String certificateFile = cg.generate(attendee);

            gmailService.sendEmailWithAttachment(EMAIL_FROM, "everton.cavalcante@ufrn.br",
                    EMAIL_SUBJECT, "Teste de Envio de Certificado", new File(certificateFile));
            System.out.println();
            break;
        }
    }
}