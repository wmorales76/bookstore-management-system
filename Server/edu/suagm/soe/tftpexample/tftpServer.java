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

/**
 * The tftpServer class represents a TFTP server that listens for client connections and handles data transfer.
 */
public class tftpServer {

	/**
	 * The main method of the tftpServer class.
	 * It creates a server socket, waits for client connections, and handles data transfer for each client.
	 *
	 * @param args the command line arguments. The first argument should be the port number to listen on.
	 */
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
		} finally {
			System.out.println("Server closed");
		}
	}

}
