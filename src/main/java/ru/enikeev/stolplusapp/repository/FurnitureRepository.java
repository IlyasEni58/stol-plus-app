package ru.enikeev.stolplusapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.enikeev.stolplusapp.model.Furniture;
import ru.enikeev.stolplusapp.model.enums.Material;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface FurnitureRepository extends JpaRepository<Furniture, UUID> {

    //поиск по названию (частичное совпадение
    List<Furniture> findByNameContainingIgnoreCase(String name);

    //поиск по материалу
    List<Furniture> findByMaterial(Material material);

    //поиск в ценовом диапазоне
    List<Furniture> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    //поиск по нескольким материалам
    List<Furniture> findByMaterialIn(List<Material> materials);

    //сортировка по цене (возрастание)
    List<Furniture> findAllByOrderByPriceAsc();

    //сортировка по цене (убывание)
    List<Furniture> findAllByOrderByPriceDesc();

    //поиск товаров в категории
    @Query("SELECT f FROM Furniture f JOIN f.categories c WHERE c.id = :categoryId ")
    List<Furniture> findByCategoryId(@Param("categoryId") UUID categoryId);

    UUID id(UUID id);
}
