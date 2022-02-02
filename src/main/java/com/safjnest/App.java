/**
 * Copyright (c) 22 Giugno anno 0, 2022, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * 
 */
package com.safjnest;

import java.util.*;
import java.io.*;
import java.net.*;
/**
* POVERO GABBIANOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
* @author @SafJNest Official Corporation Industries codingTech & Co & Sons & his affilitaes & @{@code DBMR}
*/
public class App {
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        System.out.println("*************GABBIANO IN ESECUZIONE******************");
        serverSocket = new ServerSocket(80);

        InetAddress IP=InetAddress.getLocalHost();
        //InetAddress IP = InetAddress.getByName("safjnest.ddns.net");
        System.out.println("IP of my seagull is := " + IP.getHostAddress());
        
        while (true) {
            try {
                Socket s = serverSocket.accept();
                System.out.println("Connesso ad un gabbiano: " + s.getRemoteSocketAddress() + "\n");
                new ClientHandler(s);
            } catch (Exception x) {
                System.out.println(x);
            }
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket s) {
        socket = s;
        start();
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
            String s = in.readLine();
            System.out.println("request: " + s);
            String filename = "";
            StringTokenizer st = new StringTokenizer(s);

            try {
                if (st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET") && st.hasMoreElements()) {
                    filename = st.nextToken();
                    System.out.println("filename = " + filename);
                } else
                    throw new FileNotFoundException();
                if (filename.endsWith("/"))
                    filename += "index.html";
                while (filename.indexOf("/") == 0)
                    filename = filename.substring(1);
                if (filename.indexOf("..") >= 0 || filename.indexOf(":") >= 0 || filename.indexOf("|") >= 0)
                    throw new FileNotFoundException();
                if (new File(filename).isDirectory()) {
                    filename = filename.replace("\\", "/");
                    out.print("HTTP/1.0 301 Moved Permanently\r\n" + "Location: /" + filename + "/\r\n\r\n");
                    out.close();
                    return;
                }
                System.out.println("filename to open = " + filename);
                InputStream f = new FileInputStream(filename);
                String mimeType = "text/plain";
                if (filename.endsWith(".html") || filename.endsWith(".htm"))
                    mimeType = "html";
                else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
                    mimeType = "image/jpeg";
                else if (filename.endsWith(".gif"))
                    mimeType = "image/gif";
                else if (filename.endsWith(".class"))
                    mimeType = "application/octet-stream";
                out.print("HTTP/1.0 200 OK\r\n" + "Content-type: " + mimeType + "\r\n\r\n");
                byte[] a = new byte[1604096];
                int n;
                while ((n = f.read(a)) > 0)
                    out.write(a, 0, n);
                out.close();
            } catch (FileNotFoundException x) {
                out.println("HTTP/1.0 404 Not Found\r\n" + "Content-type: text/html\r\n\r\n"
                        + "<html><head></head><body>" + filename + "not found</body></html>\n");
                out.close();
            }
        } catch (IOException x) {
            System.out.println(x);
        }
    }
}