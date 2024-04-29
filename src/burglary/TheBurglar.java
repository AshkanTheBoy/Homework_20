package burglary;

import java.time.LocalTime;
import java.util.Random;
import java.util.Scanner;

public class TheBurglar {
    public static Random random = new Random();

    public static Thread burglar;

    public static int reversedPassword = 0;

    public static boolean isTimeUp = false;
    public static void main(String[] args) {
        int password = random.nextInt(90000)+10000;
        System.out.println(password);
        int digit = 10000;
        while(digit>0){
            reversedPassword +=(password%10)*digit;
            password/=10;
            digit/=10;
        }
        //System.out.println(reversedPassword);

        Burglar burglar1 = new Burglar();
        burglar = new Thread(burglar1);
        burglar.start();
        synchronized (random){
            try {
                random.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Burglar implements Runnable{
    private boolean isNewTimerOn = false;
    private boolean needStop = false;

    @Override
    public synchronized void run() {
        LocalTime start = LocalTime.now();
        int time = TheBurglar.random.nextInt(4)+1;

        while (true) {
            System.out.println("tick");
            if (needStop) {
                break;
            }
            LocalTime current = LocalTime.now(); //обновляем время

            double startInDouble = start.getSecond()+start.getNano()/1_000_000_000.;
            double currentInDouble = current.getSecond()+current.getNano()/1_000_000_000.;
            if (currentInDouble-startInDouble >= time&&!isNewTimerOn){
                System.out.println(time+" seconds have passed");
                start = LocalTime.now();
                time = 5;
                isNewTimerOn = true;
                System.out.println("Oh no! Someone is trying to hack your door!\n" +
                        "Type in your \u001B[31mREVERSED\u001B[0m password!\n"+
                        "You have 5 seconds...");
                Thread timer = new Thread(new Timer());
                timer.start();
                int pass = new Scanner(System.in).nextInt();
                if (pass==TheBurglar.reversedPassword){
                    System.out.println("You won! Your house is secure now! :D");
                    timer.interrupt();
                    try {
                        Thread.currentThread().join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Main Clock stopped");
                return;
            }
        }
    }

    public void stopGame(){
        Thread.currentThread().interrupt();
    }
    private class Timer implements Runnable{
        @Override
        public void run() {
            synchronized (TheBurglar.random){
                LocalTime start = LocalTime.now();
                double startInDouble = start.getSecond()+start.getNano()/1_000_000_000.;
                while (true){
                    LocalTime current = LocalTime.now(); //обновляем время
                    //System.out.println("tick");
                    double currentInDouble = current.getSecond()+current.getNano()/1_000_000_000.;
                    if (currentInDouble-startInDouble>=5){
                        System.out.println("You lost! You've been robbed! D;");
                        needStop = true;
                        stopTimer();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Secondary Clock stopped");
                        TheBurglar.random.notify();
                        return;
                    }
                }
            }
        }

        public void stopTimer(){
            Thread.currentThread().interrupt();
        }
    }
}
