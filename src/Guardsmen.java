public class Guardsmen {
    public static int shiftCount = 0;

    public static void main(String[] args) {
        Guard guard11 = new Guard();
        Guard guard22 = new Guard();
        Thread guard1 = new Thread(guard11, "Guard 1");
        Thread guard2 = new Thread(guard22, "Guard 2");
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
    private static Object monitor = new Object();
    private static boolean isWaiting = false;
    private int timeOnShift = 0;
    private Watch watch = new Watch();
    private static String guardColor = "\u001B[32m";

    @Override
    public void run() {
        synchronized (monitor) {
            this.startClock();
            while (Guardsmen.shiftCount<3) {
                checkTime();
                if (timeOnShift>12&&!isWaiting){
                    changeColor();
                    isWaiting = true;
                    Guardsmen.shiftCount++;
                    monitor.notify();
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (timeOnShift>12&&isWaiting){
                    resetColor();
                    isWaiting = false;
                    Guardsmen.shiftCount++;
                    monitor.notify();
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.printf("%s%s | guarding | time on the clock: %d\n",
                            guardColor, Thread.currentThread().getName(), this.timeOnShift);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            monitor.notifyAll();
            System.out.println("\u001B[0m"+Thread.currentThread().getName()+"'s shift has ended");
            this.stopClock();
        }
    }

    private void checkTime() {
        this.timeOnShift = this.watch.getTime();
    }

    private void startClock() {
        this.watch.startClock();
    }

    public void stopClock(){
        this.watch.stopClock();
    }

    private void changeColor() {
        guardColor = "\u001B[33m";
    }

    private void resetColor() {
        guardColor = "\u001B[32m";
    }
}

class Watch implements Runnable {
    private volatile int time = 0;
    private Thread clock = new Thread(this);

    @Override
    public synchronized void run() {
        while (true) {
            while (this.time < 24) {
                this.time++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Clock is stopped");
                    return;
                }
            }
            this.time = 0;
        }
    }

    public void startClock() {
        this.clock.start();
    }

    public int getTime() {
        return this.time;
    }

    public void stopClock(){
        this.clock.interrupt();
    }
}

