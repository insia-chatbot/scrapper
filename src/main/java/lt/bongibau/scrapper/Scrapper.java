package lt.bongibau.scrapper;

import lt.bongibau.scrapper.searching.SearchManager;
import lt.bongibau.scrapper.searching.filters.DomainFilter;
import lt.bongibau.scrapper.searching.filters.Filter;
import lt.bongibau.scrapper.searching.filters.FilterContainer;
import lt.bongibau.scrapper.searching.filters.PathFilter;

import java.io.*;
import java.net.*;
import java.util.List;

public class Scrapper {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        FilterContainer filters = new FilterContainer(List.of(
                new PathFilter("login", DomainFilter.Type.DENY),
                new PathFilter("logout", DomainFilter.Type.DENY),
                new PathFilter("_authenticate", DomainFilter.Type.DENY),
                new PathFilter("calendar", DomainFilter.Type.DENY),
                new DomainFilter("moodle.insa-toulouse.fr", DomainFilter.Type.ACCEPT)
        ));

        SearchManager searchManager = new SearchManager(
                List.of(new URL("https://moodle.insa-toulouse.fr")),
                filters
        );

        List<URL> links = searchManager.start(16);

        File file = new File("moodle.insa-toulouse.txt");
        file.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        System.out.println("Found " + links.size() + " links.");

        for (URL link : links) {
            writer.write(link.toString() + "\n");
        }

        writer.close();
    }
}
