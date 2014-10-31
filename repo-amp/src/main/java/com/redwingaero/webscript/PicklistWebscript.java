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
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.redwingaero.model.VendorDatalist;

public class PicklistWebscript extends DeclarativeWebScript {

	private static final Log logger = LogFactory
			.getLog(PicklistWebscript.class);

	// site name to retrieve datalist
	public static final String PARAM_SITE_NAME = "site_name";
	// include an empty value at the start of picklist
	public static final String PARAM_DATALIST_NAME = "datalist_name";
	// field name of datalist
	public static final String PARAM_FIELD_NAME = "field_name";

	protected ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
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
		if (fillSiteData(req, model))
			return;
		else if (fillDatalistData(req, model))
			return;
		else {
			fillParamData(req, model);
			return;
		}
/*		String dataListName = req.getParameter(PARAM_DATALIST_NAME);
		// get the folder for the datalist
		StringBuffer query = new StringBuffer();
		query.append("PATH:\"app:company_home/st:sites/cm:" + siteName
				+ "/cm:dataLists/*\"");
		logger.debug("Query = " + query);
		// Set search parameterspn
		SearchParameters searchParameters = new SearchParameters();
		searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		searchParameters.setQuery(query.toString());

		ResultSet rs = serviceRegistry.getSearchService().query(
				searchParameters);

		List<PicklistItem> picklistItems = new ArrayList<PicklistItem>();
		if (rs == null || rs.length() < 1) {
			handleError("Unable to locate data list object with title "
					+ siteName + ".", model);
		} else {
			for (NodeRef nodeRef : rs.getNodeRefs()) {
				Map<QName, Serializable> props = serviceRegistry
						.getNodeService().getProperties(nodeRef);
				if (props.get(ContentModel.PROP_TITLE).equals(dataListName)) {

					List<ChildAssociationRef> lstChildAssociationRefs = serviceRegistry
							.getNodeService().getChildAssocs(nodeRef);
					for (ChildAssociationRef dataListChildren : lstChildAssociationRefs) {
						Serializable name = serviceRegistry
								.getNodeService()
								.getProperties(dataListChildren.getChildRef())
								.get(QName
										.createQName(
												VendorDatalist.REDWINGAERO_VALUE_ASSISTANCE_MODEL_URI,
												req.getParameter(PARAM_FIELD_NAME)));
						picklistItems.add(new PicklistItem(name != null ? name
								.toString() : "", name != null ? name
								.toString() : ""));
					}
				}
			}
		}
		model.put("picklistItems", picklistItems);*/
	}

	protected void handleError(String error, Map<String, Object> model) {
		logger.error(error);
		model.put("error", error);
	}

	private boolean fillSiteData(WebScriptRequest req, Map<String, Object> model) {
		String siteName = req.getParameter(PARAM_SITE_NAME);
		List<PicklistItem> itemList = new ArrayList<PicklistItem>();
		if (siteName == null || siteName.equals("")) {
			List<SiteInfo> lstSites = serviceRegistry.getSiteService()
					.listSites(null, null, 0);

			for (SiteInfo site : lstSites) {
				itemList.add(new PicklistItem(site.getShortName(), site
						.getShortName()));
			}

			model.put("picklistItems", itemList);
			return true;
		} else
			return false;
	}

	private boolean fillDatalistData(WebScriptRequest req,
			Map<String, Object> model) {
		String siteName = req.getParameter(PARAM_SITE_NAME);
		String datalistName = req.getParameter(PARAM_DATALIST_NAME);
		List<PicklistItem> itemList = new ArrayList<PicklistItem>();
		if (datalistName == null || datalistName.equals("")) {
			SiteInfo site = serviceRegistry.getSiteService().getSite(siteName);
			NodeRef datalist=null;
			for(ChildAssociationRef c:serviceRegistry.getNodeService().getChildAssocs(site.getNodeRef())){
				if(serviceRegistry.getNodeService().getProperty(c.getChildRef(),ContentModel.PROP_NAME).toString().equals("dataLists"));
				{
					datalist=c.getChildRef();
				}
			}
			for (ChildAssociationRef node : serviceRegistry.getNodeService()
					.getChildAssocs(datalist)) {
				Serializable name = serviceRegistry.getNodeService()
						.getProperty(node.getChildRef(),
								ContentModel.PROP_TITLE);
				itemList.add(new PicklistItem(name != null ? name.toString()
						: "", name != null ? name.toString() : ""));
			}

			model.put("picklistItems", itemList);
			return true;
		} else
			return false;
	}

	private void fillParamData(WebScriptRequest req, Map<String, Object> model) {
		String siteName = req.getParameter(PARAM_SITE_NAME);
		String datalistName = req.getParameter(PARAM_DATALIST_NAME);
		List<PicklistItem> itemList = new ArrayList<PicklistItem>();

		SiteInfo site = serviceRegistry.getSiteService().getSite(siteName);
		NodeRef datalist=null;
		for(ChildAssociationRef c:serviceRegistry.getNodeService().getChildAssocs(site.getNodeRef())){
			if(serviceRegistry.getNodeService().getProperty(c.getChildRef(),ContentModel.PROP_NAME).toString().equals("dataLists"));
			{
				datalist=c.getChildRef();
			}
		}
		for (ChildAssociationRef node : serviceRegistry.getNodeService()
				.getChildAssocs(datalist)) {
			Serializable name = serviceRegistry.getNodeService().getProperty(
					node.getChildRef(), ContentModel.PROP_TITLE);
			if (datalistName.equals(name)) {

				for (ChildAssociationRef listMembers : serviceRegistry
						.getNodeService().getChildAssocs(node.getChildRef())) {
					Serializable propertyName = serviceRegistry
							.getNodeService()
							.getProperty(
									listMembers.getChildRef(),
									QName.createQName(
											VendorDatalist.REDWINGAERO_VALUE_ASSISTANCE_MODEL_URI,
											req.getParameter(PARAM_FIELD_NAME)));
					itemList.add(new PicklistItem(
							propertyName != null ? propertyName.toString() : "",
							propertyName != null ? propertyName.toString() : ""));
				}
			}

		}

		model.put("picklistItems", itemList);

	}

	protected void handleError(String error, Map<String, Object> model,
			Throwable t) {
		logger.error(error, t);
		model.put("error", error);
	}
}