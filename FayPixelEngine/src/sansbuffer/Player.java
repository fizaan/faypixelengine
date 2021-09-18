package sansbuffer;

import java.util.Map;

import sansbuffer.JMario.JMarioRay;
import tests.Ball;

public class Player extends Ball {
	
	// Suggested Initial values:
	// constVel 			= 0.5f
	// constAcc 			= 0.005
	// varyAcc 				= 0.3f
	// startingXPosition 	= 5;
	// radius 				= 20;
	// gravity 				= 0.003f or 0.03f;
	// fAngleInc			= 0.05;
	// jumpAngle 			= 270
	// movingRight 			= false;
	// constantVel 			= false;
	// constantAcc 			= false;
	// fXSpeedAtJump		= 1.2f;
	//
	// :::::::::::::: COLLISIONS ::::::::::::::::::
	// These are determined at runtime using ray casting
	// float intGroundLevel 		
	// float eastBoundCollisionPoint
	// float westBoundCollisionPoint
	// float northBoundCollisionPoint
	// float collisionInfo
	
	private float fJumpAngle, fConstVel, fConstAcc, fVaryAcc, fAngleInc, fXSpeedAtJump;
	private boolean bMovingRight, bConstantVel, bConstantAcc;
	private float intGroundLevel, intStartingXPosition, 
		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		// float collision points
		eastBoundCollisionPoint, westBoundCollisionPoint, northBoundCollisionPoint,
		southEastBoundCollisionPoint, northEastBoundCollisionPoint, 
		southWestBoundCollisionPoint, northWestBoundCollisionPoint;
		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private String collisionInfo;
	public static final int MAX_ANGLE = 360;
	public static final int INIT_JUMP_ANGLE = 270;
	
	public Player() 
	{
		super(0,0);
	}
	
	public Player(float x, float y) {
		super(x, y);
	}
	
	public Player(float x, float y, float speedx, float speedy) {
		super(x, y, speedx, speedy);
	}
	
	public void incAngle() { 
		fJumpAngle += fAngleInc; 
		if(fJumpAngle >= MAX_ANGLE) fJumpAngle = MAX_ANGLE;
	}
	
	public void decAngle() { 
		fJumpAngle -= fAngleInc; 
		if(fJumpAngle <= 0) fJumpAngle = 0;
	}
	
	// :::::::::::::::::::::: SET/GET Collision points:::::::::::::::::::::::::
	public float groundPosition() { return intGroundLevel; }
	public void setGroundPosition(float g) { intGroundLevel = g; }
	
	public float eBoundCP() { return eastBoundCollisionPoint; }
	public void setEBoundCP(float eastBoundCollisionPoint) { this.eastBoundCollisionPoint = eastBoundCollisionPoint; }
	
	public float wBoundCP() { return westBoundCollisionPoint; }
	public void setWBoundCP(float westBoundCollisionPoint) { this.westBoundCollisionPoint = westBoundCollisionPoint; }
	
	public float nBoundCP() { return northBoundCollisionPoint; }
	public void setNBoundCP(float northBoundCollisionPoint) { this.northBoundCollisionPoint = northBoundCollisionPoint; }
	
	public float neBoundCP() { return northEastBoundCollisionPoint; }
	public void setNEBoundCP(float northEastBoundCollisionPoint) { this.northEastBoundCollisionPoint = northEastBoundCollisionPoint; }
	
	public float nwBoundCP() { return northWestBoundCollisionPoint; }
	public void setNWBoundCP(float northWestBoundCollisionPoint) { this.northWestBoundCollisionPoint = northWestBoundCollisionPoint; }
	
	public float seBoundCP() { return southEastBoundCollisionPoint; }
	public void setSEBoundCP(float southEastBoundCollisionPoint) { this.southEastBoundCollisionPoint = southEastBoundCollisionPoint; }
	
	public float swBoundCP() { return southWestBoundCollisionPoint; }
	public void setSWBoundCP(float southWestBoundCollisionPoint) { this.southWestBoundCollisionPoint = southWestBoundCollisionPoint; }
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	
	public String collisionInfo() { return collisionInfo; }
	public void resetCollisionInfo() { collisionInfo = null; }
	public float startXPosition() { return intStartingXPosition; }
	public boolean movingRight() { return bMovingRight; }
	public void setDirBoolean(boolean bMovingRight) { this.bMovingRight = bMovingRight; }
	public boolean direction() { return bMovingRight; } 
	public void setJumpAngle(float fJumpAngle) { this.fJumpAngle = fJumpAngle; }
	public float angle() { return fJumpAngle; }
	public float fXSpeedAtJump() { return fXSpeedAtJump; }
	
	public void initFloats(float fJumpAngle, float fConstVel, 
			float fConstAcc, float fVaryAcc, float fAngleInc, float fGravity, float fXSpeedAtJump) {
		this.fJumpAngle = fJumpAngle;
		this.fConstVel = fConstVel;
		this.fConstAcc = fConstAcc;
		this.fVaryAcc = fVaryAcc;
		this.fAngleInc = fAngleInc;
		this.fXSpeedAtJump = fXSpeedAtJump;
		setGravity(0, fGravity);
		
		// if bConstantVel is false then we are using
		// acceleration - i.e. changing velocity and this
		// velocity should start with 0
		if(!bConstantVel) fConstVel = 0;
	}
	
	public void initInts(int intGroundLevel, int intStartingXPosition) {
		this.intGroundLevel = intGroundLevel;
		this.intStartingXPosition = intStartingXPosition;
	}
	
	public void initMotionBooleans(boolean bConstantVel, boolean bConstantAcc) {
		this.bConstantVel = bConstantVel;
		this.bConstantAcc = bConstantAcc;
	}
	
	private void fly(int w, int h) {
		// mid-air collision checks
		if(location.x <= 0 && !bMovingRight)
			velocity.x *= -1;
		else if(location.x + radius() >= w && bMovingRight)
			velocity.x *= -1;
		else if(location.y <= 0)
			velocity.y *= -1;
		
		updateWithGravity(false);
	}
	
	private void fly() {
		// mid-air collision checks
		
		// WB collision
		if(location.x <= westBoundCollisionPoint && !bMovingRight) {
			// make player bounce off the westbound 'collision' in mid-air
			collisionInfo = "Arbourne WB-Collision";
			velocity.x *= -1;
			fJumpAngle = INIT_JUMP_ANGLE;
		}
		
		// EB collision
		else if(location.x + radius() >= eastBoundCollisionPoint && bMovingRight) {
			// make player bounce off the eastbound 'collision' in mid-air
			collisionInfo = "Arbourne EB-Collision";
			velocity.x *= -1;
			fJumpAngle = INIT_JUMP_ANGLE;
		
		// Check SE NE SW NW Collisions	
		// 	- SE/SW: collision y must be > player y when flying in right/left direction
		// 	- NE/NW: collision y must be < player y when flying in right/left direction
		// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
		} else if(location.y + radius() >= southEastBoundCollisionPoint && bMovingRight) {
			// make player bounce off the south east bound 'collision' in mid-air.
			// mario is falling in the SE direction so the CP y must b > mario's y
			// else we have a SE bound collision
			collisionInfo = "Arbourne SEB-Collision";
			velocity.x *= -1;
			fJumpAngle = INIT_JUMP_ANGLE;
		} else if(location.y + radius() <= northEastBoundCollisionPoint && bMovingRight) {
			// make player bounce off the north east bound 'collision' in mid-air
			// mario is climbing in the NE direction so the CP y must b < mario's y
			// else we have a NE bound collision
			collisionInfo = "Arbourne NEB-Collision";
			velocity.x *= -1;
			fJumpAngle = INIT_JUMP_ANGLE;
		} else if(location.y + radius() >= southWestBoundCollisionPoint && !bMovingRight) {
			// make player bounce off the south west bound 'collision' in mid-air
			// mario is falling in the SW direction so the CP y must b > mario's y
			// else we have a SW bound collision
			collisionInfo = "Arbourne SWB-Collision";
			velocity.x *= -1;
			fJumpAngle = INIT_JUMP_ANGLE;
		} else if(location.y + radius() <= northWestBoundCollisionPoint && !bMovingRight) {
			// make player bounce off the north west bound 'collision' in mid-air
			// mario is climbing in the Nw direction so the CP y must b < mario's y
			// else we have a Nw bound collision
			collisionInfo = "Arbourne NWB-Collision";
			velocity.x *= -1;
			fJumpAngle = INIT_JUMP_ANGLE;
		}
		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		
		// here? we have reached the ceiling!
		// NB Collision
		else if(location.y - 1 <= northBoundCollisionPoint) {
			// make player bounce off the northbound 'collision' in mid-air
			collisionInfo = "Arbourne NB-Collision";
			if(velocity.y < 0) velocity.y *= -0.01;
			fJumpAngle = INIT_JUMP_ANGLE;
		}
		
		updateWithGravity(false);
	}
	
	private void cruise() { 
		if(checkBounds()) 
			location.add(velocity);
		else {
			// make player bounce off the eastbound or westbound 'collision' while on ground
			velocity.x *= -1;
			velocity.add(acceleration);
			location.add(velocity); 
		}
	}
	
	private void cruise(int w, int h) { 
		if(checkBounds(w,h)) 
			location.add(velocity);
		else {
			// make player bounce off the eastbound or westbound 'collision' while on ground
			velocity.x *= -1;
			velocity.add(acceleration);
			location.add(velocity); 
		}
	}
	
	private void accelerate() {
		if(checkBounds()) {
			velocity.add(acceleration);
			location.add(velocity); 
		} else {
			// make player bounce off the eastbound or westbound 'collision' while on ground 
			velocity.x *= -1;
			velocity.add(acceleration);
			location.add(velocity); 
		}
	}
	
	private void accelerate(int w, int h) {
		if(checkBounds(w,h)) {
			velocity.add(acceleration);
			location.add(velocity); 
		} else {
			// make player bounce off the eastbound or westbound 'collision' while on ground 
			velocity.x *= -1;
			velocity.add(acceleration);
			location.add(velocity); 
		}
	}
	
	// grounded collision checks
	private boolean checkBounds() {
		if(location.x <= westBoundCollisionPoint && !bMovingRight) {
			collisionInfo = "Ground WB-Collision";
			fJumpAngle = INIT_JUMP_ANGLE;
			return false;
		}
		else if(location.x + radius() >= eastBoundCollisionPoint && bMovingRight) {
			collisionInfo = "Ground EB-Collision";
			fJumpAngle = INIT_JUMP_ANGLE;
			return false;
		}
		else 
			return true;
	}
	
	private boolean checkBounds(int w, int h) {
		if(location.x <= 0 && !bMovingRight)
			return false;
		else if(location.x + radius() >= w && bMovingRight)
			return false;
		else 
			return true;
	}
	
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// function airbourne()
	// mario is airborne when his y position is less than or equal to a SB Collision point or any 
	// ground tile. if for whatever reason mario goes below the ground position or SB collision 
	// point then this will return false. In other words, mario is airborne only when his y 
	// position is above the ground or SB Collision point. For example, when you press the D and 
	// then J key (jumping while moving right) and then release the J key, then mario begins to 
	// descend,...and at some point he hits the ground or SB collision point - then this check 
	// WILL RETURN FALSE if he crosses/goes past that SB Collision point because y becomes > 
	// intGroundLevel.
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public boolean airbourne() {
		return location.y + radius() <= intGroundLevel;
	}
	
	private boolean airbourne(Map<Integer, JMarioRay> rays) {
		boolean e = rays.get(JMarioRay.E_BOTTOM_RIGHT).collided;
		boolean f = rays.get(JMarioRay.F_BOTTOM).collided;
		boolean g = rays.get(JMarioRay.G_BOTTOM_LEFT).collided;
		return !e && !f && !g;
	}
	
	public void moveX(float fElapsedTime) {
								// only set x and leave y as is. Setting y to 0 will 
								// not make him jump if using constant velocity. also
								// note that there is a bug when bConstantVel is true
								// in that the jump is jittery. Look into it.
		if(bConstantVel) 		velocity.x = bMovingRight ? fConstVel : -fConstVel;
		
		else if (bConstantAcc) 	acceleration.setPosition(bMovingRight ? fConstAcc : -fConstAcc, 0);
		
		else					acceleration.setPosition(bMovingRight ? fVaryAcc * fElapsedTime : -fVaryAcc * fElapsedTime, 0);
	}
	
	public void reset() {
		location.setPosition(intStartingXPosition, intGroundLevel);
		velocity.setPosition(0, 0);
		acceleration.setPosition(0, 0);
		resetCollisionInfo();
	}
	
	// Used by MarioBall
	public void updatePosition(int w, int h) {
		if(airbourne())			fly(w,h);
		else if (bConstantVel) 	cruise(w,h);
		else 					accelerate(w,h);
	}
	
	// Used by JMario
	public void updatePosition() {
		if(airbourne())			fly();
		else if (bConstantVel) 	cruise();
		else 					accelerate();
	}
	
	private void updatePosition(boolean airbourne) {
		if(airbourne)			fly();
		else if (bConstantVel) 	cruise();
		else 					accelerate();
	}

}
