package pcconfigurator.pcsetmanager;

import java.util.Map;
import java.util.Set;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.configurationmanager.Configuration;

public interface PcSetManager {

    void createPcSet(PcSet pcSet);

    PcSet getPcSet(Configuration configuration,Component component);

    void updatePcSet(PcSet pcSet);

    void deletePcSet(PcSet pcSet);

    Set<Configuration> findConfigByComponent(Component component);

    Map<Component,Integer> listCompsInConfiguration(Configuration configuration);
}
