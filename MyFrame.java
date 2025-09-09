import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {
    private final KontrolerWindy kontrolerWindy;
    private final JButton startButton;

    public MyFrame() {
        super("Winda");
        setLayout(new BorderLayout());

        kontrolerWindy = new KontrolerWindy(null, this);
        WindaPanel windaPanel = new WindaPanel(kontrolerWindy);
        kontrolerWindy.setWindaPanel(windaPanel);
        PanelPrzyciskow panelPrzyciskow = new PanelPrzyciskow(kontrolerWindy);

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(0xd99c82));
        leftPanel.setPreferredSize(new Dimension(360, 800));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        leftPanel.add(panelPrzyciskow, gbc);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(0xadd8e6));
        rightPanel.setPreferredSize(new Dimension(920, 800));
        rightPanel.add(windaPanel, BorderLayout.CENTER);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.CENTER);

        startButton = new JButton("START");
        startButton.setPreferredSize(new Dimension(getWidth(), 60));
        startButton.addActionListener(e -> {
            kontrolerWindy.rozpocznijSymulacje();
            startButton.setEnabled(false);
        });
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(startButton, BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void wlaczPrzyciskStart() {
        if (startButton != null) {
            startButton.setEnabled(true);
        }
    }
}