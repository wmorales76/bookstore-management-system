package edu.suagm.soe.tftpexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class tftpServer {

	public static void main(String[] args) {
		int port = (new Integer(args[0])).intValue();
		boolean running = true;
		try {
			// Create the socket
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Socket created on port: " + port);
			System.out.println("Waiting for connections");

			while (running) {
				// Wait for connections
				Socket clientSocket = serverSocket.accept();
				System.out.println("New Connection");

				// Create the handler for each client connection
				tftpHandler handler = new tftpHandler(clientSocket);

				// Do data transfer
				handler.start();
			}
			// Close the server socket
			serverSocket.close();

		} catch (IOException e) {
		}finally {
			System.out.println("Server closed");
		}
	}

}
