package pcconfigurator;

import java.math.BigDecimal;

public class Component {

    private long id;
    private String vendor;
    private BigDecimal price;
    private ComponentTypes type;
    private int power;
    private String name;

    public Component(long id, String vendor, BigDecimal price, int power, String name) {
        this.id = id;
        this.vendor = vendor;
        this.price = price;
        this.power = power;
        this.name = name;
    }

}
