package ru.enikeev.stolplusapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.enikeev.stolplusapp.model.Favorite;
import ru.enikeev.stolplusapp.model.Furniture;
import ru.enikeev.stolplusapp.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    //Найти всё избранное пользователя
    List<Favorite> findByUser(User user);

    //Найти конкретную запись избранного
    Optional<Favorite> findByUserAndFurniture(User user, Furniture furniture);

    //проверить есть ли товар в избранном у пользователя
    boolean existsByUserAndFurniture(User user, Furniture furniture);

    //удалить из избранного
    void deleteByUserAndFurniture(User user, Furniture furniture);

    //Найти популярные товары
    @Query( "SELECT f.furniture, COUNT(f) as popularity " +
            "FROM Favorite f " +
            "GROUP BY f.furniture " +
            "ORDER BY popularity DESC " )
    List<Object[]> findPopularFurniture();

}
