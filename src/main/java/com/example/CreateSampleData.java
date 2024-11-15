package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Presentacion;
import com.example.entities.Producto;
import com.example.services.PresentacionService;
import com.example.services.ProductoService;

@Configuration
public class CreateSampleData {

    @Bean
    public CommandLineRunner samplesData(ProductoService productoService,
            PresentacionService presentacionService) {

        return args -> {
            presentacionService.save(
                    Presentacion.builder()
                            .id(1)
                            .name("unidad")
                            .build()
            );

            presentacionService.save(
                    Presentacion.builder()
                            .id(2)
                            .name("decena")
                            .build()
            );

            productoService.save(
                    Producto.builder()
                            .id(1)
                            .name("rezma de papel")
                            .description("Description")
                            .price(3.75)
                            .stock(10)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(2)
                            .name("cartas")
                            .description("Description")
                            .price(1)
                            .stock(10)
                            .presentacion(presentacionService.findById(2))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(3)
                            .name("guitarra de juguete")
                            .description("Description")
                            .price(4.5)
                            .stock(5)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(4)
                            .name("teclado para computadora")
                            .description("Description")
                            .price(15)
                            .stock(5)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(5)
                            .name("teclado para laptop")
                            .description("Description")
                            .price(40)
                            .stock(5)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(6)
                            .name("bocinas bluetooth")
                            .description("Description")
                            .price(15)
                            .stock(5)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(7)
                            .name("lapices 2b")
                            .description("Description")
                            .price(1.50)
                            .stock(4)
                            .presentacion(presentacionService.findById(2))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(8)
                            .name("plumas color azul")
                            .description("Description")
                            .price(2)
                            .stock(10)
                            .presentacion(presentacionService.findById(2))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(9)
                            .name("monitor del 15p")
                            .description("Description")
                            .price(40)
                            .stock(5)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(10)
                            .name("cargador samsung")
                            .description("Description")
                            .price(10)
                            .stock(10)
                            .presentacion(presentacionService.findById(1))
                            .build());
            productoService.save(
                    Producto.builder()
                            .id(11)
                            .name("mouse básico")
                            .description("Description")
                            .price(5)
                            .stock(5)
                            .presentacion(presentacionService.findById(1))
                            .build());

        };
    }
}
