package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private Map<String, Map<String, String>> data;
    private List<String> countries;
    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        data = new HashMap<>();
        countries = new ArrayList<>();

        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject country = jsonArray.getJSONObject(i);
                String countryName = country.getString("alpha3");

                countries.add(countryName);
                Map<String, String> countryNamesTranslated = new HashMap<>();
                for (String key : country.keySet()) {
                    if ("id".equals(key) || "alpha2".equals(key) || "alpha3".equals(key)) {
                        continue; // Skip these keys
                    }
                    countryNamesTranslated.put(key, country.getString(key));
                }

                // Store the country's translations in the main data map
                data.put(countryName, countryNamesTranslated);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        // Ensure country codes match the case in your JSON file
        Map<String, String> languages = data.get(country.toLowerCase());
        if (languages != null) {
            return new ArrayList<>(languages.keySet());
        }
        return new ArrayList<>();
    }
    @Override
    public List<String> getCountries() {

        return new ArrayList<>(countries);
    }

    @Override
    public String translate(String country, String language) {
        Map<String, String> translations = data.get(country.toLowerCase());
        if (translations != null) {
            return translations.getOrDefault(language, "Translation not available");
        }
        return "Country not found";
    }
    }

