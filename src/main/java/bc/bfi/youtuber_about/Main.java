/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.bfi.youtuber_about;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Swing UI to run {@link ScrapeService} similar to the web interface.
 */
public class Main extends javax.swing.JFrame {

    private final ScrapeService service;
    private volatile boolean stopRequested;

    javax.swing.JTextField gridHostField;
    javax.swing.JTextArea queriesArea;
    javax.swing.JProgressBar progressBar;
    javax.swing.JTextArea progressLog;
    javax.swing.JButton startBtn;
    javax.swing.JButton stopBtn;

    public Main() {
        this(new ScrapeService());
    }

    Main(ScrapeService service) {
        this.service = service;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        javax.swing.JLabel hostLabel = new javax.swing.JLabel();
        javax.swing.JLabel queriesLabel = new javax.swing.JLabel();
        javax.swing.JScrollPane queriesScroll = new javax.swing.JScrollPane();
        javax.swing.JScrollPane logScroll = new javax.swing.JScrollPane();

        gridHostField = new javax.swing.JTextField();
        queriesArea = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        progressLog = new javax.swing.JTextArea();
        startBtn = new javax.swing.JButton();
        stopBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Youtube About scraper");
        setResizable(false);

        hostLabel.setText("Selenium Grid Host:");

        queriesLabel.setText("Channel names:");

        queriesArea.setColumns(20);
        queriesArea.setRows(5);
        queriesScroll.setViewportView(queriesArea);

        progressLog.setColumns(20);
        progressLog.setRows(5);
        progressLog.setEditable(false);
        logScroll.setViewportView(progressLog);

        startBtn.setText("Start");
        startBtn.addActionListener((ActionEvent evt) -> startBtnActionPerformed(evt));

        stopBtn.setText("Stop");
        stopBtn.setEnabled(false);
        stopBtn.addActionListener((ActionEvent evt) -> stopBtnActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(queriesScroll)
                    .addComponent(logScroll)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hostLabel)
                            .addComponent(queriesLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(gridHostField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(startBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopBtn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hostLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridHostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(queriesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queriesScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startBtn)
                    .addComponent(stopBtn))
                .addContainerGap())
        );

        pack();
    }

    private void startBtnActionPerformed(ActionEvent evt) {
        stopRequested = false;
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        new Thread(() -> {
            startScraping();
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }).start();
    }

    private void stopBtnActionPerformed(ActionEvent evt) {
        stopRequested = true;
    }

    void startScraping() {
        String host = gridHostField.getText().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }

        String[] lines = queriesArea.getText().split("\\r?\\n");
        List<String> ids = new ArrayList<>();
        for (String l : lines) {
            String t = l.trim();
            if (!t.isEmpty()) {
                ids.add(t);
            }
        }

        progressBar.setValue(0);
        progressBar.setMaximum(ids.size());
        progressLog.setText("");

        int processed = 0;
        for (String id : ids) {
            if (stopRequested) {
                break;
            }
            String result = service.scrape(id, host);
            progressLog.append(result + "\n");
            processed++;
            progressBar.setValue(processed);
        }
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }
}
