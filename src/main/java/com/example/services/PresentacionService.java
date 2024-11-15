package com.example.services;

import java.util.List;

import com.example.entities.Presentacion;

public interface PresentacionService {
    public List<Presentacion> findAll();
    public void save(Presentacion presentacion);
    public Presentacion findById(int id);
}
