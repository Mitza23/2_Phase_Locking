package ubb.mihai.exam_2pl.data.warehouse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.mihai.exam_2pl.data.warehouse.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT CASE WHEN (p.quantity - :quantity) >= 0 THEN true ELSE false END FROM Product p WHERE p.id = :productId")
    boolean checkProductAvailability(Long productId, Long quantity);

    @Transactional
    @Modifying
    @Query("update Product p set p.quantity = p.quantity - :quantity where p.id = :productId")
    void decreaseQuantity(Long productId, Long quantity);

    @Transactional
    @Modifying
    @Query("update Product p set p.quantity = p.quantity + :quantity where p.id = :productId")
    void increaseQuantity(Long productId, Long quantity);
}
