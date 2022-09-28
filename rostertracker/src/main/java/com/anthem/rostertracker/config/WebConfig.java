package com.anthem.rostertracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@EnableWebMvc
@Configuration
@ComponentScan
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RosterConfig rosterConfig;

    public static String addSlashToPath(String path) {
        if (path != null && !path.endsWith("/")) {
            return path + "/";
        }
        return path;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("file:" + addSlashToPath(rosterConfig.getTemplatesFolder()));
        templateResolver.setSuffix(".html");
        return templateResolver;
    }
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }
    @Bean
    ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        ViewResolverRegistry registry = new ViewResolverRegistry(null, applicationContext);
        resolver.setTemplateEngine(templateEngine());
        resolver.setCache(false);
        registry.viewResolver(resolver);
        return resolver;
    }

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {  };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        String templatesFolder = rosterConfig.getTemplatesFolder();
        registry.addResourceHandler("/**")
                .addResourceLocations(templatesFolder, addSlashToPath(templatesFolder), "file:" + templatesFolder, "file:" + addSlashToPath(templatesFolder))
                .resourceChain(true).addResolver(new PathResourceResolver());
    }
}