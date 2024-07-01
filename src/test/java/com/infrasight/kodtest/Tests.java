package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.infrasight.kodtest.model.Account;
import com.infrasight.kodtest.model.Relationship;
import com.infrasight.kodtest.service.AccountService;
import com.infrasight.kodtest.service.AuthenticationService;
import com.infrasight.kodtest.service.RelationshipService;
import okhttp3.OkHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 * 
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {
	private static final AuthenticationService authenticationService = new AuthenticationService();
	private static final OkHttpClient okHttpClient = getHttpClientBuilder().build();
	private static final AccountService accountService = new AccountService(okHttpClient);
	private static final RelationshipService relationshipService = new RelationshipService(okHttpClient);
	private static List<Relationship> allRelationships;

	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@BeforeClass
	public static void configure(){
		authenticationService.getBearerToken(okHttpClient);
		allRelationships = new ArrayList<>();
	}

	@Test
	public void assignment1() throws InterruptedException {
		assertTrue(serverUp);
		String employeeId = "1337";
		Account account = accountService.findAccountById(employeeId, null);
		assertEquals("Vera", account.getFirstName());
		assertEquals("Scope", account.getLastName());
	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);
		allRelationships.addAll(relationshipService.getRelationships("vera_scope", null, null));

		assertEquals(3, allRelationships.size());
		assertEquals("grp_köpenhamn", allRelationships.get(0).getGroupId());
		assertEquals("grp_malmo", allRelationships.get(1).getGroupId());
		assertEquals("grp_itkonsulter", allRelationships.get(2).getGroupId());
	}

	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);
		List<String> processedGroupIds = new ArrayList<>();
		allRelationships = relationshipService.getAllRelationshipsByMemberIds(allRelationships, processedGroupIds);

		assertEquals(9, allRelationships.size());
		assertEquals(allRelationships.get(0).getGroupId(), ("grp_köpenhamn"));
		assertEquals(allRelationships.get(1).getGroupId(), ("grp_malmo"));
		assertEquals(allRelationships.get(2).getGroupId(), ("grp_itkonsulter"));
		assertEquals(allRelationships.get(3).getGroupId(), ("grp_danmark"));
		assertEquals(allRelationships.get(4).getGroupId(), ("grp_sverige"));
		assertEquals(allRelationships.get(5).getGroupId(), ("grp_inhyrda"));
		assertEquals(allRelationships.get(6).getGroupId(), ("grp_chokladfabrik"));
		assertEquals(allRelationships.get(7).getGroupId(), ("grp_choklad"));
		assertEquals(allRelationships.get(8).getGroupId(), ("grp_konfektyr"));
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);
		List<String> membersId = new ArrayList<>();
		List<Account> accounts = new ArrayList<>();
		membersId.addAll(relationshipService.getAllMembersID("grp_inhyrda", null, null));
		accounts.addAll(accountService.getAllAccountsById(membersId));

		System.out.println(membersId.size());
		System.out.println(accounts.size());

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
