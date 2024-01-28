package ubb.mihai.exam_2pl.data.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.mihai.exam_2pl.data.ecomm.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByName(String name);
}
