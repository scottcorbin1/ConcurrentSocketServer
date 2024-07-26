import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static long avgTime;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server address: ");
        String serverAddress = scanner.nextLine();

        System.out.print("Enter the server port: ");
        int serverPort = scanner.nextInt();
        scanner.nextLine();

        while (true) {
            avgTime = 0;
            System.out.println("Commands:");
            System.out.println("1. Date and Time");
            System.out.println("2. Uptime");
            System.out.println("3. Memory Use");
            System.out.println("4. Netstat");
            System.out.println("5. Current Users");
            System.out.println("6. Running Processes");
            System.out.println("0. Exit");
            System.out.print("Enter the command number: ");
            int commandNumber = scanner.nextInt();
            scanner.nextLine();

            String operation;
            switch (commandNumber) {
                case 1:
                    operation = "date";
                    break;
                case 2:
                    operation = "uptime";
                    break;
                case 3:
                    operation = "free";
                    break;
                case 4:
                    operation = "netstat";
                    break;
                case 5:
                    operation = "users";
                    break;
                case 6:
                    operation = "ps aux";
                    break;
                case 0:
                    operation = "Exit";
                    break;
                default:
                    System.out.println("Invalid command number. Please try again.");
                    continue;
            }

            if (operation.equals("Exit")) {
                break;
            }

            System.out.print("Enter the number of requests: ");
            int numberOfRequests = scanner.nextInt();
            scanner.nextLine();

            List<Thread> threads = threadHandler(serverAddress, serverPort, operation, numberOfRequests);

            long startTime = System.currentTimeMillis();

            // Start all threads at the same time
            for (Thread thread : threads) {
                thread.start();
            }

            // Wait for all threads to complete
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Average execution time: " + (avgTime/numberOfRequests) + "ms");

            System.out.println("Total Execution Time for all requests: " + totalTime + " ms");
        }
        scanner.close();
    }

    private static List<Thread> threadHandler(String serverAddress, int serverPort, String operation, int numberOfRequests) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numberOfRequests; i++) {
            Thread t = new Thread(() -> {
                try (Socket socket = new Socket(serverAddress, serverPort);
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    long executionTimeStart = System.currentTimeMillis();
                    writer.println(operation);

                    String response = reader.readLine();
                    System.out.println(response);
                    long executionTimeEnd = System.currentTimeMillis();
                    long executionTime = executionTimeEnd - executionTimeStart;
                    avgTime += executionTime;
                    System.out.println("Execution time: " + executionTime + "ms");

                } catch (IOException e) {
                    
                    e.printStackTrace();
                }
            });
            threads.add(t);
        }
        return threads;
    }
}
