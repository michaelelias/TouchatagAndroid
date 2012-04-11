package com.touchatag.acs.api.client;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.touchatag.acs.api.client.model.AcsIdentity;
import com.touchatag.acs.api.client.model.MetadataAssociation;
import com.touchatag.acs.api.client.model.MetadataHolder;
import com.touchatag.acs.api.client.model.MetadataHolderType;
import com.touchatag.acs.api.client.model.MetadataItem;
import com.touchatag.acs.api.client.model.MetadataItemPage;
import com.touchatag.acs.api.client.model.MetadataType;
import com.touchatag.acs.api.client.model.MetadataTypeScope;

public class MetadataApiClientTest {

	private static final String ID = "id";

	// Coreteam identity id
	private static final String OWNERID = "d96cc489-1d39-43e4-8a9d-9408d892d2cc";

	private static final String VALUE = "value";

	private static TestServer SERVER = new TestServer();
	private static MetadataApiClient CLIENT;

	private static MetadataType TEST_TYPE;
	private static MetadataItem TEST_ITEM;
	private static final String TEST_TYPE_IDENTIFIER = "test-type";

	@BeforeClass
	public static void setUpClass() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		CLIENT = new MetadataApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

			@Override
			protected void log(String message) {
				System.out.println(message);
			}
		};

		createTestType();
		createTestItem();
	}

	@AfterClass
	public static void tearDownClass() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		deleteTestItem();
		
		CLIENT.deleteAllHolderAssociations(MetadataHolderType.USER, OWNERID);
		deleteMetadataItemsOfOwner(OWNERID);
	}

	private static void deleteMetadataItemsOfOwner(String ownerId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		MetadataItemPage page = CLIENT.getItemPage(1, 100);
		for(MetadataItem item : page.getItems()){
			if(item.getOwnerId().equals(ownerId)){
				CLIENT.deleteItem(item.getId());
			}
		}
	}

	private static void createTestType() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		try {
			CLIENT.getType(TEST_TYPE_IDENTIFIER);
		} catch (Exception e) {
			MetadataType type = new MetadataType();
			type.setIdentifier(TEST_TYPE_IDENTIFIER);
			type.setOwnerId(OWNERID);
			type.setScope(MetadataTypeScope.PUBLIC);
			TEST_TYPE = CLIENT.createType(type);
		}
	}

	private static void createTestItem() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		MetadataItem item = new MetadataItem();
		item.setType(TEST_TYPE_IDENTIFIER);
		item.setOwnerId(OWNERID);
		item.setValue("test-value");
		TEST_ITEM = CLIENT.createItem(item);
	}

	private static void deleteTestItem() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		CLIENT.deleteItem(TEST_ITEM.getId());
	}

	@Test
	public void testCRUDMetadataItem() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		String value = "test-value";
		String updatedValue = "updated-test-value";

		MetadataItem item = new MetadataItem();
		item.setOwnerId(OWNERID);
		item.setType(TEST_TYPE_IDENTIFIER);
		item.setValue(value);

		// MetadataLabel label = new MetadataLabel();
		// label.setName("name");
		// label.setOwnerId(OWNERID);
		// item.getLabels().add(label);
		MetadataItem createdItem = CLIENT.createItem(item);

		Assert.assertNotNull(createdItem);
		Assert.assertNotNull(createdItem.getId());
		Assert.assertEquals(item.getOwnerId(), createdItem.getOwnerId());
		Assert.assertEquals(item.getValue(), createdItem.getValue());
		Assert.assertEquals(item.getType(), createdItem.getType());

		createdItem.setValue(updatedValue);
		MetadataItem updatedItem = CLIENT.updateItem(createdItem);
		Assert.assertNotNull(updatedItem);
		Assert.assertNotNull(updatedItem.getId());
		Assert.assertEquals(createdItem.getOwnerId(), updatedItem.getOwnerId());
		Assert.assertEquals(updatedValue, updatedItem.getValue());
		Assert.assertEquals(createdItem.getType(), updatedItem.getType());

		MetadataItem fetchedItem = CLIENT.getItem(updatedItem.getId());
		Assert.assertNotNull(fetchedItem);
		Assert.assertNotNull(fetchedItem.getId());
		Assert.assertEquals(updatedItem.getOwnerId(), fetchedItem.getOwnerId());
		Assert.assertEquals(updatedItem.getValue(), fetchedItem.getValue());
		Assert.assertEquals(updatedItem.getType(), fetchedItem.getType());

		boolean deleted = CLIENT.deleteItem(fetchedItem.getId());
		Assert.assertTrue(deleted);

		try {
			CLIENT.getItem(fetchedItem.getId());
			Assert.fail("Expected item to be deleted");
		} catch (Exception e) {
		}

	}
	
	@Test
	public void testMetadataAssociation() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		MetadataHolder holder = new MetadataHolder();
		holder.setType(MetadataHolderType.USER);
		holder.setReference(OWNERID);
		
		MetadataAssociation asso = new MetadataAssociation();
		asso.setMetadataItemId(TEST_ITEM.getId());
		asso.setMetadataHolder(holder);
		
		MetadataAssociation createdAsso = CLIENT.createAssociation(asso);
		Assert.assertNotNull(createdAsso);
		Assert.assertEquals(asso.getMetadataItemId(), createdAsso.getMetadataItemId());
		
	}
	
	@Test
	public void testGetAcsIdentityAndCreateMetadataItem() throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		AcsIdentityApiClient acsIdentityClient = new AcsIdentityApiClient(SERVER, SERVER.getAccessToken(), SERVER.getAccessTokenSecret()) {

			@Override
			protected void log(String message) {
				System.out.println(message);
			}
		};
		
		AcsIdentity identity =  acsIdentityClient.get(OWNERID);
		Assert.assertEquals(OWNERID, identity.getIdentityId());
		
		String value = "test-value";
		MetadataItem item = new MetadataItem();
		item.setOwnerId(OWNERID);
		item.setType(TEST_TYPE_IDENTIFIER);
		item.setValue(value);
		item = CLIENT.createItem(item);
		
		Assert.assertNotNull(item);
		Assert.assertNotNull(item.getId());
		
		MetadataAssociation asso = new MetadataAssociation();
		MetadataHolder holder = new MetadataHolder();
		holder.setReference(OWNERID);
		holder.setType(MetadataHolderType.USER);
		asso.setMetadataHolder(holder);
		asso.setMetadataItemId(item.getId());
		asso = CLIENT.createAssociation(asso);
		
		Assert.assertNotNull(asso);
		Assert.assertNotNull(asso.getMetadataHolder());
		Assert.assertEquals(OWNERID, asso.getMetadataHolder().getReference());
		Assert.assertEquals(MetadataHolderType.USER, asso.getMetadataHolder().getType());
		Assert.assertEquals(item.getId(), asso.getMetadataItemId());
		
		MetadataItemPage page = CLIENT.getItemPage(1, 25);
		Assert.assertNotNull(page);
		
		Assert.assertTrue(page.getItems().size() > 0);
	}

}
