/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author s506571
 */
public class ServerThread extends Thread {
    Socket sock;
    Server serv;
    BufferedReader in;
    PrintWriter out;
    String pname;
    public ServerThread(Socket sock, Server serv) {
        this.sock = sock;
        this.serv = serv;
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        if (serv == null || sock == null || in == null || out == null) return;
        try {
            try {
                pname = in.readLine();
                if (pname == null || pname.equals(""))
                    throw new IOException("Invalid name");
                serv.register(pname, this);
            } catch (Server.NameTakenException e) {
                out.println("ERROR name taken");
                out.flush();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
            // read, write
            while (true) {
                String cmd = in.readLine();
                if (cmd == null) break;
                if (cmd.equals("DEAL")) {
                    System.out.println(pname + " deals");
                } else if (cmd.equals("CLAIM")) {
                    System.out.println(pname + " claims");
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                sock.close();
                serv.deregister(pname, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
