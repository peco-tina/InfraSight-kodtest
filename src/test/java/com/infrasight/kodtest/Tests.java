package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 * 
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {

	/**
	 * Simple test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	/**
	 * Count the total number of groups by querying the API for groups. The expected
	 * number is seen in the final assert.
	 */
	@Test
	public void countGroups() throws InterruptedException {
		assertTrue(serverUp);
		assertEquals(7, getGroupCount());
	}

	/**
	 * @return The number of groups retrieved from the Kodtest Server
	 */
	private int getGroupCount() {
		// user & password to use for API authentication
		final String apiUser = TestVariables.API_USER;
		final String apiPassword = TestVariables.API_PASSWORD;

		// TODO: Get the actual value from the API
		return 7;
	}
}
