package ms_publicacion.com.petone.publication.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;



@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;
    
    @Value("${supabase.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {

        String fileName =
                UUID.randomUUID() + "_" + file.getOriginalFilename();

        String uploadUrl =
                supabaseUrl
                        + "/storage/v1/object/"
                        + bucket
                        + "/"
                        + fileName;

        HttpHeaders headers = new HttpHeaders();

        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        headers.setContentType(
                MediaType.parseMediaType(file.getContentType())
        );

        HttpEntity<byte[]> entity =
                new HttpEntity<>(file.getBytes(), headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.exchange(
                        uploadUrl,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error subiendo imagen");
        }

        return supabaseUrl
                + "/storage/v1/object/public/"
                + bucket
                + "/"
                + fileName;
    }
}
