package lt.bongibau.scrapper.database;

import java.util.Date;

public class Data {
    private final String url;
    private final String content;
    private final Date dateModification;
    private final Date dateVisionage ;

    public Data(String url, String content, Date dateModification, Date dateVisionage) {
        this.url = url;
        this.content = content;
        this.dateModification = dateModification;
        this.dateVisionage = dateVisionage;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public Date getDateVisionage() {
        return dateVisionage;
    }
}
