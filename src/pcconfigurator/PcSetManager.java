package pcconfigurator;

import java.util.List;

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
    List<Configuration> findConfigByComponent(Component component);

    /**
     *
     * @param Configuration
     */
    List<Component> listCompsInConfiguration(Configuration configuration);

}
