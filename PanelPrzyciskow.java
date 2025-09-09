import javax.swing.*;
import java.awt.*;

public class PanelPrzyciskow extends JPanel {
    private static final float FONT_SIZE = 20f;

    public PanelPrzyciskow(KontrolerWindy controller) {
        setPreferredSize(new Dimension(240, 500));
        setLayout(new GridLayout(4, 3, 5, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setBackground(new Color(0xc7e099));

        Font buttonFont = getFont().deriveFont(FONT_SIZE);
        for (int i = 0; i <= KontrolerWindy.ILOSC_PIETER_RAZEM -1 ; i++) {
            Przycisk button = new Przycisk(String.valueOf(i));
            button.setFont(buttonFont);
            final int floorNumber = i;
            button.addActionListener(e -> {
                if (controller.czySymulacjaAktywna()) {
                    controller.zglosWezwanie(floorNumber, ZrodloWezwania.PANEL_PRZYCISKOW);
                }
            });
            add(button);
        }
    }
}