package uk.ac.hud.cryptic.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfidenceTest {

	private static final double DELTA = 1e-15;

	@Test
	public void testMultiply() {
		// Lower bound test - 0 should be minimum
		assertEquals(0d, Confidence.multiply(0d, 0d), DELTA);
		assertEquals(0d, Confidence.multiply(0d, -100d), DELTA);
		assertEquals(0d, Confidence.multiply(0d, 100d), DELTA);
		assertEquals(0d, Confidence.multiply(-100d, 2d), DELTA);

		// Upper bound test - 100 should be maximum
		assertEquals(100d, Confidence.multiply(1d, 200d), DELTA);
		assertEquals(100d, Confidence.multiply(100d, 100d), DELTA);
		assertEquals(100d, Confidence.multiply(-1d, -100d), DELTA);
		assertEquals(100d, Confidence.multiply(50d, 3d), DELTA);

		// Typical values
		assertEquals(80d, Confidence.multiply(40d, 2d), DELTA);
		assertEquals(20d, Confidence.multiply(40d, 0.5d), DELTA);
		assertEquals(12.5d, Confidence.multiply(100d, 0.125d), DELTA);

		// Right on the bound
		assertEquals(0d, Confidence.multiply(0d, 0d), DELTA);
		assertEquals(100d, Confidence.multiply(100d, 1d), DELTA);
	}

} // End of class ConfidenceTest
