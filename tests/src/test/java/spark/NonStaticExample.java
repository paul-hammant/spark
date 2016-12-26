package spark;

public class NonStaticExample {

    public static void main(String[] args) {

        Service svc = new Service() {{

            get("/greeting", (request, response) -> "hello");

        }}.init();


        // unnecessary really ....
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("stopping");
                svc.stop();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                }
            }
        });

    }


}
