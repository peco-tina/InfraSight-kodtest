package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.infrasight.kodtest.service.AccountService;
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
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@Test
	public void assignment1() throws InterruptedException {
		String employeeId = "1337";
		AccountService accountService = new AccountService();
		assertTrue(serverUp);
		accountService.findAccountById(getHttpClientBuilder(), employeeId);

		/**
		 * TODO: Add code to solve the first assignment. Add Assert to show that you
		 * found the account for Vera
		 */
	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the second assignment where we expect the number of
		 * groups to be 3.
		 */
		int groupCount = 0;

		// Assert which verifies the expected group count of 3
		assertEquals(3, groupCount);

		/**
		 * TODO: Add Assert to verify the IDs of the groups found
		 */
	}

	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the third assignment. Add Assert to verify the
		 * expected number of groups. Add Assert to verify the IDs of the groups found.
		 */
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fourth assignment. Add Asserts to verify the
		 * total salary requested
		 */
	}

	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fifth assignment. Add Asserts to verify the
		 * managers requested
		 */
	}
}
