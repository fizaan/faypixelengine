# faypixelengine

A Java Swing based game engine

- What is it?

A Java game engine that uses swing and can be used to develop interactive games. The idea for this project was inspired by the excellent tutorials of OneLoneCoder and his
famous olcpixelgameengine. Please checkout his youtube channel at Javidx9. I would also like to attribute this work to the great Daniel Shiffman - The Coding Train which
inspired me as well. FayPixelEngine is no where close to olcpixelengine (which is written in C++) but is close enough to get you started with game development in Java. I hope
this tool will be useful for beginners as well as intermediate developers alike.

- Technical requirements

This project was tested with Java v13 on Windows 10, but it should run with 1.8 as well. You will need Maven Apache Maven 3+.

- Setup

Import this repo then run the src/helloworld examples to get started. Please see the bottom for compiling and running using maven
Essentially, here is all you need to do:

```
package helloworld;

import java.awt.Color;

import fay.original.FaySansBuffer;

@SuppressWarnings("serial")
public class HelloWorldSkeleton extends FaySansBuffer {

	public HelloWorldSkeleton(int len, int hei, int pX, int pY, String title) {

	 super(len,hei,pX,pY,title);

	}

	public static void main(String args[]) {

	 new HelloWorldSkeleton(400,400,1,1, "Bare bones");

	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {

		clearConsole(Color.BLUE);

		return true;
	}

	@Override
	public boolean OnUserCreate() {
		return true;
	}

}
```

...then compile run the example from your command line prompt (if you prefer) using Maven

```
prompt>PATHTO\faypixelengine\FayPixelEngine>mvn clean

prompt>PATHTO\faypixelengine\FayPixelEngine>mvn compile

prompt>PATHTO\faypixelengine\FayPixelEngine>mvn exec:java -Dexec.mainClass="helloworld.HelloWorld" -Dexec.classpathScope=runtime

prompt>PATHTO\faypixelengine\FayPixelEngine>mvn exec:java -Dexec.mainClass="helloworld.HelloWorldSkeleton" -Dexec.classpathScope=runtime
```
