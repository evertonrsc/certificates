import br.org.sbc.generators.PresentationCertificateGeneratorSbrc;
import br.org.sbc.importers.GSheetImporterPapersAuthors;
import br.org.sbc.model.Author;
import br.org.sbc.model.Certificate;
import br.org.sbc.model.Paper;
import com.google.api.AuthService;
import com.google.api.GMailService;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.File;
import java.util.List;

public class PresentationCertificateCoUrb {
    private static final String PAPER_SPREADSHEET_ID = "1zsgJSoK6oNotHHma6XywzjOFhY4e6K8OJjU5lqENtWQ";
    private static final String AUTHOR_SPREADSHEET_ID = "1-2_HEt7h58gXa5cS_jFNf1D1DgIyL_fcXvd2-aXrfck";
    private static final String PAPER_SHEET_RANGE = "Artigos!A2:E";
    private static final String AUTHOR_SHEET_RANGE = "Autores!A2:H";
    private static final String TEMPLATE_FILE = "resources/templates/certificado-courb2025.pdf";
    private static final String RFONT_FILE = "resources/fonts/OpenSans-Regular.ttf";
    private static final String IFONT_FILE = "resources/fonts/OpenSans-Italic.ttf";
    private static final String OUTPUT = "certificado-courb2025";
    private static final float FONT_SIZE = 15;
    private static final float POSITION_PAPER_TITLE = 320;
    private static final float POSITION_AUTHOR_LIST = 240;
    private static final String EMAIL_FROM = "sbrc2025@ufrn.br";
    private static final String EMAIL_SUBJECT = "[CoUrb 2025] Certificado de Apresentação";

    private static final AuthService authService =
            new AuthService(List.of(GmailScopes.MAIL_GOOGLE_COM, SheetsScopes.SPREADSHEETS_READONLY));

    public static void main(String[] args) {
        GSheetImporterPapersAuthors importerPapersAuthors = new GSheetImporterPapersAuthors(
                authService, PAPER_SPREADSHEET_ID, PAPER_SHEET_RANGE, AUTHOR_SPREADSHEET_ID, AUTHOR_SHEET_RANGE);

        List<Paper> papersCoUrb = importerPapersAuthors.loadPapersWithAuthors();

        GMailService gmailService = new GMailService(authService);

        for (Paper paper : papersCoUrb) {
            PresentationCertificateGeneratorSbrc cg = new PresentationCertificateGeneratorSbrc(
                    new Certificate(TEMPLATE_FILE, RFONT_FILE), IFONT_FILE, OUTPUT, FONT_SIZE,
                    POSITION_PAPER_TITLE, POSITION_AUTHOR_LIST);
            String certificateFile = cg.generate(paper);

            for (Author author : paper.getAuthors()) {
                String bodyText = String.format("""
                <html>
                    <body>
                        <p>Prezado(a) %s,</p>
                        <p>Em anexo, enviamos o <strong>Certificado de Apresentação</strong> do seu artigo
                        intitulado "%s" no CoUrb 2025.<br/>
                        Caso identifique qualquer inconsistência, pedimos a gentileza de entrar em contato
                        pelo e-mail <a href="mailto:sbrc2025@ufrn.br">sbrc2025@ufrn.br</a>.</p>
                        <p><em>Leandro Villas, Thiago Silva e Daniel Guidoni<br/>
                        Coordenadores do CoUrb 2025</em></p>
                    </body>
                </html>
                """, author.getName(), paper.getTitle());

                gmailService.sendEmailWithAttachment(EMAIL_FROM, author.getEmail(),
                        EMAIL_SUBJECT, bodyText, new File(certificateFile));
            }

            try {
                // To avoid possible denial from GMail API
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
