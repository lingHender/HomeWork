package ali.deadLock;

public class SimpleExample {

    public static Object object1 = new Object();
    public static Object object2 = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " start");
                try {
                    synchronized (object1) {
                        System.out.println(Thread.currentThread().getName() + " get object1 locked");
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + " try to lock object2");
                        synchronized (object2) {
                            System.out.println(Thread.currentThread().getName() + " get object2 locked");
                            Thread.sleep(3000);

                        }
                    }
                } catch (Exception e) {
                    System.out.println("ops! here is an error: "+e.getStackTrace());
                }

        }, "Thread1");

        Thread thread2 = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " start");
                try {
                    synchronized (object2) {
                        System.out.println(Thread.currentThread().getName() + " get object2 locked");
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName() + " try to lock object1");
                        synchronized (object1) {
                            System.out.println(Thread.currentThread().getName() + " get object1 locked");
                            Thread.sleep(3000);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ops! here is an error: " + e.getStackTrace());
                }


        }, "Thread2");

        thread1.start();
        thread2.start();
    }

}
