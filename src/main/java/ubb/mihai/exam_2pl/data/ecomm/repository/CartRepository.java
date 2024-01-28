package ubb.mihai.exam_2pl.data.ecomm.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.mihai.exam_2pl.data.ecomm.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    default void createCart(Long userId, Long productId, Long quantity) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        save(cart);
    }

    @Transactional
    @Modifying
    @Query(value = "delete from Cart c " +
            "where c.userId = :userId " +
            "and c.productId = :productId " +
            "and c.quantity = :quantity")
    void removeCart(Long userId, Long productId, Long quantity);
}
