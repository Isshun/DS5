package alone.in.deepspace;
import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Create the window
		RenderWindow window = new RenderWindow();
		window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "DS5");

		try {
			Game game = new Game(window);
			game.load("saves/2.sav");
			game.loop();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//
//		//Limit the framerate
//		window.setFramerateLimit(30);
//
//		//Main loop
//		while(window.isOpen()) {
//		    //Fill the window with red
//		    window.clear(Color.RED);
//
//		    //Display what was drawn (... the red color!)
//		    window.display();
//
//		    //Handle events
//		    for(Event event : window.pollEvents()) {
//		        if(event.type == Event.Type.CLOSED) {
//		            //The user pressed the close button
//		            window.close();
//		        }
//		    }
//		}
	}

}
