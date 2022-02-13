package com.glycemic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.glycemic.model.Category;
import com.glycemic.model.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food,Long>{

public Optional<Food> findByNameEqualsIgnoreCase(String name);
    
    public Optional<Food> findByUrlIgnoreCase(String url);

    public List<Food> findByCreatedByEqualsIgnoreCase(String createdBy);

    public Optional<Food> findByCreatedByEqualsIgnoreCaseAndIdEquals(String createdBy, Long id);
    
    public List<Food> findAllByCategory(Category category);
    
    @Query(value="SELECT * FROM FOOD WHERE CATEGORY_ID = :category_id ORDER BY NAME ASC", countQuery="SELECT COUNT(*) FROM FOOD WHERE CATEGORY_ID = :category_id", nativeQuery=true)
    public Page<Food> findByCategoryIdPage(@Param("category_id") Long categoryId, Pageable pageable);
    
    @Query(value="SELECT * FROM FOOD ORDER BY NAME ASC", countQuery="SELECT COUNT(*) FROM FOOD", nativeQuery=true)
    public Page<Food>findAllPageable(Pageable pageable);
    
    @Query(value="SELECT F.* FROM FOOD F INNER JOIN CATEGORY C ON f.CATEGORY_ID = C.ID WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND C.URL LIKE :category",nativeQuery=true,
    		countQuery="SELECT COUNT(F.*) FROM FOOD F INNER JOIN CATEGORY C ON f.CATEGORY_ID = C.ID WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND C.URL LIKE :category")
    public Page<Food> foodsNameWithCategoryJoinAndLimited(@Param("name") String name, @Param("category") String category, Pageable pageable);
    
    @Query(value="SELECT F.* FROM FOOD F WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%'))",nativeQuery=true,
    		countQuery="SELECT COUNT(F.*) FROM FOOD F WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%'))")
    public Page<Food> foodsNameWithAll(@Param("name") String name , Pageable pageable);
}
