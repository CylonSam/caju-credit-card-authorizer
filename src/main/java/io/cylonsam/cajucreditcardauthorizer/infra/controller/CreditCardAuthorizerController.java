package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.service.ProcessAuthorizationRequestService;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditCardAuthorizerController {
    private final ProcessAuthorizationRequestService processAuthorizationRequestService;

    public CreditCardAuthorizerController(ProcessAuthorizationRequestService processAuthorizationRequestService) {
        this.processAuthorizationRequestService = processAuthorizationRequestService;
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationResponseDTO> authorize(@RequestBody AuthorizationRequestDTO authorizationRequestDTO) {
        processAuthorizationRequestService.process(authorizationRequestDTO.toAuthorizationRequest());
        return new ResponseEntity<>(new AuthorizationResponseDTO("00"), HttpStatus.OK);
    }
}
