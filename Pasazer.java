import java.awt.Color;
import java.util.Objects;

public class Pasazer {
    private final Color kolor;

    public Pasazer(Color kolor) {
        this.kolor = kolor;
    }

    public Color getKolor() {
        return kolor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pasazer pasazer = (Pasazer) o;
        return Objects.equals(kolor, pasazer.kolor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kolor);
    }
}