package com.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.demo.entities.Product;

@Repository("ProductRepository")
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
