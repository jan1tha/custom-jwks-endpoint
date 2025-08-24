package com.wso2.ob.webapp.utility.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JWKSUtil {

    private static Logger log = LoggerFactory.getLogger(JWKSUtil.class);
    private static final String CARBON_HOME_NAME = "carbon.home";
    private static final String JWKS_FILE_NAME = "cert-list.json";
    private static final Path CONF_FILE_LOCATION = Paths.get("repository", "conf", "finance",
            JWKS_FILE_NAME);
    private static final String GET = "GET";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final Path CONFIG_FILE_LOCATION = Paths.get("repository", "conf", "finance",
            CONFIG_FILE_NAME);
    private static final Map<String, String> propertyCache = new ConcurrentHashMap<>();

    public static JSONArray getOBbieJwksEndpointDetails() {
    JSONArray keys = null;
    try {
        String endpoint = getPropertyFromFile("DCR_JWKS_REG_ENDPOINT");
        log.info("DCR_JWKS_REG_ENDPOINT: " + endpoint);
        if (StringUtils.isEmpty(endpoint)) {
            log.error("DCR_JWKS_REG_ENDPOINT property is null.");
            return null;
        }
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(GET);

        int status = con.getResponseCode();
        StringBuffer content = new StringBuffer();

        if (HttpsURLConnection.HTTP_OK == status) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("JWKS Endpoint output: " + content);
            }
            JSONObject object = new JSONObject(content.toString());
            if (object.has("keys")) {
                keys = object.getJSONArray("keys");
            }
        } else {
            log.error("Failed to fetch JWKS. HTTP status: " + status);
        }
    } catch (IOException e) {
        log.error("I/O error while connecting to JWKS endpoint", e);
        return null;
    } catch (Exception e) {
        log.error("Unexpected error occurred", e);
    }
    return keys;
}

    public static JSONArray getBankCerts() {
        String carbonHome = System.getProperty(CARBON_HOME_NAME);
        JSONArray certList = new JSONArray();
        try {
            String certString = "";
            Path filePath = Paths.get(carbonHome, CONF_FILE_LOCATION.toString());
            File myObj = new File(String.valueOf(filePath));
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                certString += myReader.nextLine();
            }
            myReader.close();
            return new JSONArray(certString);
        } catch (FileNotFoundException e) {
            log.error("Error trying to get bank certs from file");
            e.printStackTrace();
        }
        return certList;
    }

    private static String getPropertyFromFile(String key) {
        // Return cached value if it exists
        if (propertyCache.containsKey(key)) {
            return propertyCache.get(key);
        }

        // Get the carbon home directory
        String carbonHome = System.getProperty(CARBON_HOME_NAME);
        if (carbonHome == null) {
            log.error("Carbon home property is not set.");
            return null;
        }

        // Construct the full file path using Path.of()
        Path configFilePath = Paths.get(carbonHome, CONFIG_FILE_LOCATION.toString());

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(configFilePath.toFile())) {
            properties.load(input);
            String value = properties.getProperty(key);
            if (value != null) {
                propertyCache.put(key, value);
            }
            return value;
        } catch (IOException e) {
            log.error("Error reading property file: " + configFilePath, e);
        }
        return null;
    }

}