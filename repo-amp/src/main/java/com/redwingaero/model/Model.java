
package com.redwingaero.model;

import org.alfresco.service.namespace.QName;

public class Model {
	public static final String NAMESPACE_INVOICE="http://www.redwingaero.com/link/myfilefile/1.0";
	public static final String NAMESPACE_CHARTER="http://www.redwingaero.com/charter/1.0";
	public static final String NAMESPACE_CONTENT="http://www.alfresco.org/model/content/1.0";

	
	public static final String INVOICE="Invoice";
	public static final String DOCUMENT="LinkDocs";
	public static final String PURCHASE="PurchaseOrder";
	
	public static final QName TYPE_DOCUMENT= QName.createQName(NAMESPACE_INVOICE, DOCUMENT);
	public static final QName TYPE_INVOICE= QName.createQName(NAMESPACE_INVOICE, INVOICE);
	public static final QName TYPE_PURCHASE= QName.createQName(NAMESPACE_INVOICE, PURCHASE);

	public static final QName PROP_ASSO_INVOICE= QName.createQName(NAMESPACE_INVOICE, "POAssociation");
	public static final QName PROP_ASSO_PURCHASE= QName.createQName(NAMESPACE_INVOICE, "POAssociationInvoice");
	public static final QName PROP_ASSO_DOCUMENT= QName.createQName(NAMESPACE_INVOICE, "DocumentAssociation");
	
	public static final QName PROP_NAME= QName.createQName(NAMESPACE_CONTENT, "name");
	public static final QName PROP_CONTAINS= QName.createQName(NAMESPACE_CONTENT, "contains");

	
	public static final QName ASPECT_CHARTERSTATUSINFO = QName.createQName(NAMESPACE_CHARTER, "CharterStatusInfo");
	public static final QName PROP_QUOTENUMBER = QName.createQName(NAMESPACE_CHARTER, "QuoteNumber");
	public static final QName PROP_CUSTOMERNUMBER = QName.createQName(NAMESPACE_CHARTER, "CustomerNumber");
	public static final QName PROP_ROUTING = QName.createQName(NAMESPACE_CHARTER, "Routing");
	public static final QName PROP_BROKERINFORMATION = QName.createQName(NAMESPACE_CHARTER, "BrokerInformation");
	public static final QName PROP_LEGS = QName.createQName(NAMESPACE_CHARTER, "Legs");
	public static final QName PROP_ITINERARY = QName.createQName(NAMESPACE_CHARTER, "Itinerary");
	public static final QName PROP_CREDITCARDAUTHORIZATION = QName.createQName(NAMESPACE_CHARTER, "CreditCardAuthorization");
	public static final QName PROP_CATERING = QName.createQName(NAMESPACE_CHARTER, "Catering");
	public static final QName PROP_ADDITIONALCHARGES = QName.createQName(NAMESPACE_CHARTER, "AdditionalCharges");
	public static final QName PROP_SHREDCREDITCARDFORM = QName.createQName(NAMESPACE_CHARTER, "ShredCreditCardForm");
	public static final QName PROP_COMPILEFINALINVOICEFORAUDITING = QName.createQName(NAMESPACE_CHARTER, "CompileFinalInvoiceForAccounting");
	public static final QName PROP_NOTES = QName.createQName(NAMESPACE_CHARTER, "Notes");
	public static final QName PROP_CHARTERSTATUS = QName.createQName(NAMESPACE_CHARTER, "CharterStatus");
	
	
}