package com.glycemic.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.glycemic.model.Category;
import com.glycemic.model.Food;
import com.glycemic.repository.CategoryRepository;
import com.glycemic.repository.FoodRepository;
import com.glycemic.util.EFoodStatus;
import com.glycemic.util.EPageInfo;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.Generator;
import com.glycemic.util.ResultTemplate;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository cateRepo;
	
	@Autowired
	private FoodRepository foodRepo;
	
	public LinkedHashMap<ResultTemplate,Object> categoryList(){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		List<Category> categories = cateRepo.findAll();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Kategori bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(!categories.isEmpty()) {
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kategori(ler) bulundu.");
			result.put(EResultInfo.result, categories);
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> categoryListWithFoods(){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		List<Category> categories = cateRepo.findAll();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Kategori bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(!categories.isEmpty()) {
			for(Category c : categories) {
				List<Food> foods = foodRepo.findAllByCategory(c);
				c.setFoods(foods);
			}
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kategori(ler) bulundu.");
			result.put(EResultInfo.result, categories);
		}
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> getCategoryByName(String name){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		String url = Generator.generateUrl(name);
		
		Optional<Category> category = cateRepo.findByUrlEqualsIgnoreCase(url);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 0);
		result.put(EResultInfo.message, "Kategori bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(category.isPresent()) {
			Pageable pageable = PageRequest.of(0, 3);
			Page<Food> foods = foodRepo.findByCategoryIdPage(category.get().getId(),EFoodStatus.ACCEPT.ordinal(), pageable);
			category.get().setFoods(foods.getContent());
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kategori(ler) bulundu.");
			result.put(EResultInfo.result, Arrays.asList(category.get()));
			result.put(EPageInfo.total, foods.getTotalElements());
			result.put(EPageInfo.totalPage, foods.getTotalPages());
			result.put(EPageInfo.page, 1);
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> getCategoryById(Long id){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Category> category = cateRepo.findById(id);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Kategori bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(category.isPresent()) {
			Pageable pageable = PageRequest.of(0, 3);
			Page<Food> foods = foodRepo.findByCategoryIdPage(category.get().getId(),EFoodStatus.ACCEPT.ordinal(),pageable);
			category.get().setFoods(foods.getContent());
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kategori(ler) bulundu.");
			result.put(EResultInfo.result, Arrays.asList(category.get()));
			result.put(EPageInfo.total, foods.getTotalElements());
			result.put(EPageInfo.totalPage, foods.getTotalPages());
			result.put(EPageInfo.page, 1);
		}
		
		return result;
	}
	
	
	public LinkedHashMap<ResultTemplate,Object> insert(Category category){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Category> optCategory = cateRepo.findByNameEqualsIgnoreCase(category.getName());
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Bu kategori daha önce eklenmiş.");
		result.put(EResultInfo.result, new int[0]);
		
		if(optCategory.isEmpty()) {
			String url = Generator.generateUrl(category.getName());
			category.setUrl(url);
			Category saved = cateRepo.save(category);
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kategori başarıyla eklendi.");
			result.put(EResultInfo.result, Arrays.asList(saved));
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> delete(Long id){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Category> category = cateRepo.findById(id);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Bu kategori daha önce eklenmiş.");
		result.put(EResultInfo.result, new int[0]);
		
		if(category.isPresent()) {
			
			try {
				Category deleted = category.get().copy();
				cateRepo.delete(category.get());
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.errors, 0);
				result.put(EResultInfo.message, "Kategori başarıyla temizlendi.");
				result.put(EResultInfo.result, Arrays.asList(deleted));
			}
			catch(Exception e) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.errors, 2);
				result.put(EResultInfo.message, "Kategoriyi silerken hata ile karşılaşıldı. İşlem gerçekleştirilemedi.");
				result.put(EResultInfo.result, new int[0]);
			}
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> update(Category category){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Category> searched = cateRepo.findById(category.getId());
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Böyle bir kategori bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(searched.isPresent()) {
			
			String url = Generator.generateUrl(category.getName());
			
			category.setUrl(url);
			category.setCreatedBy(searched.get().getCreatedBy());
			category.setCreatedDate(searched.get().getCreatedDate());
			
			Category updated = cateRepo.saveAndFlush(category);
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Kategori başarıyla güncellendi.");
			result.put(EResultInfo.result, Arrays.asList(updated));
		}
		
		return result;
	}
}
