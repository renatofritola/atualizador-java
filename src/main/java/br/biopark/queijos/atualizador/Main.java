/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.biopark.queijos.atualizador;

import java.util.logging.Level;
import java.util.logging.Logger;
import br.biopark.queijos.atualizador.util.PropFile;
import br.biopark.queijos.atualizador.util.Util;
import br.biopark.queijos.atualizador.enumerator.EPropertie;
import br.biopark.queijos.atualizador.util.GitUtil;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 *
 * @author Renato
 */
public class Main {

    private static Util util = new Util();
    private static PropFile prop = new PropFile();
    private static String versaoAtual = "";
    private static List<String> versoes = new ArrayList();
    private static String novaVersao = "";
    private SystemTray tray;
    private static TrayIcon trayIcon;

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }
    
    public static void main(String[] args) {
        Main main = new Main();

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("ProgressBar.background", Color.gray);
            UIManager.put("ProgressBar.foreground", Color.orange);
            UIManager.put("ProgressBar.selectionBackground", Color.white);
            UIManager.put("ProgressBar.selectionForeground", Color.white);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            main.displayTray();
        } catch (AWTException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        int delay = 5000;   // delay de 5 seg.
        int interval = 50000;//Integer.parseInt(prop.readPropertie(EPropertie.UPDATE_CHECK_FREQUENCY)) * 3600;  // intervalo configurado.
        Timer timer = new Timer();

        //timer.scheduleAtFixedRate(new TimerTask() {
        //    public void run() {
        versaoAtual = prop.readPropertie(EPropertie.APLICATION_VERSION);
        GitUtil git = new GitUtil();
        versoes = git.buscaVersoes(prop.readPropertie(EPropertie.URL_BASE_GIT));

        if (main.checkNewVersion()) {
//            main.iniciarAtualizacao();
            main.getTrayIcon().displayMessage("Atualizador Queijos", "Uma nova versão foi encontrada. \nClique aqui para atualizar.", MessageType.INFO);
            main.getTrayIcon().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Versao versao = new Versao(Progress.getInstance(), true, versaoAtual, versoes, trayIcon);
                    main.getTrayIcon().addActionListener(null);
                    versao.setLocationRelativeTo(null);
                    versao.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                    versao.getjLabel1().setText("Nova versão "+novaVersao);
                    versao.setVisible(true);
                }
            });
            

        }
        //}
        // }, delay, interval);

    }

    private boolean checkNewVersion() {

        int vAtual = Integer.parseInt(versaoAtual.replace(".", ""));

        for (String v : versoes) {

            int vRem = Integer.parseInt(v.replace(".", ""));

            if (vRem > vAtual) {
                novaVersao = v;
                return true;
            }

        }

        return false;
    }

    static class MyProgressUI extends BasicProgressBarUI {

        Rectangle r = new Rectangle();

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            r = getBox(r);
            g.setColor(progressBar.getForeground());
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    public void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        tray = SystemTray.getSystemTray();

        //If the icon is a file
        //Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/queijo.png"));

        trayIcon = new TrayIcon(image, "Atualizador Queijos");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);

        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Atualizados Queijos");
        tray.add(trayIcon);

        trayIcon.displayMessage("Atualizador Queijos", "Atualizador sendo executado em segundo plano.", MessageType.INFO);
    }
    
}
