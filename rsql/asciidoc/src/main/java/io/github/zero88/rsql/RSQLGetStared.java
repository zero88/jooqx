package io.github.zero88.rsql;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jooq.Condition;

import io.vertx.docgen.Source;
import io.zero88.rsql.jooq.JooqRSQLParser;
import io.github.zero88.sample.model.pgsql.Tables;

@Source
public class RSQLGetStared {

    public void init() {
        String url = "http://localhost:8080/api/author?q=(FIRST_NAME==zero88)";
        String query = splitQuery(url).get("q"); // simple utils function to get URL query
        System.out.println(query); // => (FIRST_NAME==zero88)

        JooqRSQLParser parser = JooqRSQLParser.DEFAULT;
        Condition condition = parser.criteria(query, Tables.AUTHORS);
        System.out.println(condition.toString()); // => "AUTHOR"."FIRST_NAME" = 'zero88'
    }

    public static Map<String, String> splitQuery(String url) {
        try {
            Map<String, String> queryPairs = new LinkedHashMap<>();
            String query = new URL(url).getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                               URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return queryPairs;
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new RSQLGetStared().init();
    }

}
