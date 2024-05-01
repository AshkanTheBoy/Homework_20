package burglary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;

public class BurglarUI {
    private JPanel panel1;
    private JButton button6;
    private JTextField passText;
    private JButton button8;
    private JButton button2;
    private JButton button4;
    private JButton button5;
    private JButton button3;
    private JButton button1;
    private JButton button7;
    private JButton button9;
    private JButton OKButton;
    private JButton cancelButton;
    private JButton button0;
    private JButton delButton;
    private JButton clearButton;
    private JTextField ansText;
    private JButton restartButton;
    private static JFrame jFrame;

    public BurglarUI() {
        jFrame = new JFrame("ALERT");
        jFrame.setVisible(true);
        jFrame.setSize(650,350);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.add(panel1);
        Font font = new Font("Dialog",Font.PLAIN,40);
        ansText.setFont(font);
        passText.setFont(font);
        jFrame.pack();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        Instant start = Instant.now();
                        Instant current = Instant.now();
                        if (ansText.getText().equals(String.valueOf(TheBurglar.reversedPassword))){
                            ansText.setForeground(Color.GREEN);
                            TheBurglar.isSuccess = true;
                        } else {
                            ansText.setForeground(Color.RED);
                        }
                        while (Duration.between(start,current).toSeconds()<1){
                            current = Instant.now();
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }

                        }
                        ansText.setForeground(Color.BLACK);
                    }
                }).start();
            }
        });
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ansText.setText("");
                if(Burglar.gameEnds){
                    Thread burglar = new Thread(new Burglar());
                    burglar.start();
                    TheBurglar.setTime(5);
                }
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+1);
                }
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+2);
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+3);
                }
            }
        });
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+4);
                }
            }
        });
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+5);
                }
            }
        });
        button6.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+6);
                }
            }
        });
        button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+7);
                }
            }
        });
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+8);
                }
            }
        });
        button9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+9);
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ansText.setText("");
            }
        });
        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ansText.getText().equals("")){
                    StringBuilder str = new StringBuilder(ansText.getText());
                    str.deleteCharAt(str.length()-1);
                    ansText.setText(str.toString());
                }
            }
        });

        button0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ansText.getText().length()<5){
                    ansText.setText(ansText.getText()+0);
                }
            }
        });

    }
    public void setPassText(String text){
        passText.setText(text);
    }
    public static JFrame getjFrame() {
        return jFrame;
    }
}
