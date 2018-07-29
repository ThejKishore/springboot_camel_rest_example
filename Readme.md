### Sample Spring Boot with Apache camel.

Note this project is sample that uses Spring boot 2.0.2 and apache camel 2.22.0

This shows the simple integration of the spring boot + camel and how the rest configuration works.

dependency list:

```groovy
ext {
        springBootVersion = '2.0.3.RELEASE'
        camelBootVersion = '2.22.0'
}

dependencies{
    compile("org.apache.camel:camel-spring-boot-starter:${camelBootVersion}")
   
    //apache camel
    compile "org.apache.camel:camel-jetty:${camelBootVersion}"
    compile "org.apache.camel:camel-jackson:${camelBootVersion}"

    compile 'org.springframework.boot:spring-boot-starter-data-jpa'
    compile "org.apache.camel:camel-jpa:${camelBootVersion}"
    compile "org.apache.camel:camel-sql:${camelBootVersion}"

    compileOnly('org.projectlombok:lombok')

    runtime 'com.h2database:h2'
    runtime('org.springframework.boot:spring-boot-devtools')
    
    testCompile('org.springframework.boot:spring-boot-starter-test')

}
```

#### how camel routing works :


```java
//rest Confiuration and jetty server construction... on port 9080 and host 0.0.0.0
// binding mode with json
restConfiguration()
    .component("jetty")
    .host("0.0.0.0").port("9080")
    .bindingMode(RestBindingMode.json);

// anything request coming to things.
rest("/things")
    // if its a post and incoming is type of Thing direct to createThing
    .post()
        .type(Thing.class)
            .to("direct:createThing")
    // if its a get return ThingSearchResult as the return type by sending to getThings
    .get()
        .outType(ThingSearchResult.class)
            .to("direct:getThings")
    // if its a get with id passed in path return Thing as the return type by sending to getThing
    .get("/{id}")
        .outType(Thing.class)
            .to("direct:getThing")
    // if its a get return Thing as the return Type by sending to removeThing
    .delete("/{id}")
        .outType(Thing.class)
            .to("direct:removeThing");

//Construct the camel routing logic 

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


```
