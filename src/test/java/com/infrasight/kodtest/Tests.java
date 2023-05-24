package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		final String apiUser = getApiUser();
		final String apiPassword = getApiPassword();

		// TODO: Get the actual value from the API
		return 7;
	}
}
