package lt.bongibau.scrapper.searching.filters;

import java.net.URL;

public class PathFilter extends Filter{
    public PathFilter(String filterStr, Type type) {
        super(filterStr, type);
    }

    public boolean check(URL url) {
        String urlPaths = url.getPath();
        String path= this.getFilterStr();
        if(path.length()>urlPaths.length()){
            return false;
        }
        return urlPaths.contains(path);
    }
}
