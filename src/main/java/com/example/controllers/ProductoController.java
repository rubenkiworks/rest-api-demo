package com.example.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    /**
     * El metodo siguiente va a responder a una peticion (request) del tipo
     * 
     * http://localhost:8080/productos?page=0&size=3
     * 
     * Donde los parametros page y size seran utilizados para la paginacion, que no son requeridos, es decir, 
     * que no es obligatorio que se suministren. Y en caso de no ser suministrados, los productos se van a devolver
     * ordenados por el nombre del producto.
     * 
     * IMPORTANTE!! 
     * 
     * Una API para que sea REST tiene que devolver informacion respecto a como ha sido cumplimentada la peticion, por ejemplo
     * 200 significaria un codigo de estado OK, 201 significaria CREATED, 400 NO ENCONTRADO, 500 el servidor no ha podido 
     * cumplimentar la peticion (Todos estos codigos se pueden buscar en Internet y concretamente en la pagina w3school)
     * 
     * https://www.w3schools.com/tags/ref_httpmessages.asp
     */ 
    @GetMapping
    public ResponseEntity<List<Producto>> findAll(@RequestParam(name="page", required=false) Integer page,
    @RequestParam(name="size", required=false) Integer size){

        List<Producto> productos = productoService.findAll();

        ResponseEntity<List<Producto>> responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);

        return responseEntity;

    }


}
