package burglary;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class TheBurglar {
    public static Random random = new Random();
    public static String reversedPassword;
    public static int password;
    public static BurglarUI burglarUI;
    public static boolean isSuccess = false;
    public static Thread burglar;
    private static int time;
    public static void main(String[] args) {
        System.out.println("GET READY...");
        TheBurglar.burglarUI = new BurglarUI();
        startGameAtRandomTime();
        System.out.println("Main ends");
    }

    public static void startGameAtRandomTime(){
        Instant start = Instant.now();
        time = TheBurglar.random.nextInt(4)+1;
        Instant current = Instant.now();
        while (Duration.between(start,current).toSeconds()<time){
            current = Instant.now();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(time+" seconds have passed");
        time = 10;
        burglar = new Thread(new Burglar());
        burglar.start();
        try {
            burglar.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setNewPassword(){
        password = random.nextInt(90000)+10000;
    }

    public static void setReversedPassword(){
        reversedPassword = new StringBuilder(String.valueOf(password)).reverse().toString();
    }
    public static int getTime() {
        return time;
    }

    public static void setTime(int time) {
        TheBurglar.time = time;
    }
}

class Burglar implements Runnable{
    private boolean isNewTimerOn = false;
    public static boolean gameEnds = false;

    private static Thread timer;

    @Override
    public synchronized void run() {
        resetGame();
        while (!gameEnds) {
            Timer clock = new Timer();
            timer = new Thread(clock);
             //обновляем время
            if (!isNewTimerOn){
                isNewTimerOn = true;
                System.out.printf("Oh no! Someone is trying to hack your door!\n" +
                        "Type in your \u001B[31mREVERSED\u001B[0m password!\n"+
                        "You have %d seconds...\n",TheBurglar.getTime());
                TheBurglar.burglarUI.setPassText(String.valueOf(TheBurglar.password));
                timer.start();
            }
            if (TheBurglar.isSuccess){
                System.out.println("You \u001B[32mWON!\u001B[0m Your house is secure now! :D");
                stopGame();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Main Clock stopped");
                return;
            }
        }
        System.out.println("Burglar stopped");
    }

    private static void resetGame(){
        TheBurglar.setNewPassword();
        TheBurglar.setReversedPassword();
        gameEnds = false;
        TheBurglar.isSuccess = false;
    }

    public static void stopGame(){
        gameEnds = true;
    }
    private class Timer implements Runnable{
        @Override
        public void run() {
            Instant start = Instant.now();
            while (!Burglar.gameEnds){
                Instant current = Instant.now(); //обновляем время
                if (Duration.between(start,current).toSeconds()>=TheBurglar.getTime()&&!TheBurglar.isSuccess){
                    System.out.println("You \u001B[31mLOST!\u001B[0m You've been robbed! D;");
                    stopGame();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Secondary Clock stopped");
                    return;
                }
            }
            System.out.println("Timer stopped");
        }
    }
}
