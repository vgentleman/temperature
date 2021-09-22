package com.city.temperature.service.impl;

import com.alibaba.fastjson.JSON;
import com.city.temperature.constant.TemperatureApi;
import com.city.temperature.exception.BizException;
import com.city.temperature.service.TemperatureService;
import com.city.temperature.vo.ResultVO;
import com.city.temperature.vo.WeatherVo;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author xujianan
 * @date 2021/9/22 12:57
 */
@Service
public class TemperatureServiceImpl implements TemperatureService {

    @Autowired
    private RestTemplate restTemplate;

    private final RateLimiter rateLimiter = RateLimiter.create(100);


    private static final String PROVINCE_CODE_URL = "http://www.weather.com.cn/data/city3jdata/china.html";

    private static final String CITY_CODE_URL = "http://www.weather.com.cn/data/city3jdata/provshi/";

    private static final String COUNTRY_CODE_URL = "http://www.weather.com.cn/data/city3jdata/station/";

    private static final String TEMPERATURE = "http://www.weather.com.cn/data/sk/";

    private static Map<String,String>  provinceCodeMap = new HashMap<>();

    @PostConstruct
    public void init(){
        ResponseEntity<String> entity = restTemplate.getForEntity(PROVINCE_CODE_URL, String.class);
        provinceCodeMap =JSON.parseObject(entity.getBody(),Map.class);
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3)
    public Optional<Integer> getTemperature(String province, String city, String country) {
        boolean tryAcquire = rateLimiter.tryAcquire(0, TimeUnit.MILLISECONDS);
        if (!tryAcquire) {
            return null;
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       if(StringUtils.isBlank(provinceCodeMap.get(province))) {
           throw new BizException("province is not exist");
       }

       Map<String,String> cityMap =  this.getCity(province);
       if(StringUtils.isBlank(cityMap.get(city))){
           throw new BizException("city is not exist");
       }


        Map<String,String> countyMap =  this.getCounty(province,city);
        if(StringUtils.isBlank(countyMap.get(country))){
            throw new BizException("country is not exist");
        }

        ResponseEntity<String> temperatureEntity = restTemplate.getForEntity(TEMPERATURE + province + city  + country + ".html", String.class);
        if(StringUtils.isBlank(temperatureEntity.getBody())){
            throw new BizException("temperature data is not exist");
        }

        ResultVO result =JSON.parseObject(temperatureEntity.getBody(),ResultVO.class);

       return Optional.of(result.getWeatherinfo().getTemp().intValue());
    }



    @Override
    public Map<String,String> getCity(String provinceCode) {
        ResponseEntity<String> entity = restTemplate.getForEntity(CITY_CODE_URL + provinceCode + ".html", String.class);
        Map<String,String>  map = JSON.parseObject(entity.getBody(),Map.class);
        return map;
    }

    @Override
    public Map<String,String> getCounty(String provinceCode, String cityCode) {
        ResponseEntity<String> countryEntity = restTemplate.getForEntity(COUNTRY_CODE_URL + provinceCode + cityCode + ".html", String.class);
        Map<String,String>  map = JSON.parseObject(countryEntity.getBody(),Map.class);
        return map;
    }

}
