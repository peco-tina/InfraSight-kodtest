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
	public void assignment1() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the first assignment, the correct answer is provided
		 */

		assertEquals(3, getGroupCount());
	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the second assignment, your answer shall be provided
		 * in a assertEquals
		 */
	}

	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);
		/**
		 * TODO: Add code to solve the third assignment, your answer shall be provided
		 * in a assertEquals
		 */
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fourth assignment, your answer shall be provided
		 * in a assertEquals
		 */
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
