
package com.redwingaero.model;

import org.alfresco.service.namespace.QName;

public class Model {
	public static final String NAMESPACE_INVOICE="http://www.redwingaero.com/link/myfilefile/1.0";
	
	public static final String INVOICE="Invoice";
	public static final String DOCUMENT="LinkDocs";
	public static final String PURCHASE="PurchaseOrder";
	
	public static final QName TYPE_DOCUMENT= QName.createQName(NAMESPACE_INVOICE, DOCUMENT);
	public static final QName TYPE_INVOICE= QName.createQName(NAMESPACE_INVOICE, INVOICE);
	public static final QName TYPE_PURCHASE= QName.createQName(NAMESPACE_INVOICE, PURCHASE);

	public static final QName PROP_ASSO_INVOICE= QName.createQName(NAMESPACE_INVOICE, "POAssociation");
	public static final QName PROP_ASSO_PURCHASE= QName.createQName(NAMESPACE_INVOICE, "POAssociationInvoice");
	public static final QName PROP_ASSO_DOCUMENT= QName.createQName(NAMESPACE_INVOICE, "DocumentAssociation");
}