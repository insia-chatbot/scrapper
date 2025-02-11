package lt.bongibau.scrapper.searching.filters;

import java.net.URL;

public abstract class Filter {
    public enum Type {
        DENY,
        ACCEPT
    }

    private final String filterStr;
    private final Type type;

    public Filter(String filterStr, Type type) {
        this.filterStr = filterStr;
        this.type = type;
    }

    public abstract boolean check(URL url);

    public Type getType(){
        return this.type;
    }
    public String getFilterStr(){
        return this.filterStr;
    }

}
