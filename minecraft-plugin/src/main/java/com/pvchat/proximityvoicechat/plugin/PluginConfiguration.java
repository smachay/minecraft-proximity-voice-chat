package com.pvchat.proximityvoicechat.plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

/**
 * <pre>
 * Used for loading, storing and saving plugin configuration.
 * If *.properties file doesn't contain the value
 * Adding new configuration field (for instance Integer someConfig):
 *  1.add "private static final Integer defaultSomeConfig = [default value of someConfig];" to fields
 *  2.add "private static Integer someConfig;" to fields
 *  3.add "properties.setProperty("someConfig", String.valueOf(defaultSomeConfig));" to {@code getDefaultProperties()}
 *  4.add "someConfig = Integer.valueOf(properties.getProperty("someConfig")); to {@code link()}
 *  5.add default getter and setter
 *  6.add "properties.setProperty("someConfig", [setter argument converted to string])"
 *  </pre>
 */
public class PluginConfiguration {
    //defaults
    private static final Integer defaultMaxHearDistance = 100; // key: maxHearDistance
    private static final Integer defaultNoAttenuationDistance = 10; // key: noAttenuationDistance
    private static final Double defaultLinearAttenuationFactor = 0.9; // key: linearAttenuationFactor
    private static final String defaultConfigPath = System.getProperty("user.dir" + File.separator + "config.properties");

    private static Integer maxHearDistance;
    private static Integer noAttenuationDistance;
    private static Double linearAttenuationFactor;

    private static String configPath;
    private static Properties properties;


    //example usage
    public static void main(String[] args) {
        try {
            PluginConfiguration.saveDefaultConfig(System.getProperty("user.dir") + File.separator + "plugin.properties");
            PluginConfiguration.load(System.getProperty("user.dir") + File.separator + "plugin.properties");
            System.out.println(PluginConfiguration.maxHearDistance);
            System.out.println(PluginConfiguration.noAttenuationDistance);
            System.out.println(PluginConfiguration.linearAttenuationFactor);

            PluginConfiguration.setMaxHearDistance(124);
            PluginConfiguration.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return {@link java.util.Properties} containing default properties based on {@link PluginConfiguration} fields with names starting from "default".
     */
    private static Properties getDefaultProperties(){
        Properties properties = new Properties();
        properties.setProperty("maxHearDistance", String.valueOf(defaultMaxHearDistance));
        properties.setProperty("noAttenuationDistance", String.valueOf(defaultNoAttenuationDistance));
        properties.setProperty("linearAttenuationFactor", String.valueOf(defaultLinearAttenuationFactor));
        return properties;
    }

    /**
     * Loads given config file to PluginConfiguration fields.
     *
     * @param configFilePath absolute path to config file
     * @throws InvalidPathException when given path isn't parseable.
     * @throws NoSuchFileException  when config file is not found.
     * @throws IOException          when I/O error ocurred while reading config file.
     */
    public static void load(String configFilePath) throws InvalidPathException, NoSuchFileException, IOException {
        Path cfgPath = Paths.get(configFilePath);
        if (Files.exists(cfgPath)) {
            configPath = configFilePath;
            InputStream configInputStream = Files.newInputStream(cfgPath, StandardOpenOption.READ);
            properties = new Properties(getDefaultProperties());
            properties.load(configInputStream);
            maxHearDistance = Integer.valueOf(properties.getProperty("maxHearDistance"));
            noAttenuationDistance = Integer.valueOf(properties.getProperty("noAttenuationDistance"));
            linearAttenuationFactor = Double.parseDouble(properties.getProperty("linearAttenuationFactor"));
        } else throw new NoSuchFileException(configFilePath);
    }

    /**
     * Saves PluginConfiguration fields to path given in load.
     */
    public static void save() throws IOException, InvalidPathException {
        save(configPath);
    }

    /**
     * Saves PluginConfiguration fields to given path.
     */
    public static void save(String configFilePath) throws IOException, InvalidPathException {
        Path cfgPath = Paths.get(configFilePath);
        BufferedWriter configWriter = Files.newBufferedWriter(cfgPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        properties.store(configWriter,"Proximity voice chat properties.");
    }

    /**
     * Saves default config to path given in load.
     */
    public static void saveDefaultConfig() throws IOException, InvalidPathException {
        if(configPath!=null) {
            saveDefaultConfig(configPath);
        }
    }

    /**
     * Saves default config to given path.
     */
    public static void saveDefaultConfig(String configFilePath) throws IOException, InvalidPathException {
        configPath = configFilePath;
        Path configPath = Paths.get(configFilePath);
        Properties properties = getDefaultProperties();
        BufferedWriter configWriter = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        properties.store(configWriter,"Proximity voice chat properties.");
    }

    public static Integer getMaxHearDistance() {
        return maxHearDistance;
    }

    public static void setMaxHearDistance(Integer maxHearDist) {
        if (maxHearDist > 0) {
            properties.setProperty("maxHearDistance", maxHearDist.toString());
        }
    }

    public static Integer getNoAttenuationDistance() {
        return noAttenuationDistance;
    }

    public static void setNoAttenuationDistance(Integer noAttenuationDist) {
        if (noAttenuationDist >= 0 && noAttenuationDist < maxHearDistance) {
            noAttenuationDistance = noAttenuationDist;
            properties.setProperty("noAttenuationDistance", noAttenuationDist.toString());
        }
    }

    public static Double getLinearAttenuationFactor() {
        return linearAttenuationFactor;
    }

    public static void setLinearAttenuationFactor(Double factor) {
        if (factor > 0.0 && factor < 1.0) {
            linearAttenuationFactor = factor;
            properties.setProperty("linearAttenuationFactor", factor.toString());
        }
    }
}
