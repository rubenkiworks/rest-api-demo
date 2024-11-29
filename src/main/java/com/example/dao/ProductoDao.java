package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entities.Presentacion;
import com.example.entities.Producto;

@Repository
public interface ProductoDao extends JpaRepository<Producto, Integer>{

    /**
    * Vamos a crear tres metodos personalizados,
    * que traigan la presentacion en una sola consulta, que es mas eficiente,
    * que primero traer el producto y luego una subconsulta para traer la presentacion
    * que es lo que por defecto ocurriria por esta establecido al fetchType a LAZY
    * 
    * 1. Recupera los productos paginados, es decir, de 10 en 10, de 20 en 20, etc
    * 2. Recupera los productos ordenados, sin paginacion
    * 3. Dado el id de un producto recupera el producto con su presentacion correspondiente
    * 
    * Para ello vamos a utilizar el lenguaje JPQL (Java Persistence Query Language), es muy similar a SQL pero
    * lo que se consulta son las entidades, no las tablas.
    * 
    * Y no se puede utilizar consultas de SQL nativo porque no soportan la paginacion y el ordenamiento directamente
    
    */

    @Query(value="select p from Producto p left join fetch p.presentacion", 
        countQuery = "select count(p) from Producto p left join p.presentacion")
    @Override
    public Page<Producto> findAll(Pageable pageable);

    @Override
    @Query(value="select p from Producto p left join fetch p.presentacion")
    public List<Producto> findAll(Sort sort);

    @Query(value="select p from Producto p left join fetch p.presentacion where p.id = :id")
    public Producto findById(int id);

    @Query(value="select p from Producto p left join fetch p.presentacion where p.stock = :stock order by p.price")
    public Page<Producto> findByStock(int stock, Pageable pageable);

    List<Producto> findByPresentacionAndStockLessThan(Presentacion presentacion, int stock);
}
