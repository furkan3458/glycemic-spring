package com.glycemic.service;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.glycemic.model.Food;
import com.glycemic.repository.FoodRepository;
import com.glycemic.util.EPageInfo;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ResultTemplate;

@Service
public class SearchService {

	@Autowired
	private FoodRepository foodRepo;
	
	public LinkedHashMap<ResultTemplate,Object> searchWith(String q, String category, int page){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		Page<Food> foods = null;
		
		if(category.equals("all")) {
			Pageable pageable = PageRequest.of(page-1, 3);
	    	foods = foodRepo.foodsNameWithAll(q,pageable);
		}else {
			Pageable pageable = PageRequest.of(page-1, 3);
			foods = foodRepo.foodsNameWithCategoryJoinAndLimited(q,category,pageable);
		}
		
		if(!foods.isEmpty()) {
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Sonuç(lar) bulundu.");
			result.put(EResultInfo.result, foods.get());
			result.put(EPageInfo.page,page);
			result.put(EPageInfo.total,foods.getTotalElements());
			result.put(EPageInfo.totalPage, foods.getTotalPages());
		}
		
		return result;
	}
}
