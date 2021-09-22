package com.city.temperature.vo;

import lombok.Data;

/**
 * @author xujianan
 * @date 2021/9/22 14:32
 */
@Data
public class WeatherVo {
    private String cityid;
    private String city;
    private Double temp;
    private String WD;
    private String WS;
    private String SD;
    private String AP;
    private String njd;
    private String WSE;
    private String time;
    private String sm;
    private String isRadar;
    private String Radar;
}
