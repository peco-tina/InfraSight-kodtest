package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.infrasight.kodtest.model.Account;
import com.infrasight.kodtest.model.Group;
import com.infrasight.kodtest.model.Relationship;
import com.infrasight.kodtest.service.AccountService;
import com.infrasight.kodtest.service.AuthenticationService;
import com.infrasight.kodtest.service.GroupService;
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
	private static final GroupService groupService = new GroupService(okHttpClient);
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
		Account account = accountService.getAccountById(employeeId, null);
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

		List<String> groupIds = new ArrayList<>();
		for(Relationship relationship : allRelationships){
			groupIds.add(relationship.getGroupId());
		}

		List<Group> groups = groupService.getAllGroupsByIds(groupIds);

		assertEquals(9, groups.size());
		assertEquals(groups.get(0).getName(), ("Köpenhamn"));
		assertEquals(groups.get(1).getName(), ("Malmö"));
		assertEquals(groups.get(2).getName(), ("IT-Konsulter"));
		assertEquals(groups.get(3).getName(), ("Danmark"));
		assertEquals(groups.get(4).getName(), ("Sverige"));
		assertEquals(groups.get(5).getName(), ("Inhyrda"));
		assertEquals(groups.get(6).getName(), ("Chokladfabrik AB"));
		assertEquals(groups.get(7).getName(), ("Choklad"));
		assertEquals(groups.get(8).getName(), ("Konfektyr"));
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);
		List<String> memberIds = new ArrayList<>(relationshipService.getAllMemberIds("grp_inhyrda", null, null));
		List<Account> accounts = new ArrayList<>(accountService.getAllAccountsByIds(memberIds));
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
		List<String> saljareMemberIds = new ArrayList<>(relationshipService.getAllMemberIds("grp_saljare", null, null));
		List<String> malmoMemberIds = new ArrayList<>(relationshipService.getAllMemberIds("grp_malmo", null, null));
		List<String> stockholmMemberIds = new ArrayList<>(relationshipService.getAllMemberIds("grp_stockholm", null, null));
		List<String> goteborgMemberIds = new ArrayList<>(relationshipService.getAllMemberIds("grp_goteborg", null, null));
		List<String> cheferMemberIds = new ArrayList<>(relationshipService.getAllMemberIds("grp_chefer", null, null));
		List<String> allSwedishMemberIds = new ArrayList<>();

		allSwedishMemberIds.addAll(malmoMemberIds);
		allSwedishMemberIds.addAll(stockholmMemberIds);
		allSwedishMemberIds.addAll(goteborgMemberIds);

		//Lines bellow are used to separate säljare from other employees by Region
		List<String> saljareFromMalmoMemberIds = new ArrayList<>(saljareMemberIds);
		saljareFromMalmoMemberIds.retainAll(malmoMemberIds);

		List<String> saljareFromStockholmMemberIds = new ArrayList<>(saljareMemberIds);
		saljareFromStockholmMemberIds.retainAll(stockholmMemberIds);

		List<String> saljareFromGoteborgMemberIds = new ArrayList<>(saljareMemberIds);
		saljareFromGoteborgMemberIds.retainAll(goteborgMemberIds);

		//Lines bellow are used to separate chefer from other employees by Region
		List<String> cheferFromSwedenMemberIds = new ArrayList<>(cheferMemberIds);
		cheferFromSwedenMemberIds.retainAll(allSwedishMemberIds);

		List<String> cheferFromMalmoMemberIds = new ArrayList<>(cheferFromSwedenMemberIds);
		cheferFromMalmoMemberIds.retainAll(malmoMemberIds);

		List<String> cheferFromGoteborgMemberIds = new ArrayList<>(cheferFromSwedenMemberIds);
		cheferFromGoteborgMemberIds.retainAll(goteborgMemberIds);

		List<String> cheferFromStockholmMemberIds = new ArrayList<>(cheferFromSwedenMemberIds);
		cheferFromStockholmMemberIds.retainAll(stockholmMemberIds);

		//Lines bellow fetch all accounts for säljare in Sweden, it is necessary in order to check when they started work on their positions
		List<Account> saljareFromMalmoAccounts = new ArrayList<>(accountService.getAllAccountsByIds(saljareFromMalmoMemberIds));
		List<Account> saljareFromGoteborgAccounts = new ArrayList<>(accountService.getAllAccountsByIds(saljareFromGoteborgMemberIds));
		List<Account> saljareFromStockholmAccounts = new ArrayList<>(accountService.getAllAccountsByIds(saljareFromStockholmMemberIds));

		int firstStartDate = 1546297200; // 1.1.2019. 00:00:00
		int lastStardDate = 1672527599; // 31.12.2022. 23:59:59

		//Lines bellow are will keep only accounts that are owned by employees who started between 1.1.2019. 00:00:00 and 31.12.2022. 23:59:59
		saljareFromMalmoAccounts.retainAll(accountService.filterAccountsByEmploymentStartDate(saljareFromMalmoAccounts, firstStartDate, lastStardDate));
		saljareFromGoteborgAccounts.retainAll(accountService.filterAccountsByEmploymentStartDate(saljareFromGoteborgAccounts, firstStartDate, lastStardDate));
		saljareFromStockholmAccounts.retainAll(accountService.filterAccountsByEmploymentStartDate(saljareFromStockholmAccounts, firstStartDate, lastStardDate));

		//To find names of all chefer
		List<Account> cheferFromMalmoAccounts = new ArrayList<>(accountService.getAllAccountsByIds(cheferFromMalmoMemberIds));
		List<Account> cheferFromGoteborgAccounts = new ArrayList<>(accountService.getAllAccountsByIds(cheferFromGoteborgMemberIds));
		List<Account> cheferFromStockholmAccounts = new ArrayList<>(accountService.getAllAccountsByIds(cheferFromStockholmMemberIds));

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
