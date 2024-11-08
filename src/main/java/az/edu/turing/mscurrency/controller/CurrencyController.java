package az.edu.turing.mscurrency.controller;

import az.edu.turing.mscurrency.dto.CurrencyDto;
import az.edu.turing.mscurrency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/currency")
@RequiredArgsConstructor
@RestController
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/euro")
    public ResponseEntity<CurrencyDto> getEuroCurrency() {
        return ResponseEntity.ok(currencyService.getCurrencyFromRedis("EUR"));
    }

    @GetMapping("/usd")
    public ResponseEntity<CurrencyDto> getUsdCurrency() {
        return ResponseEntity.ok(currencyService.getCurrencyFromRedis("USD"));
    }

    @GetMapping("/try")
    public ResponseEntity<CurrencyDto> getTlCurrency() {
        return ResponseEntity.ok(currencyService.getCurrencyFromRedis("TRY"));
    }
}