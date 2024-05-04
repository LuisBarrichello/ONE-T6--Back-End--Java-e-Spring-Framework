package br.com.alura.tabelafipe.service;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public interface IConvertsData {
    <T> T getData(String json, Class<T> classe);

    <T> List<T> getList(String json, Class<T> tClass);
}
