import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class WindaPanel extends JPanel {
    private static final float GRUBOSC_KRAWEDZI = 2f;
    public static final int X_SHIFT = 60;
    public static final int Y_SHIFT = 20;
    public static final int SZEROKOSC = 300;
    public static final int WYSOKOSC = 780;
    private static final float ROZMIAR_CZCIONKI = 20f;

    private static final int X_OFFSET_PIETRO_JEDNOCYFROWE = 25;
    private static final int X_OFFSET_PIETRO_DWUCYFROWE = 35;

    private static final int KABINA_MARGIN_X = 10;
    private static final int KABINA_MARGIN_Y = 5;
    private static final int KABINA_OFFSET_WEWNETRZNY = 15;

    private static final int PASAZER_HEIGHT = 45;
    private static final int PASAZER_WIDTH = 40;
    private static final int ODSTEP_PASAZEROW = 7;
    private static final int ODSTEP_PASAZEROW_NA_PIETRZE = 15;
    private static final int PADDING_PO_PASAZERACH_NA_PIETRZE = 20;

    private static final int SZEROKOSC_PANELU_WZYWANIA_CALOSC = 80;
    private static final int ODSTEP_PANELU_OD_SZYBU = 10;
    private static final int ROZMIAR_PRZYCISKU_WZYWANIA = 40;
    private static final int WYSOKOSC_STRZALKI = 18;
    private static final int SZEROKOSC_STRZALKI = 22;
    private static final int ODSTEP_MIEDZY_STRZALKAMI = 4;
    private static final int ODSTEP_STRZALEK_OD_PRZYCISKU = 5;

    private static final Color KOLOR_PRZYCISKU_PIETRO_NIEAKTYWNY = Color.LIGHT_GRAY;
    private static final Color KOLOR_PRZYCISKU_PIETRO_AKTYWNY_CEL = Color.ORANGE;
    private static final Color KOLOR_PRZYCISKU_PIETRO_MOZNA_WEZWAC = Color.GREEN.darker();
    private static final Color KOLOR_PRZYCISKU_OBRAMOWANIE = Color.BLACK;

    private final KontrolerWindy controller;

    public WindaPanel(KontrolerWindy controller) {
        this.controller = controller;
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!controller.czySymulacjaAktywna()) return;
                if (sprawdzKliknieciePasazera(e.getX(), e.getY())) return;
                sprawdzKliknieciePanelWzywania(e.getX(), e.getY());
            }
        });
    }

    private boolean sprawdzKliknieciePasazera(int clickX, int clickY) {
        if (!controller.moznaWysiadac) return false;
        List<Pasazer> pasazerowieWKabinie = controller.getPasazerowieWKabinie();
        if (pasazerowieWKabinie == null || pasazerowieWKabinie.isEmpty()) return false;

        int yKabiny = controller.getPozycjaYKabinyDlaRysowania();
        int wysokoscPojedynczegoPietra = WYSOKOSC / KontrolerWindy.ILOSC_PIETER_RAZEM;

        int startX = X_SHIFT + KABINA_MARGIN_X + KABINA_OFFSET_WEWNETRZNY;
        int dostepnaWysokoscKabiny = wysokoscPojedynczegoPietra - (2 * KABINA_MARGIN_Y);
        int startY = yKabiny + KABINA_MARGIN_Y + (dostepnaWysokoscKabiny - PASAZER_HEIGHT) / 2;

        for (int i = pasazerowieWKabinie.size() - 1; i >= 0; i--) {
            Pasazer pasazer = pasazerowieWKabinie.get(i);
            int pasazerX = startX + i * (PASAZER_WIDTH + ODSTEP_PASAZEROW);
            if (clickX >= pasazerX && clickX <= pasazerX + PASAZER_WIDTH &&
                    clickY >= startY && clickY <= startY + PASAZER_HEIGHT) {
                controller.wysiadaj(pasazer);
                repaint();
                return true;
            }
        }
        return false;
    }

    private void sprawdzKliknieciePanelWzywania(int clickX, int clickY) {
        int wysokoscPojedynczegoPietra = WYSOKOSC / KontrolerWindy.ILOSC_PIETER_RAZEM;
        for (int i = 0; i < KontrolerWindy.ILOSC_PIETER_RAZEM; i++) {
            int numerPietraLogiczny = (KontrolerWindy.ILOSC_PIETER_RAZEM - 1) - i;
            int yPietra = Y_SHIFT + i * wysokoscPojedynczegoPietra;
            int xPrzycisk = X_SHIFT + SZEROKOSC + ODSTEP_PANELU_OD_SZYBU;
            int yPrzyciskPanelu = yPietra + (wysokoscPojedynczegoPietra - ROZMIAR_PRZYCISKU_WZYWANIA) / 2;

            if (clickX >= xPrzycisk && clickX <= xPrzycisk + ROZMIAR_PRZYCISKU_WZYWANIA &&
                    clickY >= yPrzyciskPanelu && clickY <= yPrzyciskPanelu + ROZMIAR_PRZYCISKU_WZYWANIA) {
                if (!controller.getPasazerowieNaPietrze(numerPietraLogiczny).isEmpty() &&
                        !controller.czyPietroJestAktywnymCelem(numerPietraLogiczny)) {
                    controller.zglosWezwanie(numerPietraLogiczny, ZrodloWezwania.PRZYCISK_WEZWANIA);
                    repaint();
                }
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(GRUBOSC_KRAWEDZI));
            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(ROZMIAR_CZCIONKI));

            int wysokoscPojedynczegoPietra = WYSOKOSC / KontrolerWindy.ILOSC_PIETER_RAZEM;
            for (int i = 0; i < KontrolerWindy.ILOSC_PIETER_RAZEM; i++) {
                int yPietra = Y_SHIFT + i * wysokoscPojedynczegoPietra;
                g2.drawRect(X_SHIFT, yPietra, SZEROKOSC, wysokoscPojedynczegoPietra);
                int numerPietraLogiczny = (KontrolerWindy.ILOSC_PIETER_RAZEM - 1) - i;
                String etykietaPietra = String.valueOf(numerPietraLogiczny);
                FontMetrics fm = g2.getFontMetrics();
                int yTekstu = yPietra + (wysokoscPojedynczegoPietra - fm.getHeight()) / 2 + fm.getAscent();
                int xTekstu = X_SHIFT - ((numerPietraLogiczny >= 10) ? X_OFFSET_PIETRO_DWUCYFROWE : X_OFFSET_PIETRO_JEDNOCYFROWE);
                g2.drawString(etykietaPietra, xTekstu, yTekstu);

                if (controller.czySymulacjaAktywna()) {
                    rysujPasazerowNaPietrze(g2, numerPietraLogiczny, yPietra, wysokoscPojedynczegoPietra);
                }
                rysujPanelWzywania(g2, numerPietraLogiczny, yPietra, wysokoscPojedynczegoPietra);
            }

            int yKabiny = controller.getPozycjaYKabinyDlaRysowania();
            g2.setColor(new Color(0xAA783C));
            g2.fillRect(X_SHIFT + KABINA_MARGIN_X, yKabiny + KABINA_MARGIN_Y,
                    SZEROKOSC - (2 * KABINA_MARGIN_X), wysokoscPojedynczegoPietra - (2 * KABINA_MARGIN_Y));
            if (controller.czySymulacjaAktywna()) {
                rysujPasazerowWKabinie(g2, yKabiny, wysokoscPojedynczegoPietra);
            }
        } finally {
            g2.dispose();
        }
    }

    private void rysujPanelWzywania(Graphics2D g2, int numerPietraLogiczny, int yPietra, int wysokoscPietra) {
        int xPrzycisk = X_SHIFT + SZEROKOSC + ODSTEP_PANELU_OD_SZYBU;
        int yPrzyciskPanelu = yPietra + (wysokoscPietra - ROZMIAR_PRZYCISKU_WZYWANIA) / 2;
        Color kolorPrzycisku = KOLOR_PRZYCISKU_PIETRO_NIEAKTYWNY;

        if (controller.czySymulacjaAktywna()) {
            if (controller.czyPietroJestAktywnymCelem(numerPietraLogiczny)) {
                kolorPrzycisku = KOLOR_PRZYCISKU_PIETRO_AKTYWNY_CEL;
            } else if (!controller.getPasazerowieNaPietrze(numerPietraLogiczny).isEmpty()) {
                kolorPrzycisku = KOLOR_PRZYCISKU_PIETRO_MOZNA_WEZWAC;
            }
        }
        g2.setColor(kolorPrzycisku);
        g2.fillOval(xPrzycisk, yPrzyciskPanelu, ROZMIAR_PRZYCISKU_WZYWANIA, ROZMIAR_PRZYCISKU_WZYWANIA);
        g2.setColor(KOLOR_PRZYCISKU_OBRAMOWANIE);
        g2.drawOval(xPrzycisk, yPrzyciskPanelu, ROZMIAR_PRZYCISKU_WZYWANIA, ROZMIAR_PRZYCISKU_WZYWANIA);

        int xStrzalek = xPrzycisk + ROZMIAR_PRZYCISKU_WZYWANIA + ODSTEP_STRZALEK_OD_PRZYCISKU;
        int yCentrumPrzycisk = yPrzyciskPanelu + ROZMIAR_PRZYCISKU_WZYWANIA / 2;
        int yStrzalkaGora = yCentrumPrzycisk - WYSOKOSC_STRZALKI - ODSTEP_MIEDZY_STRZALKAMI / 2;
        int yStrzalkaDol = yCentrumPrzycisk + ODSTEP_MIEDZY_STRZALKAMI / 2;

        if (numerPietraLogiczny < KontrolerWindy.ILOSC_PIETER_RAZEM - 1) {
            rysujStrzalke(g2, xStrzalek, yStrzalkaGora, KierunekJazdy.GORA);
        }
        if (numerPietraLogiczny > 0) {
            rysujStrzalke(g2, xStrzalek, yStrzalkaDol, KierunekJazdy.DOL);
        }
    }

    private void rysujStrzalke(Graphics2D g2, int x, int y, KierunekJazdy typStrzalki) {
        int[] xPoints = new int[]{x, x + SZEROKOSC_STRZALKI / 2, x + SZEROKOSC_STRZALKI};
        int[] yPoints = (typStrzalki == KierunekJazdy.GORA) ?
                new int[]{y + WYSOKOSC_STRZALKI, y, y + WYSOKOSC_STRZALKI} :
                new int[]{y, y + WYSOKOSC_STRZALKI, y};

        KierunekJazdy aktualnyKierunekWindy = controller.getKierunekJazdy();
        Color kolorStrzalki = Color.LIGHT_GRAY;
        if (controller.czySymulacjaAktywna() && aktualnyKierunekWindy == typStrzalki && aktualnyKierunekWindy != KierunekJazdy.STOP) {
            kolorStrzalki = (typStrzalki == KierunekJazdy.GORA ? Color.GREEN : Color.RED);
        }
        g2.setColor(kolorStrzalki);
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xPoints, yPoints, 3);
    }

    private void rysujPasazerowNaPietrze(Graphics2D g2, int numerPietraLogiczny, int yPietra, int wysokoscPietra) {
        List<Pasazer> pasazerowie = controller.getPasazerowieNaPietrze(numerPietraLogiczny);
        if (pasazerowie.isEmpty()) return;
        int startX = X_SHIFT + SZEROKOSC + ODSTEP_PANELU_OD_SZYBU + SZEROKOSC_PANELU_WZYWANIA_CALOSC + ODSTEP_PASAZEROW_NA_PIETRZE;
        int startY = yPietra + (wysokoscPietra - PASAZER_HEIGHT) / 2;
        for (int i = 0; i < pasazerowie.size(); i++) {
            Pasazer pasazer = pasazerowie.get(i);
            int pasazerX = startX + i * (PASAZER_WIDTH + ODSTEP_PASAZEROW);
            g2.setColor(pasazer.getKolor());
            g2.fillRect(pasazerX, startY, PASAZER_WIDTH, PASAZER_HEIGHT);
            g2.setColor(Color.BLACK);
            g2.drawRect(pasazerX, startY, PASAZER_WIDTH, PASAZER_HEIGHT);
        }
    }

    private void rysujPasazerowWKabinie(Graphics2D g2, int yKabiny, int wysokoscKabiny) {
        List<Pasazer> pasazerowieWKabinie = controller.getPasazerowieWKabinie();
        if (pasazerowieWKabinie.isEmpty()) return;

        int startX = X_SHIFT + KABINA_MARGIN_X + KABINA_OFFSET_WEWNETRZNY;
        int dostepnaWysokoscKabiny = wysokoscKabiny - (2 * KABINA_MARGIN_Y);
        int startY = yKabiny + KABINA_MARGIN_Y + (dostepnaWysokoscKabiny - PASAZER_HEIGHT) / 2;

        for (int i = 0; i < pasazerowieWKabinie.size(); i++) {
            Pasazer pasazer = pasazerowieWKabinie.get(i);
            int pasazerX = startX + i * (PASAZER_WIDTH + ODSTEP_PASAZEROW);
            g2.setColor(pasazer.getKolor());
            g2.fillRect(pasazerX, startY, PASAZER_WIDTH, PASAZER_HEIGHT);
            g2.setColor(Color.BLACK);
            g2.drawRect(pasazerX, startY, PASAZER_WIDTH, PASAZER_HEIGHT);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int szerokoscPasaazerowNaPietrze = KontrolerWindy.MAX_PASAZEROW_PIETRO * (PASAZER_WIDTH + ODSTEP_PASAZEROW) + PADDING_PO_PASAZERACH_NA_PIETRZE;
        int calkowitaSzerokosc = X_SHIFT + SZEROKOSC + ODSTEP_PANELU_OD_SZYBU + SZEROKOSC_PANELU_WZYWANIA_CALOSC + szerokoscPasaazerowNaPietrze + X_SHIFT;
        int calkowitaWysokosc = Y_SHIFT + WYSOKOSC + Y_SHIFT;
        return new Dimension(calkowitaSzerokosc, calkowitaWysokosc);
    }
}