package com.infrasight.kodtest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConnectionTest extends AbstractKodTest {

	/**
	 * Simple test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}
}
