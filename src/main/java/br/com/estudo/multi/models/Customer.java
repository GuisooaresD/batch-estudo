package br.com.estudo.multi.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "document")
public class Customer {

    private String document;
    private Integer count;
}
