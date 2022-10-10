package com.springsecuritystudy.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Entit {

    @GeneratedValue
    @Id
    private Long id;

    private String name;
}
