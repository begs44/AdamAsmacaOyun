package oyunproje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class OyunProje extends JFrame {
    static final String RESIMLER_YOLU    = "C:\\P2Oyun\\Resimler\\";
    static final String TXT_YOLU         = "C:\\P2Oyun\\TXTDosyalar\\";
    static final String SIFRE_DOSYA      = TXT_YOLU + "sifre.txt";
    static final String LOG_DOSYA        = TXT_YOLU + "log.txt";
    static final String OYUNLAR_DOSYA    = TXT_YOLU + "oyunlar.txt";
    static final String KELIMELER_DOSYA  = TXT_YOLU + "kelimeler.txt";

    String[] kelimeListesi;
    String secilenKelime = "";
    boolean[] acikHarfler;
    int yanlisSayisi = 0;
    int saniye = 0;

    JLabel[] harfEtiketi;

    // Skor tablosu modeli (initComponents sonrası doldurulur)
    private DefaultTableModel skorModel;
    private DefaultTableModel logModel;

    public OyunProje() {
        initComponents();
        setLocationRelativeTo(null);

        // Skor tablosu model kurulumu
        String[] skorKolonlar = {"Sonuç", "Süre", "Tarih & Saat"};
        skorModel = new DefaultTableModel(skorKolonlar, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        skorTablo.setModel(skorModel);
        skorTablosunuDoldur(skorModel);

        // Log tablosu model kurulumu
        String[] logKolonlar = {"Olay", "Tarih & Saat"};
        logModel = new DefaultTableModel(logKolonlar, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        logTablo.setModel(logModel);
        logTabloDoldur(logModel);

        resimGoster(1);
    }

    // =========================================================
    // NetBeans tarafından yönetilen alan — elle düzenlemeyin
    // =========================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Non-visual
        oyunTimer = new javax.swing.Timer(1000, null);

        // Menü
        menuBar      = new JMenuBar();
        oyunMenu     = new JMenu("Oyun");
        baslaItem    = new JMenuItem("Oyuna Başla");
        yenidenItem  = new JMenuItem("Oyunu Yeniden Başlat");
        menuSep      = new JSeparator();
        cikisItem    = new JMenuItem("Oyundan Çıkış");

        baslaItem.addActionListener(e -> baslaItemActionPerformed(e));
        yenidenItem.addActionListener(e -> yenidenItemActionPerformed(e));
        cikisItem.addActionListener(e -> cikisItemActionPerformed(e));

        oyunMenu.add(baslaItem);
        oyunMenu.add(yenidenItem);
        oyunMenu.add(menuSep);
        oyunMenu.add(cikisItem);
        menuBar.add(oyunMenu);
        setJMenuBar(menuBar);

        // TabbedPane
        tabbedPane = new JTabbedPane();

        // ---- TAB 0: Oyun ----
        oyunPanel = new JPanel(new BorderLayout(10, 10));
        oyunPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        resimLabel = new JLabel();
        resimLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resimLabel.setPreferredSize(new Dimension(200, 200));
        oyunPanel.add(resimLabel, BorderLayout.NORTH);

        ortaPanel  = new JPanel();
        ortaPanel.setLayout(new BoxLayout(ortaPanel, BoxLayout.Y_AXIS));
        harfSatiri = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        harfSatiri.setName("harfSatiri");
        ortaPanel.add(Box.createVerticalGlue());
        ortaPanel.add(harfSatiri);
        ortaPanel.add(Box.createVerticalGlue());
        oyunPanel.add(ortaPanel, BorderLayout.CENTER);

        altPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        harfTahminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        harfTahminPanel.add(new JLabel("Harf Tahmini:"));
        harfField = new JTextField(3);
        harfTahminPanel.add(harfField);
        harfBtn = new JButton("Tahmin Et");
        harfTahminPanel.add(harfBtn);
        yanlisSayisiLabel = new JLabel("Yanlış: 0 / 11");
        harfTahminPanel.add(yanlisSayisiLabel);

        kelimeTahminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        kelimeTahminPanel.add(new JLabel("Kelime Tahmini:"));
        kelimeField = new JTextField(15);
        kelimeTahminPanel.add(kelimeField);
        kelimeBtn = new JButton("Tahmin Et");
        kelimeTahminPanel.add(kelimeBtn);

        surePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sureLabel = new JLabel("Süre: 0 saniye");
        sureLabel.setFont(new Font("Arial", Font.BOLD, 14));
        surePanel.add(sureLabel);

        altPanel.add(harfTahminPanel);
        altPanel.add(kelimeTahminPanel);
        altPanel.add(surePanel);
        oyunPanel.add(altPanel, BorderLayout.SOUTH);

        harfBtn.addActionListener(e -> harfBtnActionPerformed(e));
        harfField.addActionListener(e -> harfFieldActionPerformed(e));
        kelimeBtn.addActionListener(e -> kelimeBtnActionPerformed(e));
        kelimeField.addActionListener(e -> kelimeFieldActionPerformed(e));

        tabbedPane.addTab("Oyun", oyunPanel);

        // ---- TAB 1: Eski Skorlar ----
        skorPanel = new JPanel(new BorderLayout(10, 10));
        skorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        skorTablo      = new JTable();
        skorTablo.setRowHeight(25);
        skorScrollPane = new JScrollPane(skorTablo);
        skorPanel.add(skorScrollPane, BorderLayout.CENTER);

        skorYenileBtn  = new JButton("Yenile");
        skorTemizleBtn = new JButton("Temizle");
        skorYenileBtn.addActionListener(e -> skorYenileBtnActionPerformed(e));
        skorTemizleBtn.addActionListener(e -> skorTemizleBtnActionPerformed(e));

        skorAltPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        skorAltPanel.add(skorYenileBtn);
        skorAltPanel.add(skorTemizleBtn);
        skorPanel.add(skorAltPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Eski Skorlar", skorPanel);

        // ---- TAB 2: Loglar ----
        logPanel = new JPanel(new BorderLayout(10, 10));
        logPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        logTablo      = new JTable();
        logTablo.setRowHeight(25);
        logScrollPane = new JScrollPane(logTablo);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        logYenileBtn  = new JButton("Yenile");
        logTemizleBtn = new JButton("Temizle");
        logYenileBtn.addActionListener(e -> logYenileBtnActionPerformed(e));
        logTemizleBtn.addActionListener(e -> logTemizleBtnActionPerformed(e));

        logAltPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logAltPanel.add(logYenileBtn);
        logAltPanel.add(logTemizleBtn);
        logPanel.add(logAltPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Loglar", logPanel);

        // Frame ayarları
        setTitle("Adam Asmaca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        pack();
    }// </editor-fold>

    // =========================================================
    // Event handler metotları
    // =========================================================
    private void baslaItemActionPerformed(ActionEvent e)    { oyunuBaslat(); }
    private void yenidenItemActionPerformed(ActionEvent e)  { oyunuBaslat(); }
    private void cikisItemActionPerformed(ActionEvent e)    { System.exit(0); }
    private void harfBtnActionPerformed(ActionEvent e)      { harfTahminYap(); }
    private void harfFieldActionPerformed(ActionEvent e)    { harfTahminYap(); }
    private void kelimeBtnActionPerformed(ActionEvent e)    { kelimeTahminYap(); }
    private void kelimeFieldActionPerformed(ActionEvent e)  { kelimeTahminYap(); }

    private void skorYenileBtnActionPerformed(ActionEvent e) {
        skorModel.setRowCount(0);
        skorTablosunuDoldur(skorModel);
    }

    private void skorTemizleBtnActionPerformed(ActionEvent e) {
        if (sifreDogrula()) {
            try (FileWriter fw = new FileWriter(OYUNLAR_DOSYA, false)) {
                fw.write("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Dosya temizlenemedi!");
            }
            skorModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Skorlar temizlendi.");
        }
    }

    private void logYenileBtnActionPerformed(ActionEvent e) {
        logModel.setRowCount(0);
        logTabloDoldur(logModel);
    }

    private void logTemizleBtnActionPerformed(ActionEvent e) {
        if (sifreDogrula()) {
            try (FileWriter fw = new FileWriter(LOG_DOSYA, false)) {
                fw.write("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Dosya temizlenemedi!");
            }
            logModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Loglar başarıyla temizlendi.");
        }
    }

    // =========================================================
    // Oyun mantığı — orijinal koddan aynen alındı
    // =========================================================
    void resimGoster(int no) {
        String yol = RESIMLER_YOLU + no + ".jpg";
        ImageIcon icon = new ImageIcon(yol);
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        resimLabel.setIcon(new ImageIcon(img));
    }

    void kelimeleriYukle() {
        try (BufferedReader br = new BufferedReader(new FileReader(KELIMELER_DOSYA))) {
            ArrayList<String> liste = new ArrayList<>();
            String satir;
            while ((satir = br.readLine()) != null) {
                if (!satir.trim().isEmpty()) liste.add(satir.trim().toUpperCase());
            }
            kelimeListesi = liste.toArray(new String[0]);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "kelimeler.txt okunamadı!");
            kelimeListesi = new String[]{"BILGISAYAR"};
        }
    }

    void oyunuBaslat() {
        kelimeleriYukle();
        Random rnd = new Random();
        secilenKelime = kelimeListesi[rnd.nextInt(kelimeListesi.length)];
        acikHarfler   = new boolean[secilenKelime.length()];
        yanlisSayisi  = 0;
        saniye        = 0;
        yanlisSayisiLabel.setText("Yanlış: 0 / 11");

        harfSatiri.removeAll();
        harfEtiketi = new JLabel[secilenKelime.length()];
        for (int i = 0; i < secilenKelime.length(); i++) {
            harfEtiketi[i] = new JLabel("*");
            harfEtiketi[i].setFont(new Font("Arial", Font.BOLD, 24));
            harfEtiketi[i].setPreferredSize(new Dimension(30, 40));
            harfEtiketi[i].setHorizontalAlignment(SwingConstants.CENTER);
            harfEtiketi[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            harfSatiri.add(harfEtiketi[i]);
        }
        harfSatiri.revalidate();
        harfSatiri.repaint();

        resimGoster(1);
        harfField.setText("");
        kelimeField.setText("");
        harfField.setEnabled(true);
        kelimeField.setEnabled(true);
        sureLabel.setText("Süre: 0 saniye");

        if (oyunTimer != null) oyunTimer.stop();
        oyunTimer = new javax.swing.Timer(1000, e -> {
            saniye++;
            sureLabel.setText("Süre: " + saniye + " saniye");
        });
        oyunTimer.start();

        tabbedPane.setSelectedIndex(0);
    }

    void harfTahminYap() {
        if (secilenKelime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Önce oyunu başlatın!");
            return;
        }
        String girdi = harfField.getText().trim().toUpperCase();
        harfField.setText("");
        if (girdi.isEmpty() || girdi.length() != 1) {
            JOptionPane.showMessageDialog(this, "Lütfen tek bir harf girin!");
            return;
        }
        char harf = girdi.charAt(0);
        boolean bulundu = false;
        for (int i = 0; i < secilenKelime.length(); i++) {
            if (secilenKelime.charAt(i) == harf && !acikHarfler[i]) {
                acikHarfler[i] = true;
                harfEtiketi[i].setText(String.valueOf(harf));
                bulundu = true;
            }
        }
        if (!bulundu) yanlisTahmin();
        kazandiMiKontrol();
    }

    void kelimeTahminYap() {
        if (secilenKelime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Önce oyunu başlatın!");
            return;
        }
        String girdi = kelimeField.getText().trim().toUpperCase();
        kelimeField.setText("");
        if (girdi.equals(secilenKelime)) {
            for (int i = 0; i < secilenKelime.length(); i++) {
                acikHarfler[i] = true;
                harfEtiketi[i].setText(String.valueOf(secilenKelime.charAt(i)));
            }
            kazandiMiKontrol();
        } else {
            yanlisTahmin();
        }
    }

    void yanlisTahmin() {
        yanlisSayisi++;
        yanlisSayisiLabel.setText("Yanlış: " + yanlisSayisi + " / 11");
        resimGoster(yanlisSayisi + 1);
        if (yanlisSayisi >= 11) oyunuBitir(false);
    }

    void kazandiMiKontrol() {
        for (boolean a : acikHarfler) {
            if (!a) return;
        }
        oyunuBitir(true);
    }

    void oyunuBitir(boolean kazandi) {
        if (oyunTimer != null) oyunTimer.stop();
        harfField.setEnabled(false);
        kelimeField.setEnabled(false);

        String sonuc = kazandi ? "KAZANDI" : "KAYBETTİ";
        String mesaj = kazandi
            ? "Tebrikler! Kelimeyi buldunuz: " + secilenKelime
            : "Kaybettiniz! Doğru Kelime: " + secilenKelime;

        try (FileWriter fw = new FileWriter(OYUNLAR_DOSYA, true)) {
            fw.write(sonuc + " | " + saniye + " saniye | " + simdi() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Oyun kaydedilemedi!");
        }

        JOptionPane.showMessageDialog(this, mesaj);
    }

    void skorTablosunuDoldur(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(OYUNLAR_DOSYA))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty()) continue;
                String[] parcalar = satir.split("\\|");
                if (parcalar.length >= 3) {
                    model.addRow(new Object[]{
                        parcalar[0].trim(),
                        parcalar[1].trim(),
                        parcalar[2].trim()
                    });
                }
            }
        } catch (IOException e) {
            // dosya henüz yoksa sessizce geç
        }
    }

    void logTabloDoldur(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_DOSYA))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty()) continue;
                String[] parcalar = satir.split("\\|");
                if (parcalar.length >= 2) {
                    model.addRow(new Object[]{
                        parcalar[0].trim(),
                        parcalar[1].trim()
                    });
                }
            }
        } catch (IOException e) {
            // sessizce geç
        }
    }

    static String simdi() {
        return LocalDateTime.now()
               .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    static void logKaydet(String etiket) {
        try (FileWriter fw = new FileWriter(LOG_DOSYA, true)) {
            fw.write(etiket + " | " + simdi() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Log yazılamadı: " + e.getMessage());
        }
    }

    static String sifreyiOku() {
        try (BufferedReader br = new BufferedReader(new FileReader(SIFRE_DOSYA))) {
            String satir = br.readLine();
            return (satir != null) ? satir.trim() : "";
        } catch (IOException e) {
            return "";
        }
    }

    static void sifreyiKaydet(String sifre) {
        try (FileWriter fw = new FileWriter(SIFRE_DOSYA, false)) {
            fw.write(sifre);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Şifre kaydedilemedi: " + e.getMessage());
        }
    }

    boolean sifreDogrula() {
        String girilenSifre = JOptionPane.showInputDialog(this,
            "Şifreyi girin:", "Doğrulama", JOptionPane.PLAIN_MESSAGE);
        if (girilenSifre == null) return false;
        if (girilenSifre.trim().equals(sifreyiOku())) {
            return true;
        }
        JOptionPane.showMessageDialog(this, "Hatalı şifre girişi!");
        return false;
    }

    static boolean sifreKontrol() {
        String mevcutSifre = sifreyiOku();

        if (mevcutSifre.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "İlk kez giriş yapıyorsunuz. Lütfen bir şifre belirleyin.");

            String yeniSifre = null;
            while (yeniSifre == null || yeniSifre.trim().isEmpty()) {
                yeniSifre = JOptionPane.showInputDialog(null,
                    "Yeni şifrenizi girin:", "Şifre Oluştur",
                    JOptionPane.PLAIN_MESSAGE);
                if (yeniSifre == null) {
                    System.exit(0);
                }
            }
            sifreyiKaydet(yeniSifre.trim());
            logKaydet("YENİ ŞİFRE OLUŞTURULDU");
            JOptionPane.showMessageDialog(null, "Şifre kaydedildi! Oyuna hoş geldiniz.");
            return true;
        }

        int hak = 3;
        while (hak > 0) {
            String girilen = JOptionPane.showInputDialog(null,
                "Şifrenizi girin (" + hak + " hakkınız kaldı):",
                "Giriş", JOptionPane.PLAIN_MESSAGE);

            if (girilen == null) {
                System.exit(0);
            }

            logKaydet("ŞİFRE DENEMESİ");

            if (girilen.trim().equals(mevcutSifre)) {
                logKaydet("BAŞARILI GİRİŞ");
                return true;
            } else {
                hak--;
                if (hak > 0) {
                    JOptionPane.showMessageDialog(null,
                        "Hatalı şifre! " + hak + " hakkınız kaldı.");
                }
            }
        }

        logKaydet("3 Hatalı Giriş Yapıldı - Program Sonlandı");
        JOptionPane.showMessageDialog(null,
            "3 kez hatalı girdiniz. Program sonlanıyor.");
        return false;
    }

    public static void main(String[] args) {
        if (!sifreKontrol()) {
            System.exit(0);
        }

        SwingUtilities.invokeLater(() -> {
            OyunProje pencere = new OyunProje();
            pencere.setVisible(true);
        });
    }

    // =========================================================
    // NetBeans tarafından yönetilen değişkenler (declare)
    // =========================================================
    private JMenuItem  baslaItem;
    private JMenuItem  yenidenItem;
    private JSeparator menuSep;
    private JMenuItem  cikisItem;
    private JMenuBar   menuBar;
    private JMenu      oyunMenu;

    private JTabbedPane tabbedPane;

    // Oyun sekmesi
    private JPanel     oyunPanel;
    private JLabel     resimLabel;
    private JPanel     ortaPanel;
    private JPanel     harfSatiri;
    private JPanel     altPanel;
    private JPanel     harfTahminPanel;
    private JTextField harfField;
    private JButton    harfBtn;
    private JLabel     yanlisSayisiLabel;
    private JPanel     kelimeTahminPanel;
    private JTextField kelimeField;
    private JButton    kelimeBtn;
    private JPanel     surePanel;
    private JLabel     sureLabel;

    // Skor sekmesi
    private JPanel      skorPanel;
    private JScrollPane skorScrollPane;
    private JTable      skorTablo;
    private JPanel      skorAltPanel;
    private JButton     skorYenileBtn;
    private JButton     skorTemizleBtn;

    // Log sekmesi
    private JPanel      logPanel;
    private JScrollPane logScrollPane;
    private JTable      logTablo;
    private JPanel      logAltPanel;
    private JButton     logYenileBtn;
    private JButton     logTemizleBtn;

    private javax.swing.Timer oyunTimer;
}
