package pcconfigurator.componentmanager;

public enum ComponentTypes {

	GPU("gpu"),
	CPU("cpu"),
	MOTHERBOARD("motherboard"),
	NETWORK_CARD("networkCard"),
	HARD_DRIVE("hardDrive"),
	SOUNDCARD("soundcard"),
	OPTICAL_DRIVE("opticalDrive"),
	RAM("ram"),
	CASE("case"),
	POWER_SUPPLY("powerSupply");

        private final String name;
        ComponentTypes(String name){
            this.name = name;
        }
        
        public String getName() {
            switch (name)
            {
                case "gpu":
                    return bundle.getString("gpu");
                case "cpu":
                    return bundle.getString("cpu");
                case "motherboard":
                    return bundle.getString("motherboard");
                case "networkCard":
                    return bundle.getString("networkCard");
                case "hardDrive":
                    return bundle.getString("hardDrive");
                case "soundcard":
                    return bundle.getString("soundcard");
                case "opticalDrive":
                    return bundle.getString("opticalDrive");
                case "ram":
                    return bundle.getString("ram");
                case "case":
                    return bundle.getString("case");
                case "powerSupply":
                    return bundle.getString("powerSupply");
                default:
                    return null;
            }
        }
        
        private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pcconfigurator/gui/Strings");
}