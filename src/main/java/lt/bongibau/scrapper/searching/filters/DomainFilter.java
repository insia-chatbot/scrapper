package lt.bongibau.scrapper.searching.filters;

import java.net.URL;

/**
 * Represents a filter for URLs, which can be used to accept or deny URLs
 */
public class DomainFilter extends Filter {


    public DomainFilter(String filterStr, Type type) {
        super(filterStr, type);
    }

    /**
     * Check if the URL is accepted by the filter
     *
     * @param url URL to check
     * @return true if the URL is accepted by the filter
     */
    public boolean check(URL url) {
        String[] urlDomains = url.getHost().split("\\.");
        String[] hosts = this.getFilterStr().split("\\.");

        if (hosts.length > urlDomains.length) {
            return false;
        }

        for (int i = hosts.length - 1; i >= 0; i--) {
            if (!hosts[i].equals(urlDomains[i + urlDomains.length - hosts.length])) {
                return false;
            }
        }

        return true;
    }
}
