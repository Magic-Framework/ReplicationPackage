package magic.config;

import static dev.langchain4j.internal.Utils.getOrDefault;

public class ApiKeys {

    // TODO: replace with your own API keys
    public static final String SILICON_API_KEY = getOrDefault(System.getenv("SILICON_API_KEY"), "sk-");
}
