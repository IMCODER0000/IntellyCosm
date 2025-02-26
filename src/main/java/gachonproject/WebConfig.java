package gachonproject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String resourcePath = "/User_analysis_image/**";
    private String realPath = "/home/t24106/v1.0src/Image/User/";


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/User_analysis_image/**")
                .addResourceLocations("file:///home/t24106/v1.0src/Image/User/");

        registry.addResourceHandler("/admin_image/**")
                .addResourceLocations("file:///home/t24106/v1.0src/Image/Admin/");

       
    }


}
