package ru.enikeev.stolplusapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.enikeev.stolplusapp.model.Category;
import ru.enikeev.stolplusapp.model.Furniture;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    //найти категорию по названию
    Optional<Category> findByName(String name);

    //Найти мебель в категории
    @Query("SELECT c.furniture FROM Category c WHERE c.id = :categoryId")
    List<Furniture> findFurnitureByCategoryID(@Param("categoryId") UUID categoryId);

    //проверить существование категории
    boolean existsByName(String name);

}
