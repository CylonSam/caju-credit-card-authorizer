package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.service.AuthorizationTransactionService;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static io.cylonsam.cajucreditcardauthorizer.infra.controller.RequestValidator.validate;

@RestController
public class CreditCardAuthorizerController {
    private final AuthorizationTransactionService authorizationTransactionService;

    public CreditCardAuthorizerController(final AuthorizationTransactionService authorizationTransactionService) {
        this.authorizationTransactionService = authorizationTransactionService;
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationResponseDTO> authorize(@RequestBody final AuthorizationRequestDTO authorizationRequestDTO) {
        validate(authorizationRequestDTO);
        authorizationTransactionService.process(authorizationRequestDTO.toAuthorizationRequest());
        return new ResponseEntity<>(new AuthorizationResponseDTO("00"), HttpStatus.OK);
    }
}
