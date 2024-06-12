package com.example.invoicegenerator;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@SpringBootApplication
@EnableScheduling
@RestController
public class InvoiceGeneratorApplication {


    public static void main(String[] args) {
        SpringApplication.run(InvoiceGeneratorApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Value("${zoho.api.base.url}")
    private String baseUrl;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Value("${organization.id}")
    private String organizationId;

    @Value("${zoho.refresh.token}")
    private String refreshToken;

    @Scheduled(initialDelay = 5000, fixedRate = 3580000)
    public void generateAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        // Define the URL
        String url = "https://accounts.zoho.in/oauth/v2/token";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        // Create the request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", "http://localhost:8080/invoice");
        requestBody.add("grant_type", "refresh_token");

        // Combine headers and body into an HttpEntity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Check the response status and print the result
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Access Token Response: " + response.getBody());
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.getBody()));
                String access_token = jsonObject.getString("access_token");
                System.out.println("Access Token: " + access_token);

                // creating invoice
                String url2 = baseUrl + "/invoices?organization_id=" + organizationId+"&send=false&ignore_auto_number_generation=false";
                HttpHeaders headers1 = new HttpHeaders();
                headers1.add("Content-Type", "application/json");
                headers1.add("Authorization", "Zoho-oauthtoken " + access_token);


                // Create the request body
                JSONObject invoice = new JSONObject();
                invoice.put("customer_id", 1830655000000035003L);
//                invoice.put("invoice_number", 999);
                invoice.put("date", "2024-06-12");
                invoice.put("gst_treatment", "business_gst");
                invoice.put("place_of_supply", "MP");
                invoice.put("tax_exemption_id", 1830655000000035078L);
                invoice.put("gst_no", "22AAAAA0000A1Z5");



                // Create a JSONArray for the line items
                JSONArray lineItems = new JSONArray();

                // Create individual JSONObject instances for each line item
                JSONObject lineItem1 = new JSONObject();
                lineItem1.put("item_id", 1830655000000035048L);
                lineItem1.put("name", "Product 1");
                lineItem1.put("description", "Description of Product 1");
                lineItem1.put("rate", 100);
                lineItem1.put("quantity", 2);
                lineItem1.put("hsn_or_sac", 85258);

                // Add line items to the lineItems array
                lineItems.put(lineItem1);

                // Add the lineItems array to the main invoice JSONObject
                invoice.put("line_items", lineItems);

                System.out.println("invoice json object: " + invoice);

                // Combine headers and body into an HttpEntity
                HttpEntity<?> requestEntity1 = new HttpEntity<>(invoice.toString(), headers1);
                System.out.println("requestEntity1: " + requestEntity1);
                // Send the POST request
                ResponseEntity<String> response1 = restTemplate.exchange(url2, HttpMethod.POST, requestEntity1, String.class);
                System.out.println("Invoice Response: " + response1.getBody());

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error parsing JSON response: " + e.getMessage());
            }
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
    }

}

