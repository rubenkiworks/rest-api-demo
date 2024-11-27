package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Producto;
import com.example.model.FileUploadResponse;
import com.example.services.PresentacionService;
import com.example.services.ProductoService;
import com.example.utilities.FileDeleteUtil;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;

import jakarta.transaction.Transactional;
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
    private final FileUploadUtil fileUploadUtil;
    private final FileDownloadUtil fileDownloadUtil;
    private final FileDeleteUtil fileDeleteUtil;
    private final PresentacionService presentacionService;
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
    @RequestParam(name="size", required=false) Integer size, @RequestParam(required=false) Boolean metodoStock){

        List<Producto> productos;
        Sort sort = Sort.by("name");
        
        if (metodoStock != null){
            Pageable pageable = PageRequest.of(page, size);
            Page<Producto> pageProductos = productoService.findByStock(5, pageable);
            productos = pageProductos.getContent();
        }
        else if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Producto> pageProductos = productoService.findAll(pageable);
            productos = pageProductos.getContent();
        }else{
            productos = productoService.findAll(sort);
        }
        
        return new ResponseEntity<>(productos, HttpStatus.OK);

    }

    @PostMapping(consumes="multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> saveProducto(
        @Valid @RequestPart(name="producto") Producto producto, BindingResult results,
            @RequestPart(name="file") MultipartFile file) throws IOException {

        ResponseEntity<Map<String, Object>> responseEntity;
        Map<String, Object> responseAsMap = new HashMap<>();

        // Lo primero que comprobamos es si hay errores en el producto recibido
        if (results.hasErrors()) {

            List<String> mensajesError = new ArrayList<>();

            List<ObjectError> objectErrors = results.getAllErrors();

            objectErrors.stream().forEach(objectError -> mensajesError.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", mensajesError);
            responseAsMap.put("producto", producto);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // Si no hay errores persistimos el producto y devolvemos informacion al
        // respecto.
        // Comprobando, previamente si me han enviado la imagen del producto, es decir,
        // el file en el cuerpo de la peticion
        if (!file.isEmpty()) {

            // Para gestionar el archivo recibido, vamos a crear un componente en un paquete llamado 
            // com.example.utilities, y en dicho componente crearemos un metodo que se encargara de 
            // guardar el archivo en una carpeta especifica del servidor, y devolver un codigo de 8 
            // caracteres alfanumerico (letras y numeros) generados aleatoriamente
            String fileCode = fileUploadUtil.saveFile(file.getOriginalFilename(), file);

            // Asociar el nombre del archivo recibido con la propiedad imagenProducto de la entidad 
            // Producto
            producto.setImagenProducto(fileCode + "-" + file.getOriginalFilename());

            // Hay que proporcionar informacion respecto a la imagen guardada
            // para lo cual, en un paquete model (com.example.model), crearemos un record
            // FileUploadResponse
            // FileUploadResponse fileUploadResponse = new FileUploadResponse(
            //     fileCode + "-" + file.getOriginalFilename(), 
            //     "/productos/fileDownload/" + fileCode ,
            //     file.getSize());

            // Con lombok creamos el objeto FileUploadResponse
            FileUploadResponse fileUploadResponse = FileUploadResponse.builder()
                .fileName(fileCode + "-" + file.getOriginalFilename())
                .downloadURI("/productos/fileDownload/" + fileCode)
                .fileSize(file.getSize())
                .build();

            responseAsMap.put("Info de la imagen", fileUploadResponse);

        }

        try {
            Producto productoGuardado = productoService.save(producto);
            String message = "El producto se ha creado exitosamente";
            responseAsMap.put("mensaje", message);
            responseAsMap.put("producto", productoGuardado);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            String errorMessage = "El producto no se pudo persistir y "
                    + "la causa mas probable del error es: " + e.getMostSpecificCause().getMessage();
            responseAsMap.put("error: ", errorMessage);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    @PutMapping("/{id}")
    @Transactional
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
            Throwable error = e.getMostSpecificCause();

            if (error != null) {
                String errorMessage = "No ha podido ser actualizado el producto cuyo id es: " + id
                + ", y la causa mas probable es: " + error;
                responseAsMap.put("mensaje", errorMessage);
                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }
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
            Throwable error = e.getMostSpecificCause();

            if (error != null) {
                String errorMessage = "No ha podido ser encontrado el producto cuyo id es: " + id
                + ", y la causa mas probable es: " + error;
                responseAsMap.put("mensaje", errorMessage);
                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return responseEntity;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Integer id) throws IOException{
        ResponseEntity<Map<String, Object>> responseEntity = null;

        var responseAsMap = new HashMap<String, Object>();

        try {
            Producto producto = productoService.findById(id);

            String imagenProducto = producto.getImagenProducto();

            String fileCode = imagenProducto.split("-")[0];

            if(fileCode != null){
                fileDeleteUtil.deleteFile(fileCode);
            }

            productoService.delete(producto);
            String successMessage = "El producto con id " + id + " ha sido eliminado";
            responseAsMap.put("mensaje", successMessage);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            Throwable error = e.getMostSpecificCause();

            if (error != null) {
                String errorMessage = "No ha podido ser eliminado el producto cuyo id es: " + id
                + ", y la causa mas probable es: " + error;
                responseAsMap.put("mensaje", errorMessage);
                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return responseEntity;
    }

    @GetMapping("/fileDownload/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileCode) throws IOException{
        Resource resource;

        try {
            resource = fileDownloadUtil.getFileAsResource(fileCode);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        if(resource == null){
            return new ResponseEntity<>("Fichero no encontrado", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";



        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
    }

    @GetMapping("/fileDownloadByIdProducto/{idProducto}")
    public ResponseEntity<?> downloadFileByEmpleadoId(@PathVariable int idProducto) throws IOException{
        Resource resource;

        try {
            resource = fileDownloadUtil.getFileAsResourceByIdProducto(idProducto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        if(resource == null){
            return new ResponseEntity<>("Fichero no encontrado", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";



        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
    }

    @GetMapping("/deleteFile/{fileCode}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileCode) throws IOException{
        ResponseEntity<Map<String, Object>> responseEntity;

        var responseAsMap = new HashMap<String, Object>();

        String responseDeleteMethod;

        try {
            responseDeleteMethod = fileDeleteUtil.deleteFile(fileCode);

            responseAsMap.put("mensaje", responseDeleteMethod);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return responseEntity;
    }

    @GetMapping("/product-for-presentation")
    public ResponseEntity<Map<String, Object>> productForPresentation(
        @RequestParam(required=false) String presentacion,
        @RequestParam(required=false) Integer stock
    ){
        ResponseEntity<Map<String, Object>> responseEntity;

        List<Producto> productos;

        var responseAsMap = new HashMap<String, Object>();
        try {
            productos = productoService.findAll();

            List<Producto> productosFiltrados = productos.stream()
            .filter(p -> p.getPresentacion().equals(presentacionService.findByName(presentacion))
                && p.getStock() < stock).collect(Collectors.toList());
            //.max((p1, p2) -> Integer.valueOf(p1.getStock()).compareTo(p2.getStock()))
            

            responseAsMap.put("mensaje", "El listado de productos con un stock menor de "
            + stock + "stock de la presentacion recibida (" + presentacion + ") es: ");
            responseAsMap.put("productos", productosFiltrados);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return responseEntity;
    }
}
