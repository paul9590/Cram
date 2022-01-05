package com.pingmo.cram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterRequest extends Thread{
    private Socket socket;
    private String ip;
    private int port;
    private BufferedReader br;
    private PrintWriter pw;
    private String query;
    String flag = "0";

    public RegisterRequest(String ip, int port, String query) {
        this.ip = ip;
        this.port = port;
        this.query = query;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            byte[] binary = query.getBytes("UTF-8");
            String data = new String(binary, "UTF-8");
            pw.println(data);
            pw.flush();
            flag = br.readLine();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if(socket != null) {
                    socket.close();
                }
                if(pw != null) {
                    pw.close();
                }
                if(br != null) {
                    br.close();
                }
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    synchronized public String isCompleted(){
        return flag;
    }
}


