package br.org.sbc.model;

import java.util.ArrayList;
import java.util.List;

public class Paper {
    private int id;
    private String titlePt;
    private String titleEn;
    private Language language;
    private List<Author> authors;

    public Paper(int id, String titlePt, String titleEn, Language language) {
        this.id = id;
        this.titlePt = titlePt;
        this.titleEn = titleEn;
        this.language = language;
        this.authors = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitlePt() {
        return titlePt;
    }

    public void setTitlePt(String titlePt) {
        this.titlePt = titlePt;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return language == Language.PT ? titlePt : titleEn;
    }

    public String getAuthorList() {
        StringBuilder authorList = new StringBuilder("\n");
        for (int i = 0; i < authors.size(); i++) {
            authorList.append(authors.get(i).getName());
            if (i < authors.size()-1) {
                if (i != authors.size() - 2) {
                    authorList.append(", ");
                } else {
                    authorList.append(" e ");
                }
            }
        }
        return authorList.toString();
    }
}
