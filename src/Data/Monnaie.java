package Data;

/**
 * Created by cyril rocca Gr 2227 INPRES .
 */
public class Monnaie {

    String name;
    String value;


    public Monnaie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Monnaie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
