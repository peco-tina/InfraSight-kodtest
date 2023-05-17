package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GroupCountTest extends KodTest {

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
		// TODO: Get the actual value from the API
		return 0;
	}
}
