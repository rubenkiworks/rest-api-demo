package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import jakarta.validation.Valid;
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

        List<Producto> productos;
        Sort sort = Sort.by("name");
        
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Producto> pageProductos = productoService.findAll(pageable);
            productos = pageProductos.getContent();
        }else{
            productos = productoService.findAll(sort);
        }
        
        return new ResponseEntity<>(productos, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveProducto(@Valid @RequestBody Producto producto, BindingResult results){
        
        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>();

        if (results.hasErrors()) {
            List<String> mensajesError = new ArrayList<>();

            List<ObjectError> objectErrors =  results.getAllErrors();

            objectErrors.stream().forEach(o -> mensajesError.add(o.getDefaultMessage()));

            responseAsMap.put("errores", mensajesError);
            responseAsMap.put("producto", producto);
            
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }
        
        try {
            Producto productoGuardado = productoService.save(producto);
            String message = "El producto se ha creado exitoxamente";
            
            responseAsMap.put("mensaje", message);
            responseAsMap.put("producto", productoGuardado);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            String errorMessage = "El producto no se pudo guardar y la causa mas probable del error es: "
            + e.getMostSpecificCause();

            responseAsMap.put("error", errorMessage);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        

        return responseEntity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid @RequestBody Producto producto, BindingResult results,
    @PathVariable Integer id){

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>();

        if (results.hasErrors()) {
            List<String> mensajesError = new ArrayList<>();

            List<ObjectError> objectErrors =  results.getAllErrors();

            objectErrors.stream().forEach(o -> mensajesError.add(o.getDefaultMessage()));

            responseAsMap.put("errores", mensajesError);
            responseAsMap.put("producto", producto);
            
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }
        
        try {
            producto.setId(id);
            Producto productoGuardado = productoService.save(producto);
            String message = "El producto se ha actualizado exitoxamente";
            
            responseAsMap.put("mensaje", message);
            responseAsMap.put("producto", productoGuardado);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            String errorMessage = "El producto no se pudo guardar y la causa mas probable del error es: "
            + e.getMostSpecificCause();

            responseAsMap.put("error", errorMessage);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findByIdProducto(@PathVariable Integer id){
        ResponseEntity<Map<String, Object>> responseEntity = null;

        var responseAsMap = new HashMap<String, Object>();

        try {
            Producto producto = productoService.findById(id);
            if(producto != null){
                String successMessage = "El producto se ha encontrado";
                responseAsMap.put("mensaje", successMessage);
                responseAsMap.put("producto", producto);

                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            }else{
                String notFoundMessage = "El producto con id " + id + " no se ha encontrado";
                responseAsMap.put("mensaje", notFoundMessage);

                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            String errorMessage = "Error grave y la causa mas probable del error es: "
            + e.getMostSpecificCause();

            responseAsMap.put("error", errorMessage);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
