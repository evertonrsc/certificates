package br.org.sbc.model;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;

public class Certificate {
    private final PDDocument document;
    private final PDPage page;
    private final PDType0Font font;

    public Certificate(String templateFile, String fontFile) {
        try {
            document = Loader.loadPDF(new File(templateFile));
            page = document.getPage(0);
            font = PDType0Font.load(document, new File(fontFile));
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
