package com.glycemic.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.glycemic.model.Nutritional;
import com.glycemic.repository.NutritionalRepository;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ResultTemplate;

@Service
public class NutritionalService {

	@Autowired
	private NutritionalRepository nutriRepo;
	
	public LinkedHashMap<ResultTemplate,Object> nutritionalList(){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Sonuç bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		List<Nutritional> nutritionals = nutriRepo.findAll();
		
		if(!nutritionals.isEmpty()) {
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Sonuç(lar) bulundu.");
			result.put(EResultInfo.result, nutritionals);
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> insert(Nutritional nutritional){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Bu besin değeri daha önce eklenmiş.");
		result.put(EResultInfo.result, new int[0]);
		
		Optional<Nutritional> nutrOpt = nutriRepo.findByName(nutritional.getName());
		
		if(nutrOpt.isEmpty()) {
			Nutritional saved = nutriRepo.save(nutritional);
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Besin değeri başarıyla eklendi.");
			result.put(EResultInfo.result, Arrays.asList(saved));
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> delete(Long id){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Nutritional> nutritional = nutriRepo.findById(id);
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Besin değeri bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(nutritional.isPresent()) {
			
			try {
				Nutritional deleted = nutritional.get().copy();
				nutriRepo.delete(nutritional.get());
				
				result.put(EResultInfo.status, true);
				result.put(EResultInfo.errors, 0);
				result.put(EResultInfo.message, "Besin değeri başarıyla temizlendi.");
				result.put(EResultInfo.result, Arrays.asList(deleted));
			}
			catch(Exception e) {
				result.put(EResultInfo.status, false);
				result.put(EResultInfo.errors, 2);
				result.put(EResultInfo.message, "Besin değeri silerken hata ile karşılaşıldı. İşlem gerçekleştirilemedi.");
				result.put(EResultInfo.result, new int[0]);
			}
		}
		
		return result;
	}
	
	public LinkedHashMap<ResultTemplate,Object> update(Nutritional nutritional){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		Optional<Nutritional> searched = nutriRepo.findById(nutritional.getId());
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Böyle bir besin değeri bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(searched.isPresent()) {
			
			nutritional.setCreatedBy(searched.get().getCreatedBy());
			nutritional.setCreatedDate(searched.get().getCreatedDate());
			
			Nutritional updated = nutriRepo.saveAndFlush(nutritional);
			
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Besin değeri başarıyla güncellendi.");
			result.put(EResultInfo.result, Arrays.asList(updated));
		}
		
		return result;
	}
}
