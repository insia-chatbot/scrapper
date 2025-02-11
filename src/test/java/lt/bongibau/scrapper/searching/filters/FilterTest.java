package lt.bongibau.scrapper.searching.filters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DomainFilterTest {

    @ParameterizedTest()
    @MethodSource("provideFilter")
    @DisplayName("Test check")
    void check(DomainFilter domainFilter, String url, boolean expected) throws MalformedURLException {
        assertEquals(expected, domainFilter.check(URI.create(url).toURL()), "Check should return expected value.");
    }


    static Stream<Arguments> provideFilter() {
        return Stream.of(
                // Normal cases
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://example.org", false),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.DENY), "http://example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.DENY), "http://example.org", false),

                // Subdomains
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://sub.example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://subexample.com", false),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.DENY), "http://sub.example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.DENY), "http://example.com", true),

                // SubSubdomains
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://sub.sub.example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://sub.example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://sub.subexample.com", false),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://example.com", true),
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://example.com:80", true),

                // Long urls
                Arguments.of(new DomainFilter("example.com", DomainFilter.Type.ACCEPT), "http://sub.example.com/hello/world?query=hello&date=now", true)
        );
    }

}