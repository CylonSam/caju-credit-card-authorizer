package io.cylonsam.cajucreditcardauthorizer.infra.controller;

import io.cylonsam.cajucreditcardauthorizer.core.exception.InsufficientBalanceException;
import io.cylonsam.cajucreditcardauthorizer.core.usecase.ProcessAuthorizationRequestUseCase;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationRequestDTO;
import io.cylonsam.cajucreditcardauthorizer.infra.controller.dto.AuthorizationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditCardAuthorizerController {
    private final ProcessAuthorizationRequestUseCase processAuthorizationRequestUseCase;

    public CreditCardAuthorizerController(ProcessAuthorizationRequestUseCase processAuthorizationRequestUseCase) {
        this.processAuthorizationRequestUseCase = processAuthorizationRequestUseCase;
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationResponseDTO> authorize(@RequestBody AuthorizationRequestDTO authorizationRequestDTO) {
        try {
            processAuthorizationRequestUseCase.process(authorizationRequestDTO.toAuthorizationRequest());
            return new ResponseEntity<>(new AuthorizationResponseDTO("00"), HttpStatus.OK);
        } catch (InsufficientBalanceException e) {
            return new ResponseEntity<>(new AuthorizationResponseDTO("51"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AuthorizationResponseDTO("07"), HttpStatus.OK);
        }
    }
}
