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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List<String> membersId = new ArrayList<>(relationshipService.getAllMembersID("grp_inhyrda", null, null));
		List<Account> accounts = new ArrayList<>(accountService.getAllAccountsById(membersId));
		Map<String, Long> salaries = new HashMap<>(accountService.calculateSalaries(accounts));

		long totalSalariesInSEK = salaries.get("SEK");
		long totalSalariesInDKK = salaries.get("DKK");
		long totalSalariesInEUR = salaries.get("EUR");

		assertEquals(16029792, totalSalariesInSEK);
		assertEquals(1843283, totalSalariesInDKK);
		assertEquals(535726, totalSalariesInEUR);
	}

	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);
		List<String> saljareMembersId = new ArrayList<>(relationshipService.getAllMembersID("grp_saljare", null, null));
		List<String> malmoMembersId = new ArrayList<>(relationshipService.getAllMembersID("grp_malmo", null, null));
		List<String> stockholmMembersId = new ArrayList<>(relationshipService.getAllMembersID("grp_stockholm", null, null));
		List<String> goteborgMembersId = new ArrayList<>(relationshipService.getAllMembersID("grp_goteborg", null, null));
		List<String> cheferMembersId = new ArrayList<>(relationshipService.getAllMembersID("grp_chefer", null, null));
		List<String> allSwedishMembersId = new ArrayList<>();

		allSwedishMembersId.addAll(malmoMembersId);
		allSwedishMembersId.addAll(stockholmMembersId);
		allSwedishMembersId.addAll(goteborgMembersId);

		//Lines bellow are used to separate säljare from other employees by Region
		List<String> saljareFromMalmoMembersId = new ArrayList<>(saljareMembersId);
		saljareFromMalmoMembersId.retainAll(malmoMembersId);

		List<String> saljareFromStockholmMembersId = new ArrayList<>(saljareMembersId);
		saljareFromStockholmMembersId.retainAll(stockholmMembersId);

		List<String> saljareFromGoteborgMembersId = new ArrayList<>(saljareMembersId);
		saljareFromGoteborgMembersId.retainAll(goteborgMembersId);

		//Lines bellow are used to separate chefer from other employees by Region
		List<String> cheferFromSwedenMembersId = new ArrayList<>(cheferMembersId);
		cheferFromSwedenMembersId.retainAll(allSwedishMembersId);

		List<String> cheferFromMalmoMembersId = new ArrayList<>(cheferFromSwedenMembersId);
		cheferFromMalmoMembersId.retainAll(malmoMembersId);

		List<String> cheferFromGoteborgMembersId = new ArrayList<>(cheferFromSwedenMembersId);
		cheferFromGoteborgMembersId.retainAll(goteborgMembersId);

		List<String> cheferFromStockholmMembersId = new ArrayList<>(cheferFromSwedenMembersId);
		cheferFromStockholmMembersId.retainAll(stockholmMembersId);

		//Lines bellow fetch all accounts for säljare in Sweden, it is necessary in order to check when they started work on their positions
		List<Account> saljareFromMalmoAccounts = new ArrayList<>(accountService.getAllAccountsById(saljareFromMalmoMembersId));
		List<Account> saljareFromGoteborgAccounts = new ArrayList<>(accountService.getAllAccountsById(saljareFromGoteborgMembersId));
		List<Account> saljareFromStockholmAccounts = new ArrayList<>(accountService.getAllAccountsById(saljareFromStockholmMembersId));

		int firstStartDate = 1546297200; // 1.1.2019. 00:00:00
		int lastStardDate = 1672527599; // 31.12.2022. 23:59:59

		//Lines bellow are will keep only accounts that are owned by employees who started between 1.1.2019. 00:00:00 and 31.12.2022. 23:59:59
		saljareFromMalmoAccounts.retainAll(accountService.filterAccountsByEmploymentStartDate(saljareFromMalmoAccounts, firstStartDate, lastStardDate));
		saljareFromGoteborgAccounts.retainAll(accountService.filterAccountsByEmploymentStartDate(saljareFromGoteborgAccounts, firstStartDate, lastStardDate));
		saljareFromStockholmAccounts.retainAll(accountService.filterAccountsByEmploymentStartDate(saljareFromStockholmAccounts, firstStartDate, lastStardDate));

		//To find names all of chefer
		List<Account> cheferFromMalmoAccounts = new ArrayList<>(accountService.getAllAccountsById(cheferFromMalmoMembersId));
		List<Account> cheferFromGoteborgAccounts = new ArrayList<>(accountService.getAllAccountsById(cheferFromGoteborgMembersId));
		List<Account> cheferFromStockholmAccounts = new ArrayList<>(accountService.getAllAccountsById(cheferFromStockholmMembersId));

		assertTrue(cheferFromMalmoAccounts.get(0).getFirstName().equals("Rasmus") && cheferFromMalmoAccounts.get(0).getLastName().equals("Persson")); // Chefer in Malmö
		assertTrue(cheferFromMalmoAccounts.get(1).getFirstName().equals("Anna") && cheferFromMalmoAccounts.get(1).getLastName().equals("Gunnarsson"));
		assertTrue(cheferFromMalmoAccounts.get(2).getFirstName().equals("Agnes") && cheferFromMalmoAccounts.get(2).getLastName().equals("Nordström"));

		assertTrue(cheferFromGoteborgAccounts.get(0).getFirstName().equals("Saga") && cheferFromGoteborgAccounts.get(0).getLastName().equals("Berggren")); // Chefer in Göteborg
		assertTrue(cheferFromGoteborgAccounts.get(1).getFirstName().equals("Julia") && cheferFromGoteborgAccounts.get(1).getLastName().equals("Håkansson"));
		assertTrue(cheferFromGoteborgAccounts.get(2).getFirstName().equals("Emelie") && cheferFromGoteborgAccounts.get(2).getLastName().equals("Gustavsson"));

		assertTrue(cheferFromStockholmAccounts.get(0).getFirstName().equals("Karl") && cheferFromStockholmAccounts.get(0).getLastName().equals("Bengtsson")); // Chefer in Stockholm
		assertTrue(cheferFromStockholmAccounts.get(1).getFirstName().equals("Julia") && cheferFromStockholmAccounts.get(1).getLastName().equals("Björnsson"));
		assertTrue(cheferFromStockholmAccounts.get(2).getFirstName().equals("Sten") && cheferFromStockholmAccounts.get(2).getLastName().equals("Ekström"));
	}
}
