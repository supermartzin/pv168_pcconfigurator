package pcconfigurator.configurationmanager;

import java.util.Set;

public interface ConfigurationManager {

    void createConfiguration(Configuration configuration);

    Configuration getConfigurationById(Long id);

    Set<Configuration> findAllConfigurations();

    void updateConfiguration(Configuration configuration);

    void deleteConfiguration(Configuration configuration);

    Set<Configuration> findConfigurationByName(String name);

}
