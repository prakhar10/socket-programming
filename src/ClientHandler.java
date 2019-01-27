// Prakhar Sapre	
// 1001514586

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import javax.swing.JTextArea;

/**
 * This class implements the ClientHandler functionality to handle messages from
 * client
 * 
 * @author Prakhar
 *
 */
public class ClientHandler extends Thread implements Runnable {

	// Declare global variables
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	JTextArea txtMessageBox;
	String clientName;

	// ClientHandler constructor to initialize variables
	public ClientHandler(Socket socket, DataInputStream dis, JTextArea txtMessageBox, String clientName) {

		this.socket = socket;
		this.dis = dis;
		this.txtMessageBox = txtMessageBox;
		this.clientName = clientName;
	}

	/**
	 * This method is called implicitly to execute the thread and perform any
	 * operations
	 */
	@Override
	public void run() {
		try {
			dos = new DataOutputStream(socket.getOutputStream());

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String message;
		String decodedMsg;
		int number;

		// Keep running the loop to send and recieve messages from client untill the
		// client is killed or server is stopped
		while (true) {
			// check is socket is connected or not
			if (socket.isConnected()) {
				try {
					// read the message received from server or client
					message = dis.readUTF();
					
					// If the message contains 'Kill', then display which client is killed in server
					// textarea and kill the client
					if (message.contains("Kill")) {
						txtMessageBox.append("\n Client name " + clientName + " has been killed. \n");
						dos.writeUTF("Killed");
						break;
					}

					txtMessageBox.append("\n" + message);

					// If any other message recieved from server then decode it to parse the message
					decodedMsg = decode(message);

					// number variable will contain the number of seconds the server needs to wait
					// to respond to the client
					number = Integer.parseInt(decodedMsg.replaceAll("[^0-9]", "").trim());

					// this will make the thread sleep for given number of seconds
					Thread.sleep(number * 1000);

					// send message to the client after waiting
					dos.writeUTF(getHttpMessage(
							"Server waited for " + number + " seconds for client " + clientName + ". \n"));
				} catch (Exception e) {
					System.out.println("Server stopped. Connection closed.");
					break;
				}
			}
		}

	}

	/**
	 * This method will generate a GET http message
	 * 
	 * @param msg
	 * @return
	 */
	public String getHttpMessage(String msg) {

		return "GET Http/1.1 \n" + "Host: www.uta.com \n" + "User-Agent: Mozilla/5.0 \n"
				+ "Content=type: application/x-www-form-urlencoded \n" + "Content-Length: " + msg.length() + " \n"
				+ "Date:" + new Date() + " \n" + "message: " + msg;
	}

	/**
	 * This method will get the substring from the http message
	 * 
	 * @param message
	 * @return
	 */
	private String decode(String message) {
		return message.substring(message.indexOf("message:"), message.length());
	}

}
