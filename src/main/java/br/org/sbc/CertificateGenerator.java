package br.org.sbc;

import br.org.sbc.model.Attendee;
import br.org.sbc.model.Certificate;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class CertificateGenerator {
    private final Certificate certificate;
    private final String outputPrefix;
    private final float fontSize;
    private final float positionY;

    public CertificateGenerator(Certificate certificate, String outputPrefix, float fontSize, float positionY) {
        this.certificate = certificate;
        this.outputPrefix = outputPrefix;
        this.fontSize = fontSize;
        this.positionY = positionY;
    }

    public void generate(Attendee attendee) {
        insertAttendeeName(attendee);
        applyReadOnlyPermissions();
        saveCertificate(attendee);
    }

    private void insertAttendeeName(Attendee attendee) {
        try {
            float stringWidth = certificate.getFont().getStringWidth(attendee.getName()) / 1000 * fontSize;
            float pageWidth = certificate.getPage().getMediaBox().getWidth();
            float x = (pageWidth - stringWidth) / 2;

            try (PDPageContentStream contentStream = new PDPageContentStream(certificate.getDocument(),
                    certificate.getPage(), PDPageContentStream.AppendMode.APPEND,true, true)) {
                contentStream.setFont(certificate.getFont(), fontSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(x, positionY);
                contentStream.showText(attendee.getName());
                contentStream.endText();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyReadOnlyPermissions() {
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(true);                               // allow printing
        ap.setCanModify(false);                             // prevent modification
        ap.setCanExtractContent(false);                     // prevent copying/extracting

        String password = UUID.randomUUID().toString();     // random password
        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, null, ap);
        try {
            certificate.getDocument().protect(spp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveCertificate(Attendee attendee) {
        String outputFile = "certificates" + File.separator + outputPrefix + "_" + attendee.getId() + ".pdf";
        Path path = Paths.get(outputFile);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            certificate.getDocument().save(outputFile);
            certificate.getDocument().close();
            System.out.println("> Certificate generated for " + attendee.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
