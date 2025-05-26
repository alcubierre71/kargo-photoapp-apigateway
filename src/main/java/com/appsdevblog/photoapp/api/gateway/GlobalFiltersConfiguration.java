package com.appsdevblog.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global Filters ---> Pre Filters y Post Filters
 */
@Configuration
public class GlobalFiltersConfiguration {

	final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);
	
	/*
	 * Order de filtros
	 * PreFiltros --> se ejecutan primero los de orden inferior 0-1-2-3-4
	 * PostFiltros --> se ejecutan primero los de orden superior 4-3-2-1-0
	 */
	@Order(1)
	@Bean
	public GlobalFilter secondPreFilter() {
		
		GlobalFilter filtro = (ServerWebExchange exchange, GatewayFilterChain chain) -> {
			logger.info("My second global pre-filter is executed ... ");
			
			// Creamos la configuracion del post Filtro
			Runnable runPostFiltro = () -> {
				logger.info("My third global post-filter is executed...");
			};
			Mono<Void> monoPostFiltro = Mono.fromRunnable(runPostFiltro);
			
			// Delegamos la ejecucion al siguiente filtro de la cadena y ejecutamos el postFiltro
			Mono<Void> ret = chain.filter(exchange).then(monoPostFiltro);
			return ret;
		};
		
		return filtro;
		
	}
	
	@Order(2)
	@Bean
	public GlobalFilter thirdPreFilter() {
		
		GlobalFilter filtro = (ServerWebExchange exchange, GatewayFilterChain chain) -> {
			logger.info("My third global pre-filter is executed ... ");
			
			// Creamos la configuracion del post Filtro
			Runnable runPostFiltro = () -> {
				logger.info("My second global post-filter is executed...");
			};
			Mono<Void> monoPostFiltro = Mono.fromRunnable(runPostFiltro);
			
			// Delegamos la ejecucion al siguiente filtro de la cadena y ejecutamos el postFiltro
			Mono<Void> ret = chain.filter(exchange).then(monoPostFiltro);
			return ret;
		};
		
		return filtro;
		
	}
	
	@Order(3)
	@Bean
	public GlobalFilter fourthPreFilter() {
		
		GlobalFilter filtro = (ServerWebExchange exchange, GatewayFilterChain chain) -> {
			logger.info("My fourth global pre-filter is executed ... ");
			
			// Creamos la configuracion del post Filtro
			Runnable runPostFiltro = () -> {
				logger.info("My first global post-filter is executed...");
			};
			Mono<Void> monoPostFiltro = Mono.fromRunnable(runPostFiltro);
			
			// Delegamos la ejecucion al siguiente filtro de la cadena y ejecutamos el postFiltro
			Mono<Void> ret = chain.filter(exchange).then(monoPostFiltro);
			return ret;
		};
		
		return filtro;
		
	}
	
}
