package com.appsdevblog.photoapp.api.gateway;

import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global PreFilter
 */
@Component
public class MyPreFilter implements GlobalFilter, Ordered {

	final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// TODO Auto-generated method stub
		
		logger.info("My first Pre-filter is executed...");
		
		// Obtenemos el path de la peticion entrante
		String requestPath = exchange.getRequest().getPath().toString();
		logger.info("Request path = " + requestPath);
		
		// Obtenemos los headers de la peticion
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> headerNames = headers.keySet();
		
		logger.info("Http Headers ...");
		// Obtenemos la lista de key-value de los headers de la peticion
		Consumer <? super String> consum = (String headerName) -> {
			String headerValue = headers.getFirst(headerName);
			logger.info(headerName + ": " + headerValue);
		};
		
		headerNames.forEach(consum);
		
		
		// Delegar al nuevo filtro de la cadena
		Mono<Void> ret = chain.filter(exchange);
		
		return ret;
	}

	/**
	 * Orden de ejecucion del pre-filtro: 0-1-2-3-4
	 * Se ejecuta el primero: 0
	 */
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
