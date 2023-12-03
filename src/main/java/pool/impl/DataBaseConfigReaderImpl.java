package pool.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pool.DataBaseConfigReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataBaseConfigReaderImpl implements DataBaseConfigReader {
    private final Logger LOGGER = LogManager.getLogger(DataBaseConfigReaderImpl.class);
    private Properties properties;

    public DataBaseConfigReaderImpl() {
        load();
    }

    private void load() {
        properties = new Properties();
        try (InputStream in = DataBaseConfigReaderImpl.class.getClassLoader().getResourceAsStream("databaseConfig.properties")) {
            properties.load(in);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public String get(String name) {
        return properties.getProperty(name);
    }
}
