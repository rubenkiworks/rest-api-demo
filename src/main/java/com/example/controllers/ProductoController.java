package com.example.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import lombok.RequiredArgsConstructor;


/**
* La clase tiene que devolver y recibir datos en formato de JSON (Javascript Object Notation),
* es decir, los metodos de la clase, para lo cual hay que anotarla con la anotacion @RestController
* 
* Una API REST esta orientada a recursos, por lo cual el controlador se acostumbra a anotar tambien 
* con el tipo de recurso que va a manejar, y segun el verbo del protocolo HTTP que se utilice se estara
* recibiendo una peticion concreta.
*/
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<Producto> findAll(){

        return productoService.findAll();

    }
}
