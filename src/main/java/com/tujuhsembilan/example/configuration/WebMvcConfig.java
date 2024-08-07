package com.tujuhsembilan.example.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(@NonNull CorsRegistry cors) {
    // @formatter:off
    cors
      .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("*")
        .allowedHeaders("*")
        .allowCredentials(true)
    ;
    // @formatter:on
  }

  @Override
  public void configureContentNegotiation(@NonNull ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(new MediaType("application", "vnd.api+json"), MediaType.APPLICATION_JSON);
  }

}
