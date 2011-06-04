package ephemera.model;
/*
Copyright 2007 Jason Fuerstenberg

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import java.lang.Math;

/**
* Utility class for exposing FPS related functionality to a J2ME application.
*
* @author Jason Fuerstenberg
*/
public class FPS {

/*
 * The millisecond timestamp of the current render frame if used.
 */
private static long frameTime;

private FPS() {
	// singleton implementation
}

/**
 * Returns the best delay given the specified FPS.
 * The delay is used in application loops and is typically provided to the
 * Thread.sleep() method.
 *
 *
 * The basic formula for this calcuation is as below:
 *
 * delay = 1000 / fps
 *
 * The delay in millseconds, is the product of 1000 milliseconds divided
 * by the desired FPS.
 *
 * Note: To be compatible with the CLDC 1.0 specification this method
 * makes no use of floating point math and therefore the returned delay
 * is always an integral number and may not be the precise result.  The
 * results are precise to within 0.5ms.
 *
 * For example an FPS of 3 should produce a delay of 333.33333ms
 * however this method will return a delay of 333ms.
 *
 * Alternately an FPS of 6 would produce a 166.66666ms delay.  The actual
 * returned delay is 167ms.
 *
 * @param fps The desired frames per second.
 * @return A delay (in milliseconds) within 0.5ms accuracy.
 * @throws IllegalArgumentException if fps does not fall within 1-60.
 */
public static int getDelayFromFPS(int fps) {

	if (fps < 1 || fps > 60) {
		// invalid fps
		throw new IllegalArgumentException("fps must range from 1 to 60.");
	}

	// determine the delay
	int delay = 1000 / fps;

	// determine the duration of a cycle if the above delay were applied
	int cycleDuration = delay * fps;

	if (cycleDuration == 1000) {
		/*
		 * Found the perfect delay!
		 * Return it now.
		 */
		return delay;
	}

	/*
	 * The calculated delay, if applied, will not result in the desired FPS.
	 * Find the delay which will produce the closest FPS cycle from the
	 * following choices:
	 *
	 * 1. the calculated delay
	 * 2. the calculated delay - 1ms
	 * 3. the calculated delay + 1ms
	 */
	int delayMinusOne = delay - 1;
	int cycleDuration2 = delayMinusOne * fps;

	int delayPlusOne = delay + 1;
	int cycleDuration3 = delayPlusOne * fps;

	int diff1, diff2, diff3;

	diff1 = Math.max(cycleDuration, 1000) - Math.min(cycleDuration, 1000);
	diff2 = Math.max(cycleDuration2, 1000) - Math.min(cycleDuration2, 1000);
	diff3 = Math.max(cycleDuration3, 1000) - Math.min(cycleDuration3, 1000);

	if (diff1 < diff2) {
		return (diff1 < diff3 ? delay : delayPlusOne);
	}

	return (diff2 < diff3 ? delayMinusOne : delayPlusOne);
}

/**
 * Enforces a cycle delay on behalf of the application while using
 * an internally managed frame time.
 *
 * @param delay The delay to apply to this cycle.
 */
public static void enforceCycleDelay(int delay) {
	enforceCycleDelay(frameTime, delay);
}

/**
 * Enforces a cycle delay on behalf of the application.
 *
 * @param frameTime The millisecond timestamp of the current frame's
 * 			start time.
 * @param delay The delay to apply to this cycle.
 */
public static void enforceCycleDelay(long frameTime, int delay) {

	long sleepTime = delay - (System.currentTimeMillis() - frameTime);

	if (sleepTime > 0) {

		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException ie) {
			/*
			 * There is no appropriate course of action to recover from
			 * an InterruptedException and therefore only the fact that it
			 * occurs is output to the stack trace.
			 */
			ie.printStackTrace();
		}
	}

	// Set the current frame-time.
	FPS.frameTime = System.currentTimeMillis();
}

/**
 * Returns the interally managed frame-time.
 * The value may or may not be set depending on the previous usage of
 * this class.
 *
 * @return The internal frame-time.
 */
public static long getFrameTime() {
	return frameTime;
}
}