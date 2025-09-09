import javax.swing.Timer;
import java.awt.Color;
import java.util.*;

public class KontrolerWindy {
    public static final int ILOSC_PIETER_RAZEM = 11;
    public static final int MAX_PASAZEROW_PIETRO = 5;
    private static final int MAX_PASAZEROW_KABINA = 5;
    private static final int CZAS_RUCHU_PIETRO = 500;
    private static final int CZAS_POSTOJU_OPERACJE = 5000;
    private static final int CZAS_ANIMACJI_KLATKI = 16;
    private static final int CZAS_OCZEKIWANIA_NA_KONIEC = 10000;
    private Timer timerKoncaSymulacji;
    private final MyFrame parentFrame;

    private final Queue<Integer> kolejkaWezwan = new LinkedList<>();
    private KierunekJazdy kierunekJazdy = KierunekJazdy.STOP;
    private int aktualnePietro = 0;
    private Integer docelowePietroAktualne = null;
    private double pozycjaYKabinyRzeczywista;
    private double docelowaPozycjaYKabinyRzeczywista;
    private double predkoscPikseliNaKlatke;
    private final Timer timerRuchuWindy;
    private final List<List<Pasazer>> pasazerowieNaPietrach;
    private final List<Pasazer> pasazerowieWKabinie;
    private boolean symulacjaAktywna = false;
    public boolean moznaWysiadac = false;
    private WindaPanel windaPanel;


    public KontrolerWindy(WindaPanel windaPanel, MyFrame parentFrame) {
        this.windaPanel = windaPanel;
        this.parentFrame = parentFrame;
        this.pasazerowieNaPietrach = new ArrayList<>();
        this.pasazerowieWKabinie = new ArrayList<>();
        for (int i = 0; i < ILOSC_PIETER_RAZEM; i++) {
            this.pasazerowieNaPietrach.add(new ArrayList<>());
        }
        obliczParametryRuchu();
        this.pozycjaYKabinyRzeczywista = obliczRzeczywistaYPietra(0);
        this.docelowaPozycjaYKabinyRzeczywista = this.pozycjaYKabinyRzeczywista;

        this.timerRuchuWindy = new Timer(CZAS_ANIMACJI_KLATKI, e -> {
            if (docelowePietroAktualne == null || kierunekJazdy == KierunekJazdy.STOP) {
                ((Timer) e.getSource()).stop();
                if (this.windaPanel != null) this.windaPanel.repaint();
                return;
            }

            boolean osiagnietoCelFizyczny = false;
            if (kierunekJazdy == KierunekJazdy.GORA) {
                pozycjaYKabinyRzeczywista -= predkoscPikseliNaKlatke;
                if (pozycjaYKabinyRzeczywista <= docelowaPozycjaYKabinyRzeczywista) {
                    pozycjaYKabinyRzeczywista = docelowaPozycjaYKabinyRzeczywista;
                    osiagnietoCelFizyczny = true;
                }
            } else if (kierunekJazdy == KierunekJazdy.DOL) {
                pozycjaYKabinyRzeczywista += predkoscPikseliNaKlatke;
                if (pozycjaYKabinyRzeczywista >= docelowaPozycjaYKabinyRzeczywista) {
                    pozycjaYKabinyRzeczywista = docelowaPozycjaYKabinyRzeczywista;
                    osiagnietoCelFizyczny = true;
                }
            }

            if (osiagnietoCelFizyczny) {
                ((Timer) e.getSource()).stop();
                aktualnePietro = docelowePietroAktualne;
                obsluzAktualnePietro();
            } else {
                if (this.windaPanel != null) this.windaPanel.repaint();
            }
        });
    }

    private void sprawdzWarunkiKoncaSymulacji() {
        if (!symulacjaAktywna) return;

        boolean kabinaNieJedzie = !timerRuchuWindy.isRunning() && kierunekJazdy == KierunekJazdy.STOP;
        boolean brakPasazerowWKabinie = pasazerowieWKabinie.isEmpty();
        boolean brakWezwan = kolejkaWezwan.isEmpty() && docelowePietroAktualne == null;

        if (kabinaNieJedzie && brakPasazerowWKabinie && brakWezwan) {
            if (timerKoncaSymulacji == null || !timerKoncaSymulacji.isRunning()) {
                timerKoncaSymulacji = new Timer(CZAS_OCZEKIWANIA_NA_KONIEC, e -> {
                    ((Timer) e.getSource()).stop();
                    zakonczSymulacje();
                });
                timerKoncaSymulacji.setRepeats(false);
                timerKoncaSymulacji.start();
            }
        } else {
            if (timerKoncaSymulacji != null && timerKoncaSymulacji.isRunning()) {
                timerKoncaSymulacji.stop();
            }
        }
    }

    private void zakonczSymulacje() {
        symulacjaAktywna = false;
        kierunekJazdy = KierunekJazdy.STOP;
        moznaWysiadac = false;

        if (parentFrame != null) {
            parentFrame.wlaczPrzyciskStart();
        }

        if (windaPanel != null) {
            windaPanel.repaint();
        }
    }

    private void obliczParametryRuchu() {
        if (this.windaPanel == null) return;
        int wysokoscSzybuPanel = WindaPanel.WYSOKOSC;
        int wysokoscPojedynczegoPietraPanel = wysokoscSzybuPanel / ILOSC_PIETER_RAZEM;
        double klatkiNaPrzejazdPietra = (double) CZAS_RUCHU_PIETRO / CZAS_ANIMACJI_KLATKI;
        this.predkoscPikseliNaKlatke = wysokoscPojedynczegoPietraPanel / klatkiNaPrzejazdPietra;
    }

    private double obliczRzeczywistaYPietra(int numerPietraLogiczny) {
        if (this.windaPanel == null) return 0;
        int wysokoscSzybuPanel = WindaPanel.WYSOKOSC;
        int yShiftPanel = WindaPanel.Y_SHIFT;
        int wysokoscPojedynczegoPietraPanel = wysokoscSzybuPanel / ILOSC_PIETER_RAZEM;
        return yShiftPanel + ((double) (ILOSC_PIETER_RAZEM - 1) - numerPietraLogiczny) * wysokoscPojedynczegoPietraPanel;
    }

    private void obsluzAktualnePietro() {
        kolejkaWezwan.remove(aktualnePietro);
        moznaWysiadac = true;
        if (windaPanel != null) windaPanel.repaint();

        Timer timerOperacjiNaPietrze = new Timer(CZAS_POSTOJU_OPERACJE, operacjeEvent -> {
            ((Timer) operacjeEvent.getSource()).stop();
            moznaWysiadac = false;
            List<Pasazer> pasazerowieDoWsiadania = new ArrayList<>(pasazerowieNaPietrach.get(aktualnePietro));
            for (Pasazer p : pasazerowieDoWsiadania) {
                if (pasazerowieWKabinie.size() < MAX_PASAZEROW_KABINA) {
                    wsiadaj(p);
                } else {
                    break;
                }
            }
            uruchomNastepnyCelZKolejki();
            sprawdzWarunkiKoncaSymulacji();
        });
        timerOperacjiNaPietrze.setRepeats(false);
        timerOperacjiNaPietrze.start();
    }

    public void rozpocznijSymulacje() {
        if (timerRuchuWindy.isRunning()) timerRuchuWindy.stop();
        aktualnePietro = 0;
        docelowePietroAktualne = null;
        this.pozycjaYKabinyRzeczywista = obliczRzeczywistaYPietra(0);
        this.docelowaPozycjaYKabinyRzeczywista = this.pozycjaYKabinyRzeczywista;
        kierunekJazdy = KierunekJazdy.STOP;
        pasazerowieWKabinie.clear();
        kolejkaWezwan.clear();
        for (List<Pasazer> lista : pasazerowieNaPietrach) lista.clear();
        generujPasazerow();
        symulacjaAktywna = true;
        if (windaPanel != null) windaPanel.repaint();
    }

    private void generujPasazerow() {
        Random random = new Random();
        for (int pietro = 1; pietro < ILOSC_PIETER_RAZEM; pietro++) {
            int liczbaPasazerow = random.nextInt(MAX_PASAZEROW_PIETRO + 1);
            for (int i = 0; i < liczbaPasazerow; i++) {
                Color kolorPasazera = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                pasazerowieNaPietrach.get(pietro).add(new Pasazer(kolorPasazera));
            }
        }
    }

    public void zglosWezwanie(int pietroZgloszenia, ZrodloWezwania zrodlo) {
        if (!symulacjaAktywna || (zrodlo == ZrodloWezwania.PANEL_PRZYCISKOW && pasazerowieWKabinie.isEmpty())) {
            return;
        }

        if (zrodlo == ZrodloWezwania.PANEL_PRZYCISKOW && pietroZgloszenia == this.aktualnePietro) {
            return;
        }

        boolean czyNoweWezwanieDodano = false;
        boolean czyWezwanieJestJuzWSystemie = kolejkaWezwan.contains(pietroZgloszenia) || Objects.equals(pietroZgloszenia, docelowePietroAktualne);
        boolean czyWindaStoiBezczynnieNaMiejscu = !timerRuchuWindy.isRunning() && docelowePietroAktualne == null && aktualnePietro == pietroZgloszenia;

        if (!czyWezwanieJestJuzWSystemie && !czyWindaStoiBezczynnieNaMiejscu) {
            kolejkaWezwan.offer(pietroZgloszenia);
            czyNoweWezwanieDodano = true;
        }

        // sprawdzam czy wezwanie trzeba zmienic
        boolean celZmienionyWLocie = false;
        if (timerRuchuWindy.isRunning() && docelowePietroAktualne != null) {
            if (kierunekJazdy == KierunekJazdy.GORA && pietroZgloszenia > aktualnePietro && pietroZgloszenia < docelowePietroAktualne) {
                docelowePietroAktualne = pietroZgloszenia;
                docelowaPozycjaYKabinyRzeczywista = obliczRzeczywistaYPietra(pietroZgloszenia);
                celZmienionyWLocie = true;
            } else if (kierunekJazdy == KierunekJazdy.DOL && pietroZgloszenia < aktualnePietro && pietroZgloszenia > docelowePietroAktualne) {
                docelowePietroAktualne = pietroZgloszenia;
                docelowaPozycjaYKabinyRzeczywista = obliczRzeczywistaYPietra(pietroZgloszenia);
                celZmienionyWLocie = true;
            }
        } else if (docelowePietroAktualne == null) {
            if (czyNoweWezwanieDodano) {
                uruchomNastepnyCelZKolejki();
                return;
            }
        }

        sprawdzWarunkiKoncaSymulacji();
        if ((czyNoweWezwanieDodano || celZmienionyWLocie) && windaPanel != null) {
            windaPanel.repaint();
        }
    }

    private void uruchomNastepnyCelZKolejki() {
        if (timerRuchuWindy.isRunning()) {
            return;
        }

        Integer nastepnyLogicznyCel = wybierzNastepnyCel();

        if (nastepnyLogicznyCel != null) {
            docelowePietroAktualne = nastepnyLogicznyCel;
            docelowaPozycjaYKabinyRzeczywista = obliczRzeczywistaYPietra(docelowePietroAktualne);
            kierunekJazdy = (pozycjaYKabinyRzeczywista > docelowaPozycjaYKabinyRzeczywista) ? KierunekJazdy.GORA : KierunekJazdy.DOL;
            timerRuchuWindy.start();
        } else {
            kierunekJazdy = KierunekJazdy.STOP;
            docelowePietroAktualne = null;
            if (windaPanel != null) {
                windaPanel.repaint();
            }
        }
    }

    private Integer wybierzNastepnyCel() {
        if (kolejkaWezwan.isEmpty()) return null;

        Integer celWKierunku = null;
        int minOdlegloscWKierunku = Integer.MAX_VALUE;
        Integer celNajblizszyOgolnie = null;
        int minOdlegloscOgolnie = Integer.MAX_VALUE;

        KierunekJazdy rozwazanyKierunek = this.kierunekJazdy;
        if (this.kierunekJazdy == KierunekJazdy.STOP && this.docelowePietroAktualne != null) {
            rozwazanyKierunek = (this.docelowePietroAktualne > this.aktualnePietro) ? KierunekJazdy.GORA : KierunekJazdy.DOL;
        }

        if (rozwazanyKierunek == KierunekJazdy.GORA) {
            for (Integer pietro : kolejkaWezwan) {
                if (pietro > aktualnePietro) {
                    int odleglosc = pietro - aktualnePietro;
                    if (odleglosc < minOdlegloscWKierunku) {
                        minOdlegloscWKierunku = odleglosc;
                        celWKierunku = pietro;
                    }
                }
            }
        } else if (rozwazanyKierunek == KierunekJazdy.DOL) {
            for (Integer pietro : kolejkaWezwan) {
                if (pietro < aktualnePietro) {
                    int odleglosc = aktualnePietro - pietro;
                    if (odleglosc < minOdlegloscWKierunku) {
                        minOdlegloscWKierunku = odleglosc;
                        celWKierunku = pietro;
                    }
                }
            }
        }

        if (celWKierunku != null) return celWKierunku;

        for (Integer pietro : kolejkaWezwan) {
            int odleglosc = Math.abs(pietro - aktualnePietro);
            if (odleglosc < minOdlegloscOgolnie) {
                minOdlegloscOgolnie = odleglosc;
                celNajblizszyOgolnie = pietro;
            }
        }
        return celNajblizszyOgolnie;
    }

    public void wysiadaj(Pasazer pasazer) {
        if (moznaWysiadac) {
            pasazerowieWKabinie.remove(pasazer);
            if (windaPanel != null) windaPanel.repaint();
            sprawdzWarunkiKoncaSymulacji();
        }
    }

    public void wsiadaj(Pasazer pasazer) {
        pasazerowieNaPietrach.get(aktualnePietro).remove(pasazer);
        pasazerowieWKabinie.add(pasazer);
    }

    public void setWindaPanel(WindaPanel panel) {
        this.windaPanel = panel;
        obliczParametryRuchu();
        this.pozycjaYKabinyRzeczywista = obliczRzeczywistaYPietra(this.aktualnePietro);
        this.docelowaPozycjaYKabinyRzeczywista = this.pozycjaYKabinyRzeczywista;
    }

    public List<Pasazer> getPasazerowieNaPietrze(int numerPietra) {
        if (numerPietra < 0 || numerPietra >= pasazerowieNaPietrach.size()) return Collections.emptyList();
        return pasazerowieNaPietrach.get(numerPietra);
    }

    public List<Pasazer> getPasazerowieWKabinie() {
        return pasazerowieWKabinie;
    }

    public boolean czySymulacjaAktywna() {
        return symulacjaAktywna;
    }

    public KierunekJazdy getKierunekJazdy() {
        return kierunekJazdy;
    }

    public boolean czyPietroJestAktywnymCelem(int pietro) {
        return Objects.equals(pietro, docelowePietroAktualne) || kolejkaWezwan.contains(pietro);
    }

    public int getPozycjaYKabinyDlaRysowania() {
        return (int) Math.round(pozycjaYKabinyRzeczywista);
    }
}