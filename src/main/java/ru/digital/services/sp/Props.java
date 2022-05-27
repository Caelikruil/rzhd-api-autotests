package ru.digital.services.sp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Props {

    private static final Logger LOG = LoggerFactory.getLogger(Props.class);

    private static Props instance;
    private static Properties properties;

    /**
     * Creates single instance of Props class or returns already created
     *
     * @return instance of Props
     */
    private synchronized static Props getInstance() {
        if (instance == null) {
            instance = new Props();
        }
        return instance;
    }

    /**
     * Constructs Props object. It loads properties from filesystem path set in
     * the <b>BDDConfigFile</b> system properties or from classpath
     * config/application.properties by default.
     */
    private Props() { initProps(); }

    private static void initProps(){
        System.setProperty("logback.configurationFile", "config/logback.xml");

        String sConfigFile = getSystemProperty("BDDConfigFile", "application.properties");
        properties = getPropsFromResource(sConfigFile);

        Map<String,String> all = getAllProps();
        List<String> keys = all.keySet().stream().filter(k->k.toLowerCase().endsWith(".properties.file"))
                .collect(Collectors.toList());
        keys.forEach(k->{
            String s = getOrEnv(k);
            if (!s.isEmpty()) {
                Properties p = getPropsFromResource(s);
                p.forEach((key, value) -> properties.setProperty(key.toString(), value.toString()));
            }
        });
    }

    private static Map<String,String> getAllProps(){
        Map<String,String> map = new HashMap<>();
        properties.forEach((key, value) -> map.put(key.toString(), value.toString()));
        map.putAll(System.getenv());
        System.getProperties().forEach((key, value) -> map.put(key.toString(), value.toString()));
        return map;
    }

    private static Properties getPropsFromResource(String resource) {
        Properties properties = new Properties();
        //first try to load properties from resources
        InputStream in = Props.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            try (FileInputStream fin = new FileInputStream(resource)) {
                properties.load(fin);
            } catch (IOException e) {
                LOG.error(String.format("Failed to initialize properties: %s", e));
            }
        } else {
            try {
                properties.load(in);
            } catch (IOException e) {
                LOG.error(String.format("Failed to initialize properties: %s", e));
            }
        }
        return properties;
    }

    private static String getSystemProperty(String propName, String defVal){
        String val = System.getProperty(propName);
        if (null == val)
            val = System.getenv(propName.toUpperCase());
        if (null == val)
            val = System.getenv(propName);
        if (null == val)
            val = defVal;
        return val;
    }

    /**
     * Returns value of the property 'name' of empty string if the property is
     * not found
     *
     * @param name Name of the property to get value of
     * @return value of the property of empty string
     */
    private String getProp(String name) {
        String val = getProps().getProperty(name, "");
        if (val.isEmpty()) {
            LOG.debug("Property {} was not found in Props", name);
        }
        return val.trim();
    }

    /**
     * Get property from file
     *
     * @param prop property name.
     * @return property value.
     */
    public static String get(String prop) {
        return Props.getInstance().getProp(prop);
    }

    /**
     * Get property (from System properties, then from System Environment and then from properties)
     *
     * @param prop - Property name
     * @return property value (default is empty string if missing)
     */
    public static String getOrEnv(String prop) { return getOrEnv(prop, ""); }

    /**
     * Get property (from System properties, then from System Environment and then from properties)
     *
     * @param prop - Property name
     * @param defVal - default property value (if none is set)
     * @return property value
     */
    private static String getOrEnv(String prop, String defVal) {
        String val = System.getProperty(prop);
        if (null == val)
            val = System.getenv(prop.toUpperCase());
        if (null == val)
            val = System.getenv(prop);
        if (null == val) {
            if (properties == null)
                Props.getInstance();
            if (properties != null)
                val = properties.getProperty(prop);
        }
        if (null == val)
            val = defVal;
        return val;
    }

    /**
     * Get property from file
     *
     * @param prop property name.
     * @param defaultValue default value if not set
     * @return property value.
     */
    public static String get(String prop, String defaultValue) {
        String val = Props.getInstance().getProp(prop);
        if (val.isEmpty()) {
            return defaultValue;
        }
        return val;
    }

    /**
     * @return the properties
     */
    private static Properties getProps() {
        return properties;
    }
}
