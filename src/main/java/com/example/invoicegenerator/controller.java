package com.example.invoicegenerator;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//
//import okhttp3.*;
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.view.RedirectView;
//
//import java.io.IOException;
//
@RestController
@Log4j2
public class controller {

    @GetMapping("/invoice")
    public ResponseEntity<?> invoice(@RequestParam String code,
                                         @RequestParam String location,
                                         @RequestParam("accounts-server") String server) {
        log.info("redirect called code is {} ::: {} ::: {}", code , location, server);
        return new ResponseEntity<>("redirect:" + location + "?code=" + code + "&accounts-server=" + server, HttpStatus.OK);
    }


}

