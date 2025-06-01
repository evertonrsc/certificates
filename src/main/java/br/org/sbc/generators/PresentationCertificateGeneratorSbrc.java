package br.org.sbc.generators;

import br.org.sbc.model.Certificate;
import br.org.sbc.model.Paper;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class PresentationCertificateGeneratorSbrc extends AbstractCertificateGenerator {
    private static final float LEFT_MARGIN = 98;
    private static final float RIGHT_MARGIN = LEFT_MARGIN * 2;
    private final String OTHER_FONT;
    private final float POSITION_Y2;

    public PresentationCertificateGeneratorSbrc(Certificate certificate, String otherFont,
                                                String outputPrefix, float fontSize, float positionY,
                                                float positionY2) {
        this.certificate = certificate;
        this.OTHER_FONT = otherFont;
        this.outputPrefix = outputPrefix;
        this.fontSize = fontSize;
        this.positionY = positionY;
        this.POSITION_Y2 = positionY2;
    }

    public String generate(Paper paper) {
        insertPaperTitle(paper);
        insertAuthorList(paper);
        applyReadOnlyPermissions();
        return saveCertificate(paper);
    }

    private void insertPaperTitle(Paper paper) {
        try {
            InputStream fontStream = new FileInputStream(OTHER_FONT);
            PDType0Font fontPaperTitle = PDType0Font.load(this.certificate.getDocument(),
                    fontStream, true);

            float maxWidth = this.certificate.getPage().getMediaBox().getWidth() - RIGHT_MARGIN;
            List<String> lines = wrapText(paper.getTitle(), fontPaperTitle, this.fontSize, maxWidth);

            float lineHeight = this.fontSize + 4;
            float y = this.positionY;

            try (PDPageContentStream contentStream = new PDPageContentStream(this.certificate.getDocument(),
                    this.certificate.getPage(), PDPageContentStream.AppendMode.APPEND,true,
                    true)) {
                contentStream.setFont(fontPaperTitle, this.fontSize);
                for (String line : lines) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(LEFT_MARGIN, y);
                    contentStream.showText(line);
                    contentStream.endText();
                    y -= lineHeight;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertAuthorList(Paper paper) {
        try {
            float maxWidth = this.certificate.getPage().getMediaBox().getWidth() - RIGHT_MARGIN;
            List<String> lines = wrapText(paper.getAuthorList(), this.certificate.getFont(),
                    this.fontSize, maxWidth);

            float lineHeight = this.fontSize + 4;
            float y = POSITION_Y2;

            try (PDPageContentStream contentStream = new PDPageContentStream(this.certificate.getDocument(),
                    this.certificate.getPage(), PDPageContentStream.AppendMode.APPEND,true, true)) {
                contentStream.setFont(this.certificate.getFont(), this.fontSize);
                for (String line : lines) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(LEFT_MARGIN, y);
                    contentStream.showText(line);
                    contentStream.endText();
                    y -= lineHeight;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String saveCertificate(Paper paper) {
        String outputFile = "certificates" + File.separator + outputPrefix + "_" + paper.getId() + ".pdf";
        Path path = Paths.get(outputFile);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            this.certificate.getDocument().save(outputFile);
            this.certificate.getDocument().close();
            System.out.println("\n> Certificate generated for paper #" + paper.getId());
            return outputFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> wrapText(String text, PDType0Font font, float fontSize, float maxWidth) {
        text = text.replace("\n", " ");        // Ensure no line breaks
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split(" ");

        for (String word : words) {
            String potentialLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            try {
                float textWidth = font.getStringWidth(potentialLine) / 1000 * fontSize;
                if (textWidth <= maxWidth) {
                    currentLine.append(currentLine.isEmpty() ? word : " " + word);
                } else {
                    if (!currentLine.isEmpty()) {
                        lines.add(currentLine.toString());
                    }
                    currentLine = new StringBuilder(word);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}
