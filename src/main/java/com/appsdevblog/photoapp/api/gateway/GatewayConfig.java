package com.appsdevblog.photoapp.api.gateway;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	   @Bean
	    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
		   
			// Funcion Lambda que redirige los paths /users al endpoint del microservicio users-service 
			Function<PredicateSpec, Buildable<Route>> funcRouteUsers = 
								r -> r.path("/users/**").uri("lb://users-service");
			// Funcion Lambda que redirige los paths /orders al endpoint del microservicio orders-service 
			Function<PredicateSpec, Buildable<Route>> funcRouteOrders = 
									r -> r.path("/orders/**").uri("lb://orders-service");
			// Funcion Lambda que redirige los paths /products al endpoint del microservicio products-service 
			Function<PredicateSpec, Buildable<Route>> funcRouteProducts = 
									r -> r.path("/products/**").uri("lb://products-service");
			
			RouteLocator routeLocator = builder.routes()
		            .route("users-service", funcRouteUsers)
		            .route("orders-service", funcRouteOrders)
		            .route("products-service", funcRouteProducts)
		            .build();
									
	        return routeLocator;
	        
	    }
	   
}
