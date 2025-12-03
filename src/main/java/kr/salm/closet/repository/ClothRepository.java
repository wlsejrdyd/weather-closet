package kr.salm.closet.repository;

import kr.salm.closet.domain.Cloth;
import kr.salm.closet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothRepository extends JpaRepository<Cloth, Long> {
    
    List<Cloth> findByUserAndIsActiveTrue(User user);
    
    List<Cloth> findByUserIdAndIsActiveTrue(Long userId);
    
    @Query("SELECT c FROM Cloth c WHERE c.user.id = :userId " +
           "AND c.isActive = true " +
           "AND (c.tempMin IS NULL OR c.tempMin <= :temp) " +
           "AND (c.tempMax IS NULL OR c.tempMax >= :temp)")
    List<Cloth> findByUserIdAndTemperatureRange(
            @Param("userId") Long userId, 
            @Param("temp") int temperature);
    
    @Query("SELECT c FROM Cloth c WHERE c.user.id = :userId " +
           "AND c.category.id = :categoryId " +
           "AND c.isActive = true " +
           "AND (c.tempMin IS NULL OR c.tempMin <= :temp) " +
           "AND (c.tempMax IS NULL OR c.tempMax >= :temp)")
    List<Cloth> findByUserIdAndCategoryAndTemperature(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("temp") int temperature);
    
    List<Cloth> findByUserIdAndCategoryIdAndIsActiveTrue(Long userId, Long categoryId);
    
    long countByUserIdAndIsActiveTrue(Long userId);
}
