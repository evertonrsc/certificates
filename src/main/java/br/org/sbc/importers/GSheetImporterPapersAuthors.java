package br.org.sbc.importers;

import br.org.sbc.model.Author;
import br.org.sbc.model.Language;
import br.org.sbc.model.Paper;
import com.google.api.AuthService;
import com.google.api.GSheetsService;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class GSheetImporterPapersAuthors {
    private final String paperSheetId;
    private final String paperSheetRange;
    private final String authorSheetId;
    private final String authorSheetRange;
    private final AuthService authService;

    public GSheetImporterPapersAuthors(AuthService authService, String paperSheetId,
                                       String paperSheetRange, String authorSheetId, String authorSheetRange) {
        this.paperSheetId = paperSheetId;
        this.paperSheetRange = paperSheetRange;
        this.authorSheetId = authorSheetId;
        this.authorSheetRange = authorSheetRange;
        this.authService = authService;
    }

    public List<Paper> loadPapersWithAuthors() {
        List<Paper> papers = new ArrayList<Paper>();

        GSheetsService gSheetsService = new GSheetsService(authService);
        List<List<Object>> paperEntries = gSheetsService.getValues(paperSheetId, paperSheetRange);
        List<List<Object>> authorEntries = gSheetsService.getValues(authorSheetId, authorSheetRange);

        for (List<Object> paperEntry : paperEntries) {
            int paperId = Integer.parseInt(paperEntry.get(0).toString());
            Language paperLanguage = paperEntry.get(1).toString().equals("pt") ? Language.PT : Language.EN;

            List<Author> authors = new ArrayList<Author>();
            for (List<Object> authorEntry : authorEntries) {
                int paperIdAuthor = Integer.parseInt(authorEntry.getFirst().toString());
                if (paperIdAuthor == paperId) {
                    String authorName = authorEntry.get(1).toString().replaceFirst("\\s+$", "") + " ";
                    if (authorEntry.get(2) != null && !authorEntry.get(2).toString().isEmpty()) {
                        authorName += authorEntry.get(2).toString().replaceFirst("\\s+$", "") + " ";
                    }
                    authorName += authorEntry.get(3).toString().replaceFirst("\\s+$", "");
                    authors.add(new Author(authorName, authorEntry.get(7).toString()));
                }
            }

            Paper paper = new Paper(paperId, paperEntry.get(3).toString(), paperEntry.get(4).toString(),
                    paperLanguage);
            paper.setAuthors(authors);
            papers.add(paper);
        }

        return papers;
    }
}
