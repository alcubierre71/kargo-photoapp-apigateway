package com.appsdevblog.photoapp.api.gateway;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.core.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * 
 * En esta clase se van a ejecutar una serie de filtros de validacion que se van a controlar en el Gateway 
 * antes de reenviar la peticion al microservicio correspondiente:
 * Intercepts incoming HTTP requests.
 * Checks for an Authorization header.
 * Validates a JWT token inside that header.
 * Blocks the request if the token is missing or invalid.
 * 
 * 
 * when you define a route with filters in the app.properties
 * ----------------------------------------------------------
 * The Spring framework does the following at runtime:
 * It calls your custom filter’s apply(Config config) method.
 * This method returns a GatewayFilter — which is a functional interface.
 * When an HTTP request comes in, Spring Cloud Gateway invokes:
 * gatewayFilter.filter(exchange, chain)
 * where:
 * exchange is the current HTTP request and response wrapper: ServerWebExchange.
 * chain is the object used to forward the request to the next filter: GatewayFilterChain.
 * 
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	@Autowired
	Environment env;
	
	public AuthorizationHeaderFilter() {
		super(Config.class);
	}
	
	
	public static class Config {
		// Put configuration properties here
	}
 
	/**
	 * Este metodo se ejecuta automaticamente al procesar los filtros
	 */
	@Override
	public GatewayFilter apply(Config config) {
		// TODO Auto-generated method stub
		
		// Los tipos de 'exchange' y 'chain' son inferidos a partir del tipo devuelto por la funcion Lambda
		// exchange --> HTTP request
		// chain --> contenedor del request junto con el siguiente filtro de la cadena GatewayFilterChain
		GatewayFilter pLambda = (ServerWebExchange exchange, GatewayFilterChain chain) -> {
			
			// Recuperamos la peticion HTTP
			ServerHttpRequest request = exchange.getRequest();
			
			// Devolvemos error si la peticion no contiene un Header "Authorization"
			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			// Extraemos el JWT Token almacenado en el header Authorization
			String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwtToken = authHeader.replace("Bearer ", "").trim();
			
			// Se valida el token JWT
			if (!isJwtValid(jwtToken)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}
			
			// Si todo es correcto, se continua y se invoca al siguiente filtro de la cadena
			return chain.filter(exchange);
		};
		
		return pLambda;
		
	}

	/**
	 * OnError
	 * @param exchange
	 * @param string
	 * @param unauthorized
	 * @return
	 */
	private Mono<Void> onError(ServerWebExchange exchange, String string, HttpStatus unauthorized) {
		// TODO Auto-generated method stub
		
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(unauthorized);
		
		Mono<Void> ret = response.setComplete();
		
		return ret;
	}

	/**
	 * Validacion del token JWT :
	 * Parse the token using a secret key.
	 * Extract the subject (typically user ID or email).
	 * Return false if the token is invalid or empty.
	 * @param jwt
	 * @return
	 */
	private boolean isJwtValid(String jwt) {
		
		boolean returnValue = true;
		
		String subject = null;
		
		// Extraemos la clave almacenada en el token JWT 
		String tokenSecret = env.getProperty("token.secret");
		byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        // Control de excepcion de existencia de usuario asociado al token
		try {

			// Extramos el subject del token JWT (ID del usuario)
	        Claims claims = parser.parseSignedClaims(jwt).getPayload();      
	        subject = (String) claims.get("sub");

		} catch (Exception ex) {
			returnValue = false;
		}

		if (subject == null || subject.isEmpty()) {
			returnValue = false;
		}
		
		return returnValue;
		
	}
	
}
