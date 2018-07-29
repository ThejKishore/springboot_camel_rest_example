package com.kish.camel.learncamel.model;


import lombok.Value;
import lombok.experimental.Wither;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Value
@Wither
@Entity(name = "THING")
public class Thing {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    Integer id;

    @Column(name = "NAME")
    String name;

    @Column(name = "OWNER")
    String owner;
}
