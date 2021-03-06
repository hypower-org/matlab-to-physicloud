package edu.gatech.hypower;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
public class PhysiCloudClient {
	
	//fields -
	private Thread worker; //thread for doing the work
	private volatile boolean stopRequested; //boolean for stopping thread gracefully
	private Socket pcClient; //client socket for communication with Physicloud network over TCP
	private ObjectOutputStream out; //object out stream for reading objects from the Physicloud network
	private ObjectInputStream in; //object in stream for writing objects to the Physicloud network
	private ConcurrentHashMap<String, Object> currentData; //concurrent map for incoming data
	
	//constructor - start worker thread in boolean-dependent loop
	public PhysiCloudClient(){
		currentData = new ConcurrentHashMap<String, Object>();
		stopRequested = false;
		worker = new Thread(){
			public void run(){
				while(!stopRequested){
					getPCData();
					try{
						Thread.sleep(100);
					}catch(InterruptedException e){
						System.out.println("Physicould client terminated");
						Thread.currentThread().interrupt();
					}
				}
			}
		};
		try {
			connectToPhysiCloud();
		}
		catch (InterruptedException e) {e.printStackTrace();}
		worker.start();
	}
	
	//method for gracefully ending worker thread
	public void kill(){
		stopRequested = true;
		if(worker != null){
			worker.interrupt();
		}
	}
	
	//method to receive data from physicloud network and update
	//concurrent data structure
	@SuppressWarnings("unchecked")
	private void getPCData() {
		try {
			HashMap<String, Object> dataIn = (HashMap<String, Object>) in.readObject();
			currentData.putAll(dataIn);
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {}
	}
	
	//utility method for connecting TCP client
	private void connectToPhysiCloud() throws InterruptedException{
		Boolean connected = false;
		while(!connected){
			try {
				pcClient = new Socket("127.0.0.1", 8756);
				out = new ObjectOutputStream(pcClient.getOutputStream());
				in = new ObjectInputStream(pcClient.getInputStream());
				connected = true;
			}
			catch (IOException e) {
				System.out.println("Server not up...");
				Thread.sleep(2500);
			}
		}
	}
	
	//method for MATLAB users to get the state data of
	// a particular robot
	@SuppressWarnings("rawtypes")
	public double[] getData(String id){
		double[] data = new double[7];
		if(currentData != null){
			if(currentData.containsKey(id)){
				Vector robotState = (Vector) currentData.get(id);
				data[0] = ((double) robotState.get(0));
				data[1] = ((double) robotState.get(1));
				data[2] = ((double) robotState.get(2));
				data[3] = ((Integer) robotState.get(3)).doubleValue();
				data[4] = ((Integer) robotState.get(4)).doubleValue();
				data[5] = ((Integer) robotState.get(5)).doubleValue();
				data[6] = ((Integer) robotState.get(6)).doubleValue();
			}
			else{
				System.out.println("Error: Robot with that ID does not exist");
			}
		}
		return data;
	}
	
	//method for matlab to know how many agents are in the system
	public int numAgents(){
		return currentData.size();
	}
	
	//method for MATLAB users to stop all robots' movement
	public void stop(){
		HashMap<String, Object> stopMap = new HashMap<String, Object>();
		stopMap.put("command", "stop");
		try {
			out.writeObject(stopMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to stop a specific robot's movement
	public void stop(String robotId){
		HashMap<String, Object> stopMap = new HashMap<String, Object>();
		stopMap.put("command", "stop");
		Vector<String> ids = new Vector<String>();
		ids.add(robotId);
		stopMap.put("ids", ids);
		try {
			out.writeObject(stopMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to stop a given set of robots' movement
	public void stop(String[] robotIds){
		HashMap<String, Object> stopMap = new HashMap<String, Object>();
		stopMap.put("command", "stop");
		Vector<String> ids = new Vector<String>();
		for (int i = 0; i < robotIds.length; i++){
			ids.add(robotIds[i]);
		}
		stopMap.put("ids", ids);
		try {
			out.writeObject(stopMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to drive all robots at v, w
	public void drive(Double v, Double w){
		HashMap<String, Object> driveMap = new HashMap<String, Object>();
		driveMap.put("command", "drive");
		driveMap.put("v", v);
		driveMap.put("w", w);
		try {
			out.writeObject(driveMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to drive a specific robot at v, w
	public void drive(String robotId, Double v, Double w){
		HashMap<String, Object> driveMap = new HashMap<String, Object>();
		driveMap.put("command", "drive");
		Vector<Double> velocities = new Vector<Double>(2);
		velocities.add(0, v);
		velocities.add(1, w);
		driveMap.put(robotId, velocities);
		try {
			out.writeObject(driveMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to drive a given set of robots at v, w
	public void drive(String[] robotIds, Double v, Double w){
		HashMap<String, Object> driveMap = new HashMap<String, Object>();
		driveMap.put("command", "drive");
		for (int i = 0; i < robotIds.length; i++){
			Vector<Double> velocities = new Vector<Double>(2);
			velocities.add(0, v);
			velocities.add(1, w);
			driveMap.put(robotIds[i], velocities);
		}
		try {
			out.writeObject(driveMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to drive a given set of robots at different v, w values
	public void drive(String[] robotIds, Double[] vs, Double[] ws){
		HashMap<String, Object> driveMap = new HashMap<String, Object>();
		driveMap.put("command", "drive");
		for (int i = 0; i < robotIds.length; i++){
			Vector<Double> velocities = new Vector<Double>(2);
			velocities.add(0, vs[i]);
			velocities.add(1, ws[i]);
			driveMap.put(robotIds[i], velocities);
		}
		try {
			out.writeObject(driveMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to reset a specified state variable (x, y, t) for a specific robot
	public void resetCoords(String robotId, Double[] coords){
		HashMap<String, Object> resetCoordsMap = new HashMap<String, Object>();
		resetCoordsMap.put("command", "reset-coords");
		Vector<String> ids = new Vector<String>();
		ids.add(robotId);
		resetCoordsMap.put("ids", ids);
		resetCoordsMap.put("x", coords[0]);
		resetCoordsMap.put("y", coords[1]);
		resetCoordsMap.put("t", coords[2]);
		try {
			out.writeObject(resetCoordsMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to toggle an led on a given set of robots
	//color should be "red", "green", or "off" (technically any other string will work for "off")
	public void led(String[] robotIds, int led, String color){
		HashMap<String, Object> ledMap = new HashMap<String, Object>();
		ledMap.put("command", "led");
		Vector<String> ids = new Vector<String>();
		for (int i = 0; i < robotIds.length; i++){
			ids.add(robotIds[i]);
		}
		ledMap.put("ids", ids);
		ledMap.put("led", led);
		ledMap.put("color", color);
		try {
			out.writeObject(ledMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	//method for MATLAB users to play one of the pre-programmed sounds on the kobuki
	//integer tag defines sound to be played:
	//		0 for ON sound
	//		1 for OFF sound
	//		2 for RECHARGE sound
	//		3 for BUTTON sound
	//		4 for ERROR sound
	//		5 for CLEANINGSTART sound
	//		6 for CLEANINGEND sound
	public void sound(String[] robotIds, int sound){
		HashMap<String, Object> soundMap = new HashMap<String, Object>();
		soundMap.put("command", "sound");
		Vector<String> ids = new Vector<String>();
		for (int i = 0; i < robotIds.length; i++){
			ids.add(robotIds[i]);
		}
		soundMap.put("ids", ids);
		soundMap.put("sound", sound);
		try {
			out.writeObject(soundMap);
		}
		catch (IOException e) {e.printStackTrace();}
	}
}








