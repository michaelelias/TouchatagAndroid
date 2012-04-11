package com.touchatag.acs.api.client;

import com.touchatag.acs.api.client.model.MetadataAssociation;
import com.touchatag.acs.api.client.model.MetadataHolderType;
import com.touchatag.acs.api.client.model.MetadataItem;
import com.touchatag.acs.api.client.model.MetadataItemPage;
import com.touchatag.acs.api.client.model.MetadataLabel;
import com.touchatag.acs.api.client.model.MetadataType;

public abstract class MetadataApiClient extends BaseAcsApiClient {

	public MetadataApiClient(AcsServer server, String accessToken, String accessTokenSecret) {
		super(server, accessToken, accessTokenSecret);
	}

	public MetadataItem createItem(MetadataItem item) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPost("/metadataitems", item, MetadataItem.class);
	}

	public MetadataItem updateItem(MetadataItem item) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPut("/metadataitems/" + item.getId(), item, MetadataItem.class);
	}

	public MetadataItem getItem(String itemId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/metadataitems/" + itemId, MetadataItem.class);
	}

	public boolean deleteItem(String itemId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/metadataitems/" + itemId);
	}
	
	public MetadataItemPage getItemPage(int pageNumber, int pageSize) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doGet("/metadataitems/page/" + pageNumber + "?pageSize=" + pageSize, MetadataItemPage.class);
	}
	
	public MetadataLabel createLabel(MetadataLabel item) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPost("/metadatalabels", item, MetadataLabel.class);
	}

	public MetadataLabel updateLabel(MetadataLabel item) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPut("/metadatalabels/" + item.getId(), item, MetadataLabel.class);
	}

	public MetadataItem getLabel(String labelId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/metadatalabels/" + labelId, MetadataItem.class);
	}

	public boolean deleteLabel(String labelId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/metadatalabels/" + labelId);
	}

	public MetadataType createType(MetadataType type) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPost("/metadatatypes", type, MetadataType.class);
	}

	public MetadataType updateType(MetadataType type) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doPut("/metadatatypes/" + type.getIdentifier(), type, MetadataType.class);
	}

	public MetadataType getType(String typeId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doGet("/metadatatypes/" + typeId, MetadataType.class);
	}

	public boolean deleteType(String typeId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
		return doDelete("/metadatatypes/" + typeId);
	}
	
	public MetadataAssociation createAssociation(MetadataAssociation association) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doPost("/metadataassociations", association, MetadataAssociation.class);
	}
	
	public boolean deleteAssociation(MetadataHolderType holdertype, String holderId, String metadataItemId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doDelete("/metadataassociations/" + holdertype + "/" + holderId + "/" + metadataItemId);
	}
	
	public boolean deleteAllHolderAssociations(MetadataHolderType holdertype, String holderId) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException{
		return doDelete("/metadataassociations/" + holdertype + "/" + holderId);
	}

}
