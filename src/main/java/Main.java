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

        for (Attendee attendee : attendeeList) {
            CertificateGenerator cg = new CertificateGenerator(new Certificate(TEMPLATE_FILE, FONT_FILE),
                    OUTPUT, FONT_SIZE, POSITION_Y);
            String certificateFile = cg.generate(attendee);

            String bodyText = String.format("""
                <html>
                    <body>
                        <p>Prezado(a) %s,</p>
                        <p>Agradecemos novamente por sua participação no SBRC 2025, realizado em Natal-RN.</p>
                        <p>Em anexo, enviamos o seu <strong>Certificado de Participação</strong> no evento.<br/>
                        Caso identifique qualquer inconsistência, pedimos a gentileza de entrar em contato pelo e-mail
                        <a href="mailto:sbrc2025@ufrn.br">sbrc2025@ufrn.br</a>.</p>
                        <p>Finalmente, gostaríamos de convidá-lo(a) a responder a uma <strong>pesquisa de satisfação</strong>,
                        acessível pelo link abaixo:<br/>
                        <a href="https://forms.gle/gSLrGNnnXMJvUoyG6" target="_blank">https://forms.gle/gSLrGNnnXMJvUoyG6</a>.<br/>
                        Sua opinião é muito importante para nós e ajudará a aprimorar futuras edições do SBRC.<br/>
                        A participação é voluntária e as respostas serão mantidas em total anonimato.</p>
                        <p><em>Everton Cavalcante e Roger Kreutz Immich<br/>
                        Coordenadores Gerais do SBRC 2025</em></p>
                    </body>
                </html>
                """, attendee.getName());

            gmailService.sendEmailWithAttachment(EMAIL_FROM, attendee.getEmail(),
                    EMAIL_SUBJECT, bodyText, new File(certificateFile));
            System.out.println();
        }
    }
}