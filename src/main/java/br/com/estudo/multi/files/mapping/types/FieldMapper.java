package br.com.estudo.multi.files.mapping.types;

public interface FieldMapper<T> {

    T map(String value);

    Class<T> from();

}
