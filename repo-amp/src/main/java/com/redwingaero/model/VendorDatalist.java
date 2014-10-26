package com.redwingaero.model;

import org.alfresco.service.namespace.QName;


/**
 * References to all of the items defined in valueAssistanceModel.xml
 * 
 */
public class VendorDatalist
{
 
    /**Value Assistance Model URI */
    public static final String REDWINGAERO_VALUE_ASSISTANCE_MODEL_URI = "http://www.redwingaero.com/datalist/1.0";
    public static final String DATALIST_MODEL_URI = "http://www.alfresco.org/model/datalist/1.0";

    public static final String REDWINGAERO_VALUE_ASSISTANCE_MODEL_PREFIX = "rwad";
    public static final String CONTENT_MODEL_PREFIX = "cm";
    public static final String DATALIST_MODEL_PREFIX = "dl";
    
    public static final QName TYPE_VALUE_ASSISTANCE_LIST_ITEM = QName.createQName(REDWINGAERO_VALUE_ASSISTANCE_MODEL_URI, "VendorDatalist");
    public static final QName TYPE_DATALIST = QName.createQName(DATALIST_MODEL_URI, "dataList");
    
    public static final QName PROP_SORT_ORDER = QName.createQName(REDWINGAERO_VALUE_ASSISTANCE_MODEL_URI, "VendorId");
    public static final QName PROP_VALUE = QName.createQName(REDWINGAERO_VALUE_ASSISTANCE_MODEL_URI, "VendorName");
}
