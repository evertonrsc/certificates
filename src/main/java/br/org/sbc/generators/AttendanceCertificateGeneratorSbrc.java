package br.org.sbc.generators;

import br.org.sbc.model.Attendee;
import br.org.sbc.model.Certificate;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AttendanceCertificateGeneratorSbrc extends AbstractCertificateGenerator {

    public AttendanceCertificateGeneratorSbrc(Certificate certificate, String outputPrefix,
                                              float fontSize, float positionY) {
        this.certificate = certificate;
        this.outputPrefix = outputPrefix;
        this.fontSize = fontSize;
        this.positionY = positionY;
    }

    public String generate(Attendee attendee) {
        insertAttendeeName(attendee);
        applyReadOnlyPermissions();
        return saveCertificate(attendee);
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

    private String saveCertificate(Attendee attendee) {
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
            return outputFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
