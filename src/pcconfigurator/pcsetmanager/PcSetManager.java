package pcconfigurator.pcsetmanager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.configurationmanager.Configuration;

public interface PcSetManager {

    /**
     *
     * @param PcSet
     */
    void createPcSet(PcSet pcSet);

    /**
     *
     * @param Configuration
     * @param Component
     */
    PcSet getPcSet(Configuration configuration,Component component);

    /**
     *
     * @param PcSet
     */
    void updatePcSet(PcSet pcSet);

    /**
     *
     * @param PcSet
     */
    void deletePcSet(PcSet pcSet);

    /**
     *
     * @param Component
     */
    Set<Configuration> findConfigByComponent(Component component);

    /**
     *
     * @param Configuration
     */
    Map<Component,Integer> listCompsInConfiguration(Configuration configuration);

}
