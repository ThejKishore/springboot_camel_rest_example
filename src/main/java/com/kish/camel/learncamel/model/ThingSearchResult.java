package com.kish.camel.learncamel.model;


import lombok.Value;
import lombok.experimental.Wither;

import java.util.List;

@Value
@Wither
public class ThingSearchResult {
    Integer size;
    List<Thing> things;
}