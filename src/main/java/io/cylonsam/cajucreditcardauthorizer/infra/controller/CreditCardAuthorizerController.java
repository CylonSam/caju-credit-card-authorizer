package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.service.AuthorizationRequestService;
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
    private final AuthorizationRequestService authorizationRequestService;

    public CreditCardAuthorizerController(final AuthorizationRequestService authorizationRequestService) {
        this.authorizationRequestService = authorizationRequestService;
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationResponseDTO> authorize(@RequestBody final AuthorizationRequestDTO authorizationRequestDTO) {
        validate(authorizationRequestDTO);
        authorizationRequestService.process(authorizationRequestDTO.toAuthorizationRequest());
        return new ResponseEntity<>(new AuthorizationResponseDTO("00"), HttpStatus.OK);
    }
}
