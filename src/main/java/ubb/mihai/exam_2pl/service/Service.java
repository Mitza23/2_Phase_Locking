package ubb.mihai.exam_2pl.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ubb.mihai.exam_2pl.data.ecomm.repository.CartRepository;
import ubb.mihai.exam_2pl.data.ecomm.repository.UserRepository;
import ubb.mihai.exam_2pl.data.warehouse.repository.ProductRepository;
import ubb.mihai.exam_2pl.scheduling.Scheduler;
import ubb.mihai.exam_2pl.scheduling.domain.Command;
import ubb.mihai.exam_2pl.scheduling.domain.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class Service {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final Scheduler scheduler;

    private final AtomicLong counter = new AtomicLong();

    public void addToCart(Long userId, Long productId, Long quantity) {
        Command checkAvailability = new Command("products",
                "R",
                () -> productRepository.checkProductAvailability(productId, quantity),
                () -> {
                });

        Command decreaseStock = new Command("products",
                "W",
                () -> productRepository.decreaseQuantity(productId, quantity),
                () -> productRepository.increaseQuantity(productId, quantity));

        Command createCart = new Command("cart",
                "W",
                () -> cartRepository.createCart(userId, productId, quantity),
                () -> cartRepository.removeCart(userId, productId, quantity));

        Transaction addToCart = new Transaction(generateId(),
                List.of(checkAvailability, decreaseStock, createCart),
                LocalDateTime.now());

        scheduler.scheduleTransaction(addToCart);
    }

    private synchronized Long generateId() {
        return counter.incrementAndGet();
    }
}
