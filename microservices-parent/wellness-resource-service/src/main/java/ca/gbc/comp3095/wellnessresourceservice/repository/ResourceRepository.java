package ca.gbc.comp3095.wellnessresourceservice.repository;

import ca.gbc.comp3095.wellnessresourceservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // Existing method
    List<Resource> findByCategory(String category);

    // Case-insensitive method
    @Query("SELECT r FROM Resource r WHERE LOWER(r.category) = LOWER(:category)")
    List<Resource> findByCategoryIgnoreCase(@Param("category") String category);
}
