package com.glycemic.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.glycemic.config.AuditAwareConfigurer;
import com.glycemic.model.Food;
import com.glycemic.model.FoodNutritional;
import com.glycemic.model.Nutritional;
import com.glycemic.repository.FoodNutritionalRepository;
import com.glycemic.repository.FoodRepository;
import com.glycemic.repository.NutritionalRepository;
import com.glycemic.util.EFoodStatus;
import com.glycemic.util.EPageInfo;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.Generator;
import com.glycemic.util.ResultTemplate;

@Service
public class FoodService {

	@Autowired
	private FoodRepository foodRepo;
	
	@Autowired
	private FoodNutritionalRepository fNutrRepo;

	@Autowired
	private NutritionalRepository nutriRepo;
	
	@Autowired
	private AuditAwareConfigurer auditAwareConfig;
	
	public LinkedHashMap<ResultTemplate,Object> foodList() {
    	LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
    	
    	result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
    	
		Pageable pageable = PageRequest.of(0, 3);
    	Page<Food> foods = foodRepo.findAllPageable(EFoodStatus.ACCEPT.ordinal(),pageable);
    	
    	if(!foods.isEmpty()) {
    		result.put(EResultInfo.status, true);
    		result.put(EResultInfo.errors, 0);
    		result.put(EResultInfo.message, "Sonuç(lar) bulundu.");
    		result.put(EResultInfo.result, foods.get());
    		result.put(EPageInfo.page, 1);
    		result.put(EPageInfo.total, foods.getTotalElements());
    		result.put(EPageInfo.totalPage, foods.getTotalPages());
    	}
    	
        return result;
    }
	
	public LinkedHashMap<ResultTemplate,Object> userFoodList() {
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        
        result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
        
        if (oUserName.isPresent()) {
        	Pageable pageable = PageRequest.of(0, 3);
            Page<Food> foods = foodRepo.findByCreatedByWithPage(oUserName.get(),pageable);
            
            result.put(EResultInfo.status, true);
    		result.put(EResultInfo.errors, 0);
    		result.put(EResultInfo.message, "Sonuç(lar) bulundu.");
    		result.put(EResultInfo.result, foods.get());
    		result.put(EPageInfo.page, 1);
    		result.put(EPageInfo.total, foods.getTotalElements());
    		result.put(EPageInfo.totalPage, foods.getTotalPages());
        }

        return result;
    }
	
	@Transactional
	public LinkedHashMap<ResultTemplate,Object> insert(Food foods) {
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Food> oFoods = foodRepo.findByNameEqualsIgnoreCase(foods.getName());
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Bu sonuç daha önce eklendi.");
		result.put(EResultInfo.result, new int[0]);
		
        
        if (oFoods.isEmpty()) {
        	foods.setId(0L);
        	foods.setEnabled(false);
            foods.setUrl(Generator.generateUrl(foods.getName()));
            foods.setFoodStatus(EFoodStatus.WAITING);
            Food food = foodRepo.save(foods);
            
            
            if(!foods.getFoodNutritional().isEmpty()) {
            	for(FoodNutritional fn : foods.getFoodNutritional()) {
            		Optional<Nutritional> nutritional;
            		
            		if(fn.getNutritional().getId() != null && fn.getRate() != null && fn.getPercent() != null && (nutritional = nutriRepo.findById(fn.getNutritional().getId())).isPresent()) {
            			FoodNutritional fnNew = new FoodNutritional();
            			fnNew.setFood(food);
            			fnNew.setNutritional(nutritional.get());
            			fnNew.setRate(fn.getRate());
            			fnNew.setPercent(fn.getPercent());
            			fNutrRepo.save(fnNew);
            		}
            	}
            }
            
            result.put(EResultInfo.status, true);
    		result.put(EResultInfo.errors, 0);
    		result.put(EResultInfo.message, "Sonuç(lar) eklendi.");
    		result.put(EResultInfo.result, Arrays.asList(food));
        }
        
        return result;
    }
	
	public LinkedHashMap<ResultTemplate,Object> delete(Long id) {
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Food> food = foodRepo.findById(id);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
        
        if (food.isPresent()) {           
            result.put(EResultInfo.status, false);
    		result.put(EResultInfo.errors, 0);
    		result.put(EResultInfo.message, "Sonuç(lar) temizlendi.");
    		result.put(EResultInfo.result, Arrays.asList(food.get()));
    		foodRepo.delete(food.get());
        }
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> update(Food updateFood) {
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Food> food = foodRepo.findById(updateFood.getId());
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(food.isPresent()) {
			Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
			
			if(auditAwareConfig.isAdminUser() || food.get().getCreatedBy().equals(oUserName.get())) {
				updateFood.setEnabled(false);
				updateFood.setFoodStatus(EFoodStatus.WAITING);
				updateFood.setUrl(Generator.generateUrl(updateFood.getName()));
				updateFood.setCreatedBy(food.get().getCreatedBy());
				updateFood.setCreatedDate(food.get().getCreatedDate());
				
				Food updated = foodRepo.save(updateFood);
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.errors, 0);
				result.put(EResultInfo.message, "Sonuç(lar) güncellendi.");
				result.put(EResultInfo.result, Arrays.asList(updated));
				
			}else {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.errors, 2);
				result.put(EResultInfo.message, "Yetkiniz olmayan bir ürünü güncelleyemezsiniz.");
				result.put(EResultInfo.result, new int[0]);
			}
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> check(Long id) {
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Food> food = foodRepo.findById(id);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(food.isPresent()) {	
			List<FoodNutritional> food_nutritionals = fNutrRepo.findAllByFood(food.get());
			Food cloneFood = food.get().copy();
			cloneFood.setFoodNutritional(food_nutritionals);
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Ürün doğrulandı.");
			result.put(EResultInfo.result, Arrays.asList(cloneFood));
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> getByName(String name, String status) {
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		String url = Generator.generateUrl(name);
		
		Optional<Food> food = null;
		
		if(status.equals("all"))
			food = foodRepo.findByUrlIgnoreCase(url);
		else
			food = foodRepo.findByUrlIgnoreCaseAndFoodStatus(url, EFoodStatus.ACCEPT);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(food.isPresent()) {
			List<FoodNutritional> food_nutritionals = fNutrRepo.findAllByFood(food.get());
			Food cloneFood = food.get().copy();
			cloneFood.setFoodNutritional(food_nutritionals);
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Sonuç(lar) bulundu.");
			result.put(EResultInfo.result, Arrays.asList(cloneFood));
		}
		
		return result;
	}
}
