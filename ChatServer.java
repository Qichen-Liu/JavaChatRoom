package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

public class ChatServer extends JFrame implements Runnable {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private JTextArea text;
	private int clientNum = 0;
	private ArrayList<HandleClient> threads = new ArrayList<HandleClient>();

	public ChatServer() {
		super("Chat Server");
		text = new JTextArea(10, 10);
		JScrollPane scroll = new JScrollPane(text);
		this.add(scroll);
		this.setSize(ChatServer.WIDTH, ChatServer.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		this.setVisible(true);
		Thread thread = new Thread(this);
		thread.start();
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
	}

	@Override
	public void run() {

		try {
			ServerSocket server = new ServerSocket(9898);

			text.append("Chat server started at " + new Date() + "\n");

			while (true) {

				// listen to a new connection request and increment the client number
				Socket socket = server.accept();
				clientNum++;

				text.append("Starting thread for client " + clientNum + " at " + new Date() + "\n");

				// find client's host name and IP address
				InetAddress address = socket.getInetAddress();
				text.append("Client " + clientNum + "'s host name is " + address.getHostName() + "\n");
				text.append("Client " + clientNum + "'s IP Address is " + address.getHostAddress() + "\n");

				HandleClient hclient = new HandleClient(socket, clientNum);
				Thread thread = new Thread(hclient);
				threads.add(hclient);
				thread.start();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class HandleClient implements Runnable {

		private Socket socket;
		private int clientNum;
		private DataOutputStream out;
		private DataInputStream in;

		public HandleClient(Socket socket, int clientNum) {
			this.socket = socket;
			this.clientNum = clientNum;
		}

	@Override
	public void run() {
		// initialize input and output stream
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			while(true) {
				// get input text from client
				String message = in.readUTF();
							
				for(HandleClient hclient : threads) {
					if (hclient.clientNum == this.clientNum) {
						continue;
					}
					hclient.out.writeUTF(clientNum + ": " + message);
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	}
}
