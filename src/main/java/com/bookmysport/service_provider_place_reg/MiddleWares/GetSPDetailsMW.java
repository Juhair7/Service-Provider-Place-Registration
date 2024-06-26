package com.bookmysport.service_provider_place_reg.MiddleWares;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bookmysport.service_provider_place_reg.Models.ResponseMessage;

import reactor.core.publisher.Mono;

@Service
public class GetSPDetailsMW {

    @Autowired
    private WebClient webClient;

    @Value("${AUTH_SERVICE_URL}")
    String authServiceUrl;

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<ResponseMessage> getSPDetailsByToken(String token, String role) {
        try {
            Mono<Map<String, Object>> userDetailsMono = webClient.get()
                    .uri(authServiceUrl)
                    .headers(headers -> {
                        headers.set("Content-Type", "application/json");
                        headers.set("token", token);
                        headers.set("role", role);
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> userDetails = userDetailsMono.block();
            if (userDetails != null) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage(userDetails.get("id").toString());
                return ResponseEntity.ok().body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("No user exists");
                return ResponseEntity.ok().body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
