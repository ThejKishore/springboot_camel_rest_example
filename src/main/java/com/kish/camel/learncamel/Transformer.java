package com.kish.camel.learncamel;


import com.kish.camel.learncamel.model.Thing;
import com.kish.camel.learncamel.model.ThingSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component("transformer")
@Slf4j
public class Transformer {
    public Thing mapThing(Map map) {
       return  new Thing((Integer)map.get("id"), (String) map.get("name"),(String) map.get("owner"));
    }

    public String constructQuery(Map headers) {

        log.debug("incoming headers {}",headers);
        StringJoiner wheres = new StringJoiner(" and ");
        if (headers.get("name") !=null) {
            wheres.add("name = :#${header.name}");
        }
        if (headers.get("owner") != null) {
            wheres.add("owner = :#${header.owner}");
        }

        String query = "select * from THING";
        if (wheres.length() >0) {
            query += " where " + wheres.toString();
        }
        return query;
    }

    public ThingSearchResult mapThingSearchResults(List<Map> body) {
        List<Thing> things = body.stream().map(map -> mapThing(map)).collect(Collectors.toList());
       return  new ThingSearchResult(body.size(),things);
    }
}
