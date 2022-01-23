package br.com.estudo.multi.files.mapping;

@FunctionalInterface
public interface FieldLineMapper<T> {

    T mapFieldLine(String line);

}
