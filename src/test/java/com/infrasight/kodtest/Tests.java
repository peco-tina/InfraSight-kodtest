package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.infrasight.kodtest.model.Account;
import com.infrasight.kodtest.model.Relationship;
import com.infrasight.kodtest.service.AccountService;
import com.infrasight.kodtest.service.AuthenticationService;
import com.infrasight.kodtest.service.RelationshipService;
import okhttp3.OkHttpClient;
import org.junit.Test;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 * 
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {
	AuthenticationService authenticationService = new AuthenticationService();
	OkHttpClient okHttpClient = getHttpClientBuilder().build();
	AccountService accountService = new AccountService(okHttpClient);
	RelationshipService relationshipService = new RelationshipService(okHttpClient);

	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
		configure();
	}

	public void configure(){
		authenticationService.getBearerToken(okHttpClient);
	}

	@Test
	public void assignment1() throws InterruptedException {
		assertTrue(serverUp);
		String employeeId = "1337";
		Account account = accountService.findAccountById(employeeId);
		assertEquals("Vera", account.getFirstName());
		assertEquals("Scope", account.getLastName());
	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);
		String memberId = "vera_scope";
		Relationship [] relationships = relationshipService.getAllRelationshipsByMemberId(memberId);

		assertEquals(3, relationships.length);
		assertEquals("grp_köpenhamn", relationships[0].getGroupId());
		assertEquals("grp_malmo", relationships[1].getGroupId());
		assertEquals("grp_itkonsulter", relationships[2].getGroupId());
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
