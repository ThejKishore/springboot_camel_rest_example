package com.kish.camel.learncamel;

import com.kish.camel.learncamel.model.Thing;
import com.kish.camel.learncamel.model.ThingSearchResult;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class TestCamel extends RouteBuilder {
    @Override
    public void configure() throws Exception {
       /* from("timer:something")
                .to("log:hello world");*/

        restConfiguration()
                .component("jetty")
                .host("0.0.0.0").port("9080")
                .bindingMode(RestBindingMode.json);

        rest("/things")
                .post()
                    .type(Thing.class)
                        .to("direct:createThing")
                .get()
                    .outType(ThingSearchResult.class)
                        .to("direct:getThings")
                .get("/{id}")
                    .outType(Thing.class)
                        .to("direct:getThing")
                .delete("/{id}")
                    .outType(Thing.class)
                        .to("direct:removeThing");


        from("direct:createThing")
                .to("jpa:com.kish.camel.learncamel.model.Thing");

        from("direct:getThing")
                .to("sql:select * from THING where id = :#${header.id}?dataSource=#dataSource&outputType=SelectOne")
                .bean("transformer","mapThing");

        from("direct:getThings")
                .setProperty("query").method("transformer", "constructQuery(${headers})")
                .toD("sql:${property.query}?dataSource=#dataSource")
                .bean("transformer", "mapThingSearchResults");

        from("direct:removeThing")
                .to("direct:getThing")
                .setProperty("thing", body())
                .to("sql:delete from THING where id = :#${body.id}?dataSource=#dataSource")
                .setBody(bodyAs(Thing.class));

    }
}
