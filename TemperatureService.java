package com.city.temperature.service;

import java.util.Map;
import java.util.Optional;

/**
 * @author xujianan
 * @date 2021/9/22 12:56
 */
public interface TemperatureService {

     Optional<Integer> getTemperature(String province,String city,String country);

     Map<String,String> getCity(String provinceCode);

     Map<String,String> getCounty(String provinceCode, String cityCode);

}
