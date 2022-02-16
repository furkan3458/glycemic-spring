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
import com.glycemic.util.EFoodStatus;

@Repository
public interface FoodRepository extends JpaRepository<Food,Long>{

	public Optional<Food> findByNameEqualsIgnoreCase(String name);
    
	public Optional<Food> findByUrlIgnoreCase(String url);

    public Optional<Food> findByUrlIgnoreCaseAndFoodStatus(String url, EFoodStatus foodStatus);

    public List<Food> findByCreatedByEqualsIgnoreCase(String createdBy);
    
    @Query(value="SELECT * FROM FOOD WHERE CREATED_BY = :created_by ORDER BY NAME ASC", countQuery="SELECT COUNT(*) FROM FOOD WHERE CREATED_BY = :created_by", nativeQuery=true)
    public Page<Food> findByCreatedByWithPage(@Param("created_by") String createdBy, Pageable pageable);

    public Optional<Food> findByCreatedByEqualsIgnoreCaseAndIdEquals(String createdBy, Long id);
    
    public List<Food> findAllByCategory(Category category);
    
    @Query(value="SELECT * FROM FOOD WHERE CATEGORY_ID = :category_id AND FOOD_STATUS = :food_status ORDER BY NAME ASC", countQuery="SELECT COUNT(*) FROM FOOD WHERE CATEGORY_ID = :category_id AND FOOD_STATUS = :food_status", nativeQuery=true)
    public Page<Food> findByCategoryIdPage(@Param("category_id") Long categoryId, @Param("food_status") Integer food_status, Pageable pageable);
    
    @Query(value="SELECT * FROM FOOD WHERE FOOD_STATUS = :food_status ORDER BY NAME ASC", countQuery="SELECT COUNT(*) FROM FOOD WHERE FOOD_STATUS = :food_status", nativeQuery=true)
    public Page<Food>findAllPageable(@Param("food_status") Integer foodStatus, Pageable pageable);
    
    @Query(value="SELECT F.* FROM FOOD F INNER JOIN CATEGORY C ON f.CATEGORY_ID = C.ID WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND C.URL LIKE :category AND F.FOOD_STATUS = :food_status ORDER BY F.NAME",nativeQuery=true,
    		countQuery="SELECT COUNT(F.ID) FROM FOOD F INNER JOIN CATEGORY C ON f.CATEGORY_ID = C.ID WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND C.URL LIKE :category AND F.FOOD_STATUS = :food_status")
    public Page<Food> foodsNameWithCategoryJoinAndLimited(@Param("name") String name, @Param("category") String category, @Param("food_status") Integer foodStatus, Pageable pageable);
    
    @Query(value="SELECT F.* FROM FOOD F WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND F.FOOD_STATUS = :food_status ORDER BY F.NAME",nativeQuery=true,
    		countQuery="SELECT COUNT(F.ID) FROM FOOD F WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND F.FOOD_STATUS = :food_status")
    public Page<Food> foodsNameWithAll(@Param("name") String name, @Param("food_status") Integer foodStatus, Pageable pageable);
    
    @Query(value="SELECT F.* FROM FOOD F INNER JOIN CATEGORY C ON f.CATEGORY_ID = C.ID WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND C.URL LIKE :category AND F.CREATED_BY = :created_by ORDER BY F.NAME",nativeQuery=true,
    		countQuery="SELECT COUNT(F.ID) FROM FOOD F INNER JOIN CATEGORY C ON f.CATEGORY_ID = C.ID WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND C.URL LIKE :category AND F.CREATED_BY = :created_by")
    public Page<Food> foodsNameWithCategoryJoinAndLimitedForUser(@Param("name") String name, @Param("category") String category, @Param("created_by") String createdBy, Pageable pageable);
    
    @Query(value="SELECT F.* FROM FOOD F WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND F.CREATED_BY=:created_by ORDER BY F.NAME",nativeQuery=true,
    		countQuery="SELECT COUNT(F.ID) FROM FOOD F WHERE LOWER(F.NAME) LIKE LOWER(CONCAT('%',:name,'%')) AND F.CREATED_BY=:created_by")
    public Page<Food> foodsNameWithAllForUser(@Param("name") String name, @Param("created_by") String createdBy, Pageable pageable);
}
