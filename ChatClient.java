package chat;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import chat.ChatServer.HandleClient;

public class ChatClient extends JFrame implements Runnable {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	DataInputStream in;
	DataOutputStream out;
	JTextField textField;
	JTextArea textArea;
	Socket socket;

	public ChatClient() {
		super("Chat Client");
		textField = new JTextField(30);
		textArea = new JTextArea(30, 30);
		this.add(textField, BorderLayout.NORTH);
		this.setLayout(new BorderLayout());
		textField.addActionListener(new TextListener());
		JPanel panel = new JPanel();
		panel.add(textField);
		this.add(panel, BorderLayout.SOUTH);
		this.add(textArea, BorderLayout.CENTER);
		createMenu();
		this.setSize(ChatClient.WIDTH, ChatClient.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem connection = new JMenuItem("connect");
		connection.addActionListener(new ConnectionListener());
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(connection);
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	public void run() {
		// override method
	}

	class TextListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e1) {
				textArea.append(e1.toString() + '\n');
			}

			String message = textField.getText();
			try {
				out.writeUTF(message);
				out.flush();
				textArea.append(message + "\n");
				///////// textField.setText(null);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class ConnectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				socket = new Socket("localhost", 9898);
				textArea.append("connected\n");
				in = new DataInputStream(socket.getInputStream());

				Thread t = new Thread(new MessageHandler(socket, in));
				t.start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	class MessageHandler implements Runnable {

		private Socket socket;
		private DataInputStream fromServer;

		public MessageHandler(Socket socket, DataInputStream fromServer) {
			this.socket = socket;
			this.fromServer = fromServer;

		}

		@Override
		public void run() {
			try {
				// always listen from server
				while (true) {
					// Receive text from the server
					String message = fromServer.readUTF();
					// Send area back to the client
					textArea.append(message + "\n");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
	}
}
