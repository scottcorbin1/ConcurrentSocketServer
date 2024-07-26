import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select a port to listen on: ");
        int port = scanner.nextInt();
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.println("Server is running on port " + port);
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                handleClient(clientSocket);
            }
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        scanner.close(); //remove this if it possibly causes problems
    }

    private static void handleClient(Socket clientSocket)
    {
        Thread clientThread = new Thread(() -> {
            try
            {
                InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                String command = reader.readLine();
                if (command.equals("Exit"))
                {
                    clientSocket.close();
                    return;
                }
                String result = runCommand(command);
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                writer.println(result);
                System.out.println("Result:\n" + result);
                writer.flush();
                clientSocket.close();
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }

    private static String runCommand(String command) throws IOException
    {
        Process process = Runtime.getRuntime().exec(command);
        InputStreamReader inputReader = new InputStreamReader(process.getInputStream());
        BufferedReader reader = new BufferedReader(inputReader);
        String line="";
        String result="";
        while ((line = reader.readLine()) != null)
        {
            result += line;
        }
        return result;
    }
}
