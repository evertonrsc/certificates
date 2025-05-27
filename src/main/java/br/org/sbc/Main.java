package br.org.sbc;

import br.org.sbc.model.Attendee;
import br.org.sbc.model.Certificate;

public class Main {
    public static final String TEMPLATE_FILE = "resources/templates/certificado-sbrc2025.pdf";
    public static final String FONT_FILE = "/fonts/OpenSans-SemiBold.ttf";
    public static final String OUTPUT = "certificado-sbrc2025";
    public static final float FONT_SIZE = 18;
    public static final float POSITION_Y = 305;

    public static void main(String[] args) {
        Attendee attendee = new Attendee(12345, "Everton Ranielly de Sousa Cavalcante",
                "everton.cavalcante@ufrn.br");
        Certificate certificate = new Certificate(TEMPLATE_FILE, FONT_FILE);

        CertificateGenerator cg = new CertificateGenerator(certificate, OUTPUT, FONT_SIZE, POSITION_Y);
        cg.generate(attendee);
    }
}