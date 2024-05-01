package burglary;
/*
Игра "Ограбление".
При запуске, в случайный момент времени (1-5 сек.) подается сообщение, что "нас взламывают".
У игрока есть пароль, который отображается в верхнем поле окна.
Игроку нужно за отведенное время ввести обратный(?) пароль, чтобы выиграть.
Ввод производится с помощью соответствующих кнопок в окне прилодения.
Существует функция удаления крайнего символа, очистки поля ввода и рестарта игры.
Изначально на ответ дается 10 секунд, при рестарте - 5 секунд.
Кнопка "Cancel" завершает программу.
 */
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class TheBurglar {
    public static Random random = new Random();
    public static String reversedPassword; //правильный ответ
    public static int password; //пароль
    public static BurglarUI burglarUI; //окно приложения
    public static boolean isSuccess = false; //статус выигрыша
    public static Thread burglar; //поток игры
    private static int time; //время до старта/время на решение
    public static void main(String[] args) {
        System.out.println("GET READY...");
        TheBurglar.burglarUI = new BurglarUI(); //вызываем окно приложения
        startGameAtRandomTime(); //начать игру в случ. момент
        System.out.println("Main ends");
    }

    public static void startGameAtRandomTime(){
        Instant start = Instant.now();
        time = TheBurglar.random.nextInt(4)+1; //рандомное время для старта
        Instant current = Instant.now();
        while (Duration.between(start,current).toSeconds()<time){ //считает время в секундах
            current = Instant.now();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(time+" seconds have passed");
        time = 10; //даем изначально 10 сек на ответ
        burglar = new Thread(new Burglar());
        burglar.start(); //начинаем игру
        try {
            burglar.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setNewPassword(){
        password = random.nextInt(90000)+10000; //устанавливаем случайный пятизначный пароль
    }

    public static void setReversedPassword(){
        //устанавливаем пароль в обратном порядке
        reversedPassword = new StringBuilder(String.valueOf(password)).reverse().toString();
    }
    public static int getTime() {
        return time;
    }

    public static void setTime(int time) {
        TheBurglar.time = time; //установить новое время для ответа
    }
}

class Burglar implements Runnable{
    private boolean isNewTimerOn = false;
    public static boolean gameEnds = false; //условие работы потока/ов
    private static Thread timer;

    @Override
    public synchronized void run() {
        resetGame(); //сбросить настройки для возможности переигровки
        while (!gameEnds) {
            Timer clock = new Timer();
            timer = new Thread(clock); //таймер, который отсчитывает время ответа
            if (!isNewTimerOn){ //запускаем таймер и условие
                isNewTimerOn = true;
                System.out.printf("Oh no! Someone is trying to hack your door!\n" +
                        "Type in your \u001B[31mREVERSED\u001B[0m password!\n"+
                        "You have %d seconds...\n",TheBurglar.getTime());
                //помещаем пароль в поле окна
                TheBurglar.burglarUI.setPassText(String.valueOf(TheBurglar.password));
                timer.start();
            }
            if (TheBurglar.isSuccess){ //если ответ верный (ответ=пароль наоборот)
                System.out.println("You \u001B[32mWON!\u001B[0m Your house is secure now! :D");
                stopGame(); //останавливаем игру
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Main Clock stopped");
                return;
            }
        }
        System.out.println("Burglar stopped"); //проверка, что поток остановился
    }

    private static void resetGame(){
        TheBurglar.setNewPassword(); //новый рандомный пароль
        TheBurglar.setReversedPassword(); //установить правильный ответ
        gameEnds = false; //сбросить статус игры
        TheBurglar.isSuccess = false; //сбросить статус успешного ответа
    }

    public static void stopGame(){
        gameEnds = true; //прерываем цикл потока
    }
    private class Timer implements Runnable{ //таймер, который считает время ответа
        @Override
        public void run() {
            Instant start = Instant.now();
            while (!Burglar.gameEnds){
                Instant current = Instant.now(); //обновляем время
                //если время вышло и ответа нет, или ответ неправильный
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
            System.out.println("Timer stopped"); //проверка что поток остановлен
        }
    }
}
