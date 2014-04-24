package pcconfigurator.componentmanager;

public enum ComponentTypes {

	GPU("Grafická karta"),
	CPU("Procesor"),
	MOTHERBOARD("Základná doska"),
	NETWORK_CARD("Sieťová karta"),
	HARD_DRIVE("Pevný disk"),
	SOUNDCARD("Zvuková karta"),
	OPTICAL_DRIVE("Optická jednotka"),
	RAM("Operačná pamäť"),
	CASE("Skrinka"),
	POWER_SUPPLY("Elektrický zdroj");

        public String name;
        ComponentTypes(String name){
            this.name = name;
        }
        
        String getName() {
            return name;
        }
}