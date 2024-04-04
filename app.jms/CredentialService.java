package com.examples.services;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CredentialService {
    public String GetCredentials(String key) throws IOException {
        try (var reader = new FileReader("src/main/resources/credentials.json")) {
            var credentials = (JSONObject)(new JSONParser().parse(reader));
            return credentials.get(key).toString();
        } catch (IOException | ParseException e) {
            throw new IOException(String.format("There is no valid 'credentials.json' file in the resource folder with the correct key '%s'. Please create one locally.", key));
        }
    }
}
