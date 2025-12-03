package kr.salm.closet.repository;

import kr.salm.closet.domain.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    
    List<Outfit> findByUserId(Long userId);
    
    @Query("SELECT o FROM Outfit o WHERE o.user.id = :userId " +
           "AND o.weatherType = :weatherType " +
           "AND (o.tempMin IS NULL OR o.tempMin <= :temp) " +
           "AND (o.tempMax IS NULL OR o.tempMax >= :temp)")
    List<Outfit> findByUserIdAndWeatherCondition(
            @Param("userId") Long userId,
            @Param("weatherType") Outfit.WeatherType weatherType,
            @Param("temp") int temperature);
    
    List<Outfit> findByUserIdAndIsAiGeneratedTrue(Long userId);
}
