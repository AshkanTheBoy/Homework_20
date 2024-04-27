package guards2;

import java.time.LocalTime;
public class TwoGuards_OneWatch {
    public static int shiftCount = 0;
    public static boolean didClockRing = false;
    public static Watch watch = new Watch();

    public static void main(String[] args) {
        Thread guard1 = new Thread(new Guard(), "Guard 1");
        Thread guard2 = new Thread(new Guard(), "Guard 2");
        watch.startClock();
        guard1.start();
        guard2.start();
        try {
            guard1.join();
            guard2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        watch.stopClock(); //останавливаем часы
        System.out.println("Shifts completed: "+shiftCount);
    }
}

class Guard implements Runnable {
    private static Object monitor = new Object(); //объект синхронизации
    private static String guardColor = "\u001B[32m"; //цвет текста для разных "стражей"

    @Override
    public void run() {
        synchronized (monitor) {
            while (TwoGuards_OneWatch.shiftCount<4) { //длительность цикла в "сменах"
                //если будильник прозвенел
                if (TwoGuards_OneWatch.didClockRing){
                    TwoGuards_OneWatch.didClockRing = false; //отключить будильник
                    changeGuard(); //меняем "стража"
                } else { //печатаем сообщение и спим 200 мс
                    System.out.printf("%s%s | guarding\n",
                            guardColor, Thread.currentThread().getName());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            monitor.notifyAll(); //оповещаем всех
            resetColor(); //меняем цвет текста на обычный
            System.out.println(Thread.currentThread().getName()+"'s shift has ended");
        }
    }

    private void changeColor() {
        if (guardColor.equals("\u001B[32m")){ //если зеленый "страж" - меняем на желтого
            guardColor = "\u001B[33m"; //желтый текст
        } else { // наоборот - в зеленый
            guardColor = "\u001B[32m"; //зеленый текст
        }
    }

    private void resetColor() {
        System.out.print("\u001B[0m"); //текст в дефолтный цвет
    }

    private void changeGuard(){
        System.out.println("CHANGING SHIFTS");
        changeColor(); //меняем цвет "стража"
        TwoGuards_OneWatch.shiftCount++; //кол-во смен +1//обновляем время начала смены
        monitor.notify(); //освобождаем монитор
        try {
            monitor.wait(); //ждем
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Watch implements Runnable {
    private volatile double current; //текущее время
    private volatile static double realStartValue; //время, с которого часы отсчитывают
    private Thread clock = new Thread(this);

    @Override
    public synchronized void run() {
        while (true) {
            current = LocalTime.now().toSecondOfDay()+(LocalTime.now().getNano()/1_000_000_000.); //обновляем время
            //разница в секундах между стартом и текущим временем
            double difference = Math.abs(current-realStartValue);
            //System.out.printf("%.2f\n",difference);
            if (difference>=2){ //как часто в секундах будет срабатывать будильник
                TwoGuards_OneWatch.didClockRing = true; //подать сигнал на будильник
                setRealStartValue(LocalTime.now().toSecondOfDay()+
                        (LocalTime.now().getNano()/1_000_000_000.)); //установить новое "стартовое" время
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Clock stopped");
                return;
            }
        }
    }

    protected synchronized void startClock() {
        this.clock.start();
        realStartValue = LocalTime.now().toSecondOfDay()+
                (LocalTime.now().getNano()/1_000_000_000.); //сразу записываем, чтобы успеть загрузить данные
    }

    public void setRealStartValue(double realStartValue) { //установить новое стартовое время для отсчета
        Watch.realStartValue = realStartValue;
    }

    protected void stopClock(){
        this.clock.interrupt(); //останавливаем часы
    }
}

