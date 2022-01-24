package br.com.estudo.multi.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = "document")
@ToString()
public class Customer {

    private String document;
    private Integer count;
}
