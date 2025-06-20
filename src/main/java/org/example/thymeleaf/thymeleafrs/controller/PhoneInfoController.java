package org.example.thymeleaf.thymeleafrs.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PhoneInfoController {

    private static final Map<String, Map<String, Object>> phoneDatabase = new HashMap<>();
    static {
        Map<String, Object> info1 = new HashMap<>();
        info1.put("name", "Contoh Nama");
        info1.put("tags", new String[]{"Spam", "Penawaran Kredit"});
        info1.put("source", "Dummy Database");
        phoneDatabase.put("+6289525700414", info1);

        Map<String, Object> info2 = new HashMap<>();
        info2.put("name", "Pak Budi");
        info2.put("tags", new String[]{"Teman", "Kontak Kerja"});
        info2.put("source", "Dummy Database");
        phoneDatabase.put("+6281234567890", info2);
    }

    @GetMapping("/phoneinfo")
    public Map<String, Object> getPhoneInfo(@RequestParam String phone) {
        if (phoneDatabase.containsKey(phone)) {
            Map<String, Object> result = new HashMap<>(phoneDatabase.get(phone));
            result.put("phone", phone);
            return result;
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Nomor tidak ditemukan di database.");
            return error;
        }
    }
}
