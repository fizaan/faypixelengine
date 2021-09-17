Download the files from https://github.com/meth-meth-method/super-mario/releases/tag/ep1

Follow along this video by Meth Meth Method called 'Super Mario in JS'

https://www.youtube.com/watch?v=g-FpDQ8Eqw8

Understanding the json layout:

// "title" :	"name",x,len,y,hei 

{
    "backgrounds": [
        {
            // draw sky tiles starting at position 0,0
            // and len of 25 units and hei of 14 units
            // 1 unit = 1 tile size
            "tile": "sky,0,0,25,14"
        },
        {	// draw ground tiles starting at 0,12
        	// len of 25 and hei of 2
            "tile": "ground,0,12,25,2"
        },
        {	// draw ground tiles starting at 8,8
        	// len of 2 and hei of 1
            "tile": "ground,8,8,2,1"
        },
        {	// draw ground tiles starting at 18,5
        	// len of 1 and hei of 5
            "tile": "ground,18,5,1,5"
        }
    ]
}

json: 
	 (0,0) +------------------------+ (0+40,0)
	 	   +						+
		   +						+
		   +	sky					+
		   +						+
		   +------------------------+ (0+40,0+16)
		   +	ground				+
		   +------------------------+ (40-0,0+16+2)	

Frame rate with ITER: ~ 400
Frame rate w/o ITER: ~ 500

Part 2 - drawing mario | start at 7 mins. At the end he creates a mario 'army'
which I don't do. It's silly imo.
https://www.youtube.com/watch?v=FF93S8rLL_Q

Part 3 - Timing - start at 12 mins.

	 // define mario's starting position at 64,64 going down
	 // at a 45 deg angle
	 // mario = new Ray(64,64,1.0f,StaticHelper.degToRad(45));
	 
Note: I couldn't figure out the accumulated time from his video even when I did implement
it correctly. I read that we should not use float as counters variables.
"Do not use floating-point variables as loop counters" 
	https://wiki.sei.cmu.edu/
	https://stackoverflow.com/questions/16595668
	
This is what I tried:
private int deltaTime = 10;
private int accumulatedTime = 0;
	 
public boolean OnUserUpdate(float fElapsedTime) {
...
	accumulatedTime += (int)(fElapsedTime * 1000);
		 while(accumulatedTime > deltaTime) {
			 drawMario(fElapsedTime);
			 accumulatedTime -= deltaTime;
		 }
}
	
I will leave it at this. No accumulatedTime implementation for me.

Part 4 - Adding gravity and updating mario position before drawing.

At (64,180) Mario overlaps part of the ground in that his feet are digging in the
ground tiles. Why? The sky occupies 0-14 tiles in height so 16 * 14 = 224 pixels. The
ground occupies 12-14 tiles so 2 * 16 = 32 pixels. That leaves 192 pixels b/w
the sky and ground. Since mario's height is 16 pixels where should I place him?
192 - 16 = at 176 Y-pixels. However, he puts Mario at 180 pixels which is definitely
not correct. Hence I changed his position to (64,176).

Healthy FPS should be b/w 150 to 170. Sometimes FPS falls below 100. Watch out when
that happens and figure out why. Is it something to do with createStatusWindow(300, 200)?
Actually it's to do with your laptop not plugged in or on low battery.

Original settings:
- mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.3f, 0.05f, 0.05f);

Updated settings:
- mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.3f, 0.05f, 0.07f);
- mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.85f, 0.05f, 0.07f);
- mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.85f, 0.05f, 0.03f);
- mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.85f, 0.05f, 0.03f, 1.2f);

// sept 3 2021
// new function: setAngledPosition(float angleDeg, float fXSpeedAtJump)
// 
// when mario jumps, his x velocity at the time of jump should 
// be relative to his ground x speed just before the jump. Hence why
// I made this function. This function is called when pressing the d+j, s+j
// keys. The value I chose for fXSpeedAtJump is for the divisor value
// of 1.2f
//
//			- D+J
//			- S+J
public void setAngledPosition(float angleDeg, float fXSpeedAtJump) {
 Vf2d v = clone();
 setPosition(
  (float) (Math.cos(StaticHelper.degToRad(angleDeg))), 
  (float) (Math.sin(StaticHelper.degToRad(angleDeg))));
 x += v.x / fXSpeedAtJump;
}

Part 5: Tile Collisions
This was a weird episode. I didn't get anything. May need to implement my own tile 
collision or watch some other YT.
	
	The problem 1
	-------------
	- shiffman/StaticHelper.java
	------------
	Have a look at this code, there is a bug in showMarioRays() for this
	condition. The ray is cast from the top middle (B) of mario tile to
	the ground - (See your hand written notes for reference) - but...if 
	there is no intersect point then y - p.radius will be -16! Hence why
	mario never drops down once it has crossed the N-Bound wall/border 
	
	...
	case JMarioRay.B_TOP:        p.setGroundPosition(ray.intersectPoint.y - p.radius());	break;
	
	The solution
	------------
	Set ground position only when ray cast difference is > 0. Else, the
	ground position will be whatever it was prior. This will make sure 
	that mario doesn't fly out of the top roof window.
	
	...
	case JMarioRay.B_TOP: 
	   float diff = ray.intersectPoint.y - p.radius();
	   if(diff > 0) p.setGroundPosition(ray.intersectPoint.y - p.radius()); 	break;
	   
	The problem 2
	------------
	- sansbuffer/Player.java
	------------
	The fly() method has a bug. As mario is jumping, when he collides with 
	a NB wall the NB collision point/wall gets "updated". And also, when he
	collides with the NB wall/barrier, his velocity should always increase
	not decrease (because he's supposed to descend).
	
	...
	else if(location.y <= northBoundCollisionPoint) {
		// make player bounce off the northbound 'collision' in mid-air
		// this isn't working IDK why.
		collisionInfo = "Arbourne NB-Collision";
		velocity.y *= -1;
	}
	...
	
	The solution
	------------
	Can I lock the loction.y? I don't think that will work.
	
	Here's what I came up with. Reduce the location.y point by
	1 and compare it with the NB collision point. If there is
	breach, reverse the direction of the velocity (by a small amount). 
	But this doesn't bounce off mario from the NB Collision like EB and
	WB collisions do. Why? I have NO clue! Need to find this out.
	
	...
	else if(location.y - 1 <= northBoundCollisionPoint) {
		// make player bounce off the northbound 'collision' in mid-air
		collisionInfo = "Arbourne NB-Collision";
		if(velocity.y < 0) velocity.y *= -1;
	}
	...
	
	
	Observation 1:
	-----------
	
	- sansbuffer.Player
	
	- What causes Mario to bounce off EB and WB collisions while grounded?
	This code here:
	
	...
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
	...
	
	- What causes Mario to bounce off EB and WB collisions while airborne?
	The 'fly()' function
	But..this only works when you press the D+J keys and then release the J key
	just before the collision. If you don't release the J key then mario will "stick" 
	to the collided barrier and move upwards.
	
- E6 Scrolling
	
