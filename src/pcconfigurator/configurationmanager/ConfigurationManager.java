package pcconfigurator.configurationmanager;

import java.util.Set;

public interface ConfigurationManager {

    /**
     *
     * @param Configuration
     */
    void createConfiguration(Configuration configuration);

    /**
     *
     * @param Long
     * @return
     */
    public Configuration getConfigurationById(Long id);

    Set<Configuration> findAllConfigurations();

    /**
     *
     * @param Configuration
     */
    void updateConfiguration(Configuration configuration);

    /**
     *
     * @param Configuration
     */
    void deleteConfiguration(Configuration configuration);

    /**
     *
     * @param String
     */
    Set<Configuration> findConfigurationByName(String name);

}
