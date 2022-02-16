package com.glycemic.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.glycemic.model.City;
import com.glycemic.repository.CityRepository;
import com.glycemic.util.EResultInfo;
import com.glycemic.util.ResultTemplate;

@Service
public class CityService {
	
	@Autowired
	private CityRepository cityRepo;
	
	public LinkedHashMap<ResultTemplate,Object> cityList(){
		LinkedHashMap<ResultTemplate,Object> result = new LinkedHashMap<>();
		
		List<City> cities = cityRepo.findAll();
		
		result.put(EResultInfo.status, false);
		result.put(EResultInfo.errors, 1);
		result.put(EResultInfo.message, "Şehir(ler) bulunamadı.");
		result.put(EResultInfo.result, new int[0]);
		
		if(!cities.isEmpty()) {
			result.put(EResultInfo.status, true);
			result.put(EResultInfo.errors, 0);
			result.put(EResultInfo.message, "Şehir(ler) bulundu.");
			result.put(EResultInfo.result, cities);
		}
		
		return result;
	}
	
}
