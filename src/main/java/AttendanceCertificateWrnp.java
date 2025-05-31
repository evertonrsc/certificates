import br.org.sbc.CsvImporterAttendees;
import br.org.sbc.generators.AttendanceCertificateGeneratorWrnp;
import br.org.sbc.model.Attendee;
import br.org.sbc.model.Certificate;
import com.google.api.AuthService;
import com.google.api.GMailService;

import java.io.File;
import java.util.List;

public class AttendanceCertificateWrnp {
    private static final String REGISTRANTS_FILE = "resources/data/relatorio_final_WRNP.csv";
    private static final String TEMPLATE_FILE = "resources/templates/certificado-wrnp2025.pdf";
    private static final String FONT_FILE = "resources/fonts/Roboto-SemiBold.ttf";
    private static final String OUTPUT = "certificado-wrnp2025";
    private static final float FONT_SIZE = 30;
    private static final float POSITION_Y = 490;
    private static final String EMAIL_FROM = "sbrc2025@ufrn.br";
    private static final String EMAIL_SUBJECT = "[WRNP 2025] Certificado de Participação";

    public static void main(String[] args) {
        CsvImporterAttendees csvImporter = new CsvImporterAttendees(REGISTRANTS_FILE);
        List<Attendee> attendeeList = csvImporter.loadAttendees();

        GMailService gmailService = new GMailService(new AuthService());

        for (Attendee attendee : attendeeList) {
            AttendanceCertificateGeneratorWrnp cg = new AttendanceCertificateGeneratorWrnp(
                    new Certificate(TEMPLATE_FILE, FONT_FILE), OUTPUT, FONT_SIZE, POSITION_Y);
            String certificateFile = cg.generate(attendee);

            String bodyText = String.format("""
                <html>
                    <body>
                        <p>Prezado(a) %s,</p>
                        <p>Agradecemos novamente por sua participação no WRNP 2025, realizado em Natal-RN.</p>
                        <p>Em anexo, enviamos o seu <strong>Certificado de Participação</strong> no evento.<br/>
                        Caso identifique qualquer inconsistência, pedimos a gentileza de entrar em contato pelo e-mail
                        <a href="mailto:wrnp2025@rnp.br">wrnp2025@rnp.br</a>.</p>
                        <p><em>Comitê Organizador do WRNP 2025</em></p>
                    </body>
                </html>
                """, attendee.getName());

            gmailService.sendEmailWithAttachment(EMAIL_FROM, attendee.getEmail(),
                    EMAIL_SUBJECT, bodyText, new File(certificateFile));
            System.out.println();
        }
    }
}
