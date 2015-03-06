package com.redwingaero.webscript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class CharterExportPicklistWebscript extends DeclarativeWebScript {
	
	private SearchService searchService;

	private static final Log logger = LogFactory
			.getLog(CharterExportPicklistWebscript.class);

	// site name to retrieve datalist
	public static final String PARAM_SITE_NAME = "site_name";
	// include an empty value at the start of picklist
	public static final String PARAM_DATALIST_NAME = "datalist_name";
	// field name of datalist
	public static final String PARAM_FIELD_NAME = "field_name";

	protected ServiceRegistry serviceRegistry;
	private NodeService nodeService;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	@Override
	protected Map<String, Object> executeImpl(final WebScriptRequest req,
			Status status, Cache cache) {
		final Map<String, Object> model = new HashMap<String, Object>();

		logger.debug("Begin");
		try {
			final RetryingTransactionCallback<String> transactionWork = new RetryingTransactionCallback<String>() {
				public String execute() throws Throwable {
					process(req, model);
					return null;
				}
			};

			// perform this web script's work in a single write transaction
			serviceRegistry.getTransactionService()
					.getRetryingTransactionHelper()
					.doInTransaction(transactionWork, false);

		} catch (Throwable t) {
			String error = "Unhandled exception: " + t.getMessage();
			logger.error(error, t);
			model.put("error", error);
		}

		logger.debug("End");
		return model;
	}

	protected void process(WebScriptRequest req, Map<String, Object> model)
			throws Exception {
		if (fillDatalistData(req, model)){
			return;
		}
		return;
	}

	protected void handleError(String error, Map<String, Object> model) {
		logger.error(error);
		model.put("error", error);
	}

	private boolean fillDatalistData(WebScriptRequest req,
			Map<String, Object> model) {
		
		List<PicklistItem> itemList = new ArrayList<PicklistItem>();
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		
		StringBuffer query = new StringBuffer();
		query.append("PATH:\"/app:company_home/st:sites/cm:charter/cm:documentLibrary/*\"");
		// Set search parameterspn
		SearchParameters searchParameters = new SearchParameters();
		searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		searchParameters.setQuery(query.toString());

		ResultSet rs = serviceRegistry.getSearchService().query(
				searchParameters);	
	
		if (rs == null || rs.length() < 1) {
			handleError("Unable to locate data list object with title ", model);
		} else {
			for (NodeRef nodeRef : rs.getNodeRefs()) {
				String name = serviceRegistry.getNodeService().getProperty(nodeRef, ContentModel.PROP_NAME).toString();
				itemList.add(new PicklistItem(name != null ? name.toString()
						: "", name != null ? name.toString() : ""));
			}
			}
			model.put("picklistItems", itemList);
			return true;
	}

	protected void handleError(String error, Map<String, Object> model,
			Throwable t) {
		logger.error(error, t);
		model.put("error", error);
	}
}