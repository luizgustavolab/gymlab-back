package com.gymlab.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lgpd")
@CrossOrigin(origins = "http://localhost:4200")
public class LgpdController {

    @PostMapping("/consentimento")
    public ResponseEntity<Map<String, Object>> registrarConsentimento(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {

        Boolean consentido = (Boolean) body.get("consentido");

        String versaoTermo = (String) body.get("versaoTermo");

        String userAgent = request.getHeader("User-Agent");

        String ipOrigem = request.getRemoteAddr();

        Map<String, Object> log = new HashMap<>();

        log.put("consentido", consentido);
        log.put("versaoTermo", versaoTermo);
        log.put("ip", mascararIp(ipOrigem));
        log.put("userAgent", userAgent);
        log.put("registradoEm", Instant.now());

        System.out.println("LGPD LOG -> " + log);

        return ResponseEntity.ok(log);
    }

    private String mascararIp(String ip) {

        if (ip == null) {
            return "unknown";
        }

        if (ip.contains(".")) {

            String[] partes = ip.split("\\.");

            if (partes.length == 4) {

                return partes[0] + "."
                        + partes[1] + ".***.***";
            }
        }

        return "***";
    }
}