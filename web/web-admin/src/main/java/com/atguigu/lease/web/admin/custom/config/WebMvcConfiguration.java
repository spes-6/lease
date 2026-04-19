package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
//import com.atguigu.lease.web.admin.custom.converter.StringToItemTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//自定义converter的注册


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
//    @Autowired
//    private StringToItemTypeConverter converter;
    @Autowired
    private StringToBaseEnumConverterFactory factory;
    @Override
    public void addFormatters(FormatterRegistry registry) {

//        registry.addConverter(this.converter);
        registry.addConverterFactory(this.factory);
    }

}
