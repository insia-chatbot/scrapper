package lt.bongibau.scrapper.database;

import java.util.Date;

public record Data(String url, String content, Date modificationDate, Date viewingDate) {
}
