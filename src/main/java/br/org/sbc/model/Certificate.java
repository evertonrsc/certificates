package br.org.sbc.model;

import br.org.sbc.CertificateGenerator;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Certificate {
    private final PDDocument document;
    private final PDPage page;
    private final PDType0Font font;

    public Certificate(String templateFile, String fontFile) {
        try {
            document = Loader.loadPDF(new File(templateFile));
            page = document.getPage(0);

            InputStream fontStream = CertificateGenerator.class.getResourceAsStream(fontFile);
            if (fontStream == null) {
                throw new RuntimeException("Font file not found");
            }
            font = PDType0Font.load(document, fontStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PDDocument getDocument() {
        return document;
    }

    public PDType0Font getFont() {
        return font;
    }

    public PDPage getPage() {
        return page;
    }
}
