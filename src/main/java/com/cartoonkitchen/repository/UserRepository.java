package com.cartoonkitchen.repository;

import com.cartoonkitchen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    @Query("""
      select u from User u
      join Dish d on d.author = u
      group by u
      having count(d) >= :minCount
    """)
    List<User> findAuthorsWithAtLeast(@Param("minCount") long minCount);

    @Query("""
      select u from User u
      join Dish d on d.author = u
      group by u
      order by count(d) desc
    """)
    org.springframework.data.domain.Page<User> findTopAuthors(org.springframework.data.domain.Pageable pageable);

    long countByFavorites_Id(Long dishId);

}
