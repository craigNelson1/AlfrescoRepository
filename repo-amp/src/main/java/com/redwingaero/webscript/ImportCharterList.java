package com.redwingaero.webscript;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.redwingaero.model.CharterDatalist;

public class ImportCharterList extends AbstractWebScript {

	protected ServiceRegistry serviceRegistry;
	private static final String SITE = "site";
	private static final String DATALIST = "datalist";

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {
		// Code for reading line from file
		String siteName = req.getParameter(SITE);
		SiteInfo site = serviceRegistry.getSiteService().getSite(siteName);
		NodeRef datalist = null;
		for (ChildAssociationRef c : serviceRegistry.getNodeService()
				.getChildAssocs(site.getNodeRef())) {
			if (serviceRegistry.getNodeService()
					.getProperty(c.getChildRef(), ContentModel.PROP_NAME)
					.toString().equals("dataLists"))
				;
			{
				datalist = c.getChildRef();
			}
		}
		System.out.println("Datalist NodeRef " + datalist);
		for (ChildAssociationRef node : serviceRegistry.getNodeService()
				.getChildAssocs(datalist)) {
			Serializable name = serviceRegistry.getNodeService().getProperty(
					node.getChildRef(), ContentModel.PROP_TITLE);

			String datalistName = name != null ? name.toString() : "";
			System.out.println("Datalist Children Ref " + node.getChildRef());

			if (datalistName.equals(req.getParameter(DATALIST))) {
				String content = req.getContent().getContent();
				String[] lines = content.split(System
						.getProperty("line.separator"));
				// For removing all childrens from datalist
				for (ChildAssociationRef child : serviceRegistry
						.getNodeService().getChildAssocs(node.getChildRef())) {
					serviceRegistry.getNodeService().removeChild(
							node.getChildRef(), child.getChildRef());
				}
                 System.out.println("Line Number : " + lines.length);
							
				for (int counter = 4; counter < (lines.length - 1); counter++) {
					System.out.println("Lines " + counter + lines[counter]);
					//		for (int j = 0; j < lines[4].split(",").length - 1; j++) {
						if (lines[counter].split(",").length > 1) {
							System.out.println("Lines " + lines[counter].split(",")[1]);

							HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
							String guid = GUID.generate();
							
							props.put(ContentModel.PROP_NAME, guid);
							props.put(CharterDatalist.PROP_SORT_ORDER, lines[counter].split(",")[0]);
							props.put(CharterDatalist.PROP_VALUE, lines[counter].split(",")[1]);
							serviceRegistry
									.getNodeService()
									.createNode(
											node.getChildRef(),
											ContentModel.ASSOC_CONTAINS,
											QName.createQName(
													NamespaceService.CONTENT_MODEL_1_0_URI,
													props.get(CharterDatalist.PROP_SORT_ORDER).toString()),
													CharterDatalist.TYPE_DATALIST_VENDORDATALIST,
											props);
						}
				//	}
				}
				
				System.out.println("Got to here !!!!!!!11111111111");
			}
		}

	}
}
