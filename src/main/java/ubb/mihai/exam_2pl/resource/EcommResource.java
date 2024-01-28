package ubb.mihai.exam_2pl.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ubb.mihai.exam_2pl.service.Service;

@RestController
@RequestMapping("/ecomm")
@RequiredArgsConstructor
public class EcommResource {
    private final Service service;

    @GetMapping("/addToCart")
    public ResponseEntity<Void> addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Long quantity) {
        service.addToCart(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }
}
