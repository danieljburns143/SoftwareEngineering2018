package application.model.vehicles;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import application.model.infrastructure.gate.CrossingGate;
import application.view.CarImageSelector;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/**
 * Represents Car object
 * @author jane
 *
 */
public class Car extends Observable implements IVehicle, Observer{
	private ImageView ivCar;
	private double currentX = 0;
	private double currentY = 0;
	private double originalY = 0;
	private boolean gateDown = false;
	private double leadCarY = -1;  // Current Y position of car directly infront of this one
	private double speed = 0.5;
	Random r = new Random();
	
	public int directionState;
		
	/**
	 * Constructor
	 * @param x initial x coordinate of car
	 * @param y initial y coordinate of car
	 */
	public Car(int x, int y){
		this.currentX = x;
		this.currentY = y;
		originalY = y;
		ivCar = new ImageView(CarImageSelector.getImage());
		ivCar.setX(getVehicleX());
		ivCar.setY(getVehicleY());
		directionState = r.nextInt(2);
	}
		
	@Override
	public Node getImageView() {
		return ivCar;
	}
	
	public boolean gateIsClosed(){
		return gateDown;
	}
	
	public double getVehicleX(){
		return currentX;
	}
	public double getVehicleY(){
		return currentY;
	}
	
	public void move(){
		boolean canMove = true; 
		
		// First case.  Car is at the front of the stopping line.
		if (gateDown && getVehicleY() < 430 && getVehicleY()> 390)
			canMove = false;
		
		// Second case. Car is too close too other car.
		if (leadCarY != -1  && getDistanceToLeadCar() < 150)
			canMove = false;
		
		if (canMove){
			if (directionState == 0) {
				currentY+=speed;
				ivCar.setY(currentY);
			}
			else if (directionState == 1) {
				if (currentY <= 690) {
					currentY+=speed;
					ivCar.setY(currentY);
				}
				else {
					if (currentX < 600) {
						directionState = 2;
					}
					else {
						directionState = 3;
					}
				}
			}
			if (directionState == 2) {
				if (currentX < 790) {
					currentX+=speed;
					ivCar.setX(currentX);
				}
				else {
					directionState = 0;
				}
			}
			if (directionState == 3) {
				if (currentX > 390) {
					currentX-=speed;
					ivCar.setX(currentX);
				}
				else {
					directionState = 0;
				}
			}
		}
		
		setChanged();
		notifyObservers();
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	
	public void setGateDownFlag(boolean gateDown){
		this.gateDown = gateDown;
	}
	
	public boolean offScreen(){
		if (currentY > 1020)
			return true;
		else
			return false;			
	}
		
	public void reset(){
		currentY = originalY;
	}
	
	public double getDistanceToLeadCar(){
		return Math.abs(leadCarY-getVehicleY());
	}
	
	public void removeLeadCar(){
		leadCarY = -1;
	}

	@Override
	public void update(Observable o, Object arg1) {
		if (o instanceof Car){
			leadCarY = (((Car)o).getVehicleY());
			if (leadCarY > 1020)
				leadCarY = -1;
		}
			
		if (o instanceof CrossingGate){
			CrossingGate gate = (CrossingGate)o;
			if(gate.getTrafficCommand()=="STOP")			
				gateDown = true;
			else
				gateDown = false;
					
		}				
	}	
}
