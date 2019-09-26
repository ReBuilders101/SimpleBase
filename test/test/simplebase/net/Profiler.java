package test.simplebase.net;

import java.util.LinkedList;
import java.util.Queue;

import lb.simplebase.log.LogLevel;
import lb.simplebase.net.NetworkManager;

@Deprecated
public class Profiler {

	public static void init() {
		NetworkManager.setLogLevel(LogLevel.WARN); //Disable info output
	}
	
	public static void dump() {
		int i = 0;
		while(!dataSend.isEmpty() && !dataReceive.isEmpty()) {
			byte send = dataSend.poll();
			byte receive = dataReceive.poll();
			System.out.println(i + ": " + send + "(" + Integer.toHexString(send & 0xFF) + ") || " + receive + "(" + Integer.toHexString(receive & 0xFF) + ")" + " => " + (send == receive ? "OK" : "FAILED"));
			i++;
		}
		
		System.out.println("Extra elements in send buffer:");
		while(!dataSend.isEmpty()) {
			byte send = dataSend.poll();
			System.out.println(i + ": " + send + Integer.toHexString(send & 0xFF));
			i++;
		}
		
		System.out.println("Extra elements in receive buffer:");
		while(!dataSend.isEmpty()) {
			byte receive = dataReceive.poll();
			System.out.println(i + ": " + receive + Integer.toHexString(receive & 0xFF));
			i++;
		}
		
		System.out.println("No more elements");
	}
	
	private static Queue<Byte> dataSend = new LinkedList<>();
	private static Queue<Byte> dataReceive = new LinkedList<>();
	
	public static void send(byte data) {
		dataSend.add(data);
	}

	public static void send(byte[] data) {
		for(byte b : data) dataSend.add(b);
	}

	public static void receive(byte data) {
		dataReceive.add(data);
	}

	public static void receive(byte[] data) {
		for(byte b : data) dataReceive.add(b);
	}
}
