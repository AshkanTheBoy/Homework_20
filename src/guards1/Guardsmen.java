package guards1;

import java.time.LocalTime;
public class Guardsmen {
    public static int shiftCount = 0;

    public static void main(String[] args) {
        Thread guard1 = new Thread(new Guard(), "Guard 1");
        Thread guard2 = new Thread(new Guard(), "Guard 2");
        guard1.start();
        guard2.start();
        try {
            guard1.join();
            guard2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Shifts completed: "+shiftCount);
    }
}

class Guard implements Runnable {
    private static Object monitor = new Object(); //объект синхронизации
    private static LocalTime shiftStartTime; //время, на котором "принял" пост
    private static LocalTime timeOnShift; //текущее время
    private Watch watch = new Watch();
    private static String guardColor = "\u001B[32m"; //цвет текста для разных "стражей"

    @Override
    public void run() {
        synchronized (monitor) {
            this.startClock(); //активируем часы
            shiftStartTime = LocalTime.now(); //помечаем время старта
            while (Guardsmen.shiftCount<4) { //длительность цикла в "сменах"
                // стартовое в вещественное число
                double realStartValue = shiftStartTime.toSecondOfDay()+(shiftStartTime.getNano()/1_000_000_000.);
                //System.out.println(realStartValue);

                checkTime(); //обновить текущее время

                // текущее время в вещественное число
                double realCurrentValue = timeOnShift.toSecondOfDay()+(timeOnShift.getNano()/1_000_000_000.);
                //System.out.println(realStartValue);

                //разница между стартом и текущим временем
                double difference = Math.abs(realCurrentValue-realStartValue);
                System.out.printf("%.2f\n",difference);

                //если разница между стартом и текущим временем в секундах больше 3
                if (difference>=3){
                    changeGuard(); //меняем "стража"
                    /*
                    проблема: getSecond() берется в целых числах, поэтому на стыке секунд
                    при условии "3.9с. - 6с." пройдет так же 3 "секунды", а не 2.1, как положено.
                    Частично решается увеличением диапазона до "секунда дня", но на стыке смены суток проблема вернется
                    P.S. Вроде решил с помощью вещественных чисел и наносекунд
                     */
                } else { //печатаем сообщение и спим 200 мс
                    System.out.printf("%s%s | guarding | time on the clock: %s\n",
                            guardColor, Thread.currentThread().getName(), timeOnShift.getSecond());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            monitor.notifyAll(); //оповещаем всех
            resetColor(); //меняем цвет текста на обычный
            System.out.println(Thread.currentThread().getName()+"'s shift has ended");
            this.stopClock(); //останавливаем часы

        }
    }

    private synchronized void checkTime() {
        timeOnShift = this.watch.getTime(); //смотрим на часы и обновляем время на смене
    }

    private void startClock() {
        this.watch.startClock(); //запускаем часы
    }

    public void stopClock(){
        this.watch.stopClock(); //останавливаем часы
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
        Guardsmen.shiftCount++; //кол-во смен +1
        shiftStartTime = LocalTime.now(); //обновляем время начала смены
        monitor.notify(); //освобождаем монитор
        try {
            monitor.wait(); //ждем
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Watch implements Runnable {
    private LocalTime current; //текущее время
    private Thread clock = new Thread(this);

    @Override
    public synchronized void run() {
        while (true) {
            current = LocalTime.now(); //обновляем время
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Clock stopped");
                return;
            }
        }
    }

    protected void startClock() {
        this.clock.start();
        this.current = LocalTime.now(); //сразу записываем, чтобы успеть загрузить данные
    }

    protected LocalTime getTime() {
        return this.current; //возвращаем время на часах
    }

    protected void stopClock(){
        this.clock.interrupt(); //останавливаем часы
    }
}

