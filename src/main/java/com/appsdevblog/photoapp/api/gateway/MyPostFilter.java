package com.appsdevblog.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global Post Filter
 */
@Component
public class MyPostFilter implements GlobalFilter, Ordered {

	final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// TODO Auto-generated method stub
		
		logger.info("MyPostFilter");
		
		// Crear Runnable
		Runnable run1 = () -> {
			// TODO
			logger.info("My last post filter is executed...");
		};
		
		//
		Mono<Void> mon1 = Mono.fromRunnable(run1);
		
		// Delegar al nuevo filtro de la cadena
		// Despues se ejecuta el Mono que contiene el runnable
		Mono<Void> ret = chain.filter(exchange).then(mon1);
		
		return ret;
	}

	/**
	 * Orden de ejecucion del Post Filtro: 4-3-2-1-0
	 * Se ejecuta el ultimo: 0 
	 */
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0; 
	}

}
