package pcconfigurator.componentmanager;

import java.math.BigDecimal;
import java.util.Objects;

public class Component {

    private Long id;
    private String vendor;
    private BigDecimal price;
    private ComponentTypes type;
    private int power;
    private String name;

    public Component(String vendor, BigDecimal price, ComponentTypes type, int power, String name) {
        this.vendor = vendor;
        this.price = price;
        this.type = type;
        this.power = power;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ComponentTypes getType() {
        return type;
    }

    public void setType(ComponentTypes type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }   

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Component other = (Component) obj;
        return Objects.equals(this.id, other.id);
    }
}
