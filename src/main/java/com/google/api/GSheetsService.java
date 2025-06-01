package com.google.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.List;

public class GSheetsService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String APPLICATION_NAME = "Java App";

    private final AuthService authService;

    public GSheetsService(AuthService authService) {
        this.authService = authService;
    }

    public Sheets createSheetsClient() {
        Credential credential = authService.getCredentials();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<List<Object>> getValues(String spreadsheetId, String range) {
        Sheets sheetsClient = createSheetsClient();
        try {
            ValueRange response = sheetsClient.spreadsheets().values().get(spreadsheetId, range).execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                throw new RuntimeException("No values returned for spreadsheet " + spreadsheetId);
            } else {
                return values;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
