package io.opensaber.registry.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Definition {
    private static Logger logger = LoggerFactory.getLogger(Definition.class);
    private final static String TITLE = "title";
    private final static String OSCONFIG = "_osconfig";

    private String schema;
    private String title;
    private List<String> privateFields = new ArrayList<>();
    private List<String> signedFields = new ArrayList<>();

    public Definition(JSONObject schema) throws IOException {
        this.schema = schema.toString();
        try {
            title = schema.getString(TITLE);
            ObjectMapper mapper = new ObjectMapper();

            JSONObject configJson = schema.getJSONObject(OSCONFIG);
            OSSchemaConfiguration configProperties = mapper.readValue(configJson.toString(), OSSchemaConfiguration.class);

            privateFields = configProperties.getPrivateFields();
            signedFields = configProperties.getSignedFields();
        } catch (JSONException | JsonParseException | JsonMappingException e) {
            logger.error("while parsing schema defination  error: title or _osconfig key not found for " + schema);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return schema;
    }

    public List<String> getSignedFields() {
        return signedFields;
    }

    public List<String> getPrivateFields() {
        return privateFields;
    }

    public boolean isEncrypted(String fieldName) {
        if (fieldName != null) {
            return fieldName.substring(0, Math.min(fieldName.length(), 9)).equalsIgnoreCase("encrypted");
        } else
            return false;
    }

    public boolean isPrivate(String fieldName) {
        return privateFields.contains(fieldName);
    }

}