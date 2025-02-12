package lt.bongibau.scrapper.database;

import java.time.LocalDateTime;

public record Data(String url, String content, LocalDateTime modificationDate, LocalDateTime viewingDate) {
}
