package com.gymlab.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lgpd")
@CrossOrigin(origins = {
    "http://localhost:4200", 
    "https://gymlab-front.vercel.app"
})
public class LgpdController {

    @PostMapping("/consentimento")
    public ResponseEntity<Map<String, Object>> registrarConsentimento(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        
        System.out.println("Recebendo consentimento de: " + request.getRemoteAddr());

        Boolean consentido = (Boolean) body.get("consentido");
        String versaoTermo = (String) body.get("versaoTermo");
        String userAgent = request.getHeader("User-Agent");
        String ipOrigem = request.getHeader("X-Forwarded-For"); 
        
        if (ipOrigem == null || ipOrigem.isEmpty()) {
            ipOrigem = request.getRemoteAddr();
        }

        Map<String, Object> log = new HashMap<>();
        log.put("consentido", consentido != null ? consentido : false);
        log.put("versaoTermo", versaoTermo != null ? versaoTermo : "1.0");
        log.put("ip", mascararIp(ipOrigem));
        log.put("userAgent", userAgent);
        log.put("registradoEm", Instant.now());

        System.out.println("LGPD LOG -> " + log);

        return ResponseEntity.ok(log);
    }

    private String mascararIp(String ip) {
        if (ip == null || ip.equals("unknown")) return "***";
        // Lidar com IP via Proxy (X-Forwarded-For pode vir com vários IPs)
        String ipReal = ip.contains(",") ? ip.split(",")[0].trim() : ip;
        
        if (ipReal.contains(".")) {
            String[] partes = ipReal.split("\\.");
            if (partes.length >= 2) {
                return partes[0] + "." + partes[1] + ".***.***";
            }
        }
        return "***";
    }
}