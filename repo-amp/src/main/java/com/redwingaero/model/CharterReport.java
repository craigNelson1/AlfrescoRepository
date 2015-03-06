package com.redwingaero.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CharterReport {
	
	private static Log logger = LogFactory.getLog(CharterReport.class);
	
    /** Thread local cache of namespace prefixes for long QName to short prefix name conversions */
    protected static ThreadLocal<Map<String, String>> namespacePrefixCache = new ThreadLocal<Map<String, String>>()
    {
        @Override
        protected Map<String, String> initialValue()
        {
            return new HashMap<String, String>(8);
        }
    };
    
	private CellStyle cs = null;
 	
 	private NodeService nodeService;
 	private FileFolderService fileFolderService;
 	private NamespaceService namespaceService;
 	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void init()
    {
		System.out.println("Initializing the bean.....");
    }
	public Workbook createWorkbook(Workbook wb,NodeRef nodeRef) throws ParseException{
		Workbook charterWorkbook = wb;
		Sheet sheet = wb.createSheet("CharterInfo");
		setCellStyles(wb);
		this.nodeService.getAspects(nodeRef);
		
		// Get node info
		System.out.println("Before NodeIfno" + nodeRef.getId());
		System.out.println("Before NodeIfno" + this.fileFolderService.toString());
        FileInfo nodeInfo = fileFolderService.getFileInfo(nodeRef);
        System.out.println("After NodeIfno" + nodeInfo.getProperties().size());
        final JSONObject jsonProperties = propertiesToJSON(nodeRef, nodeInfo.getProperties(), false);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonProperties.toString());
        JSONObject properties = (JSONObject) jsonObject.get("properties");
        insertData(sheet,properties);
		
		return charterWorkbook;
	}
    /**
     * Convert a qname to a string - either full or short prefixed named.
     * 
     * @param qname
     * @param isShortName
     * @return qname string.
     */
    private String nameToString(final QName qname, final boolean isShortName)
    {
        String result;
        if (isShortName)
        {
            final Map<String, String> cache = namespacePrefixCache.get();
            String prefix = cache.get(qname.getNamespaceURI());
            if (prefix == null)
            {
                // first request for this namespace prefix, get and cache result
                Collection<String> prefixes = this.namespaceService.getPrefixes(qname.getNamespaceURI());
                prefix = prefixes.size() != 0 ? prefixes.iterator().next() : "";
                cache.put(qname.getNamespaceURI(), prefix);
            }
            result = prefix + QName.NAMESPACE_PREFIX + qname.getLocalName();
        }
        else
        {
            result = qname.toString();
        }
        System.out.println("$$$$$ :: "+ result);
        return result;
    }
	
    private JSONObject propertiesToJSON(NodeRef nodeRef, Map<QName, Serializable> properties,boolean useShortQNames){
        JSONObject propertiesJSON = new JSONObject();
        
        for (QName propertyName : properties.keySet())
        {
            try
            {
                String key = nameToString(propertyName, useShortQNames);
                Serializable value = properties.get(propertyName);
                
                propertiesJSON.put(key, propertyToJSON(nodeRef, propertyName, key, value));
            }
            catch (NamespaceException ne)
            {
                // ignore properties that do not have a registered namespace
                if (logger.isDebugEnabled())
                	System.out.println("Ignoring property '" + propertyName + "' as its namespace is not registered");
                    logger.debug("Ignoring property '" + propertyName + "' as its namespace is not registered");
            }
        }
        
        return propertiesJSON;
    }

    /**
     * Handles the work of converting values to JSON.
     * 
     * @param nodeRef
     * @param propertyName
     * @param key
     * @param value
     * @return the JSON value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object propertyToJSON(final NodeRef nodeRef, final QName propertyName, final String key, final Serializable value)
    {
    	if (value != null)
        {            
            
            // Built-in data type processing
            if (value instanceof Date)
            {
                JSONObject dateObj = new JSONObject();
                dateObj.put("value", JSONObject.escape(value.toString()));
                dateObj.put("iso8601", JSONObject.escape(ISO8601DateFormat.format((Date)value)));
                return dateObj;
            }
            else if (value instanceof List)
            {
            	// Convert the List to a JSON list by recursively calling propertyToJSON
            	List<Object> jsonList = new ArrayList<Object>(((List<Serializable>) value).size());
            	for (Serializable listItem : (List<Serializable>) value)
            	{
            	    jsonList.add(propertyToJSON(nodeRef, propertyName, key, listItem));
            	}
            	return jsonList;
            }
            else if (value instanceof Double)
            {
                return (Double.isInfinite((Double)value) || Double.isNaN((Double)value) ? null : value.toString());
            }
            else if (value instanceof Float)
            {
                return (Float.isInfinite((Float)value) || Float.isNaN((Float)value) ? null : value.toString());
            }
            else
            {
            	return value.toString();
            }
            
        }
    	return null;
    }
    
	private void setCellStyles(Workbook wb){
		// Set the font size for sheet
		Font font = wb.createFont();
		font.setFontHeight((short)15);
		
		// Set the cell style
		cs = wb.createCellStyle();
		cs.setFont(font);
	}
	
	private int insertData(Sheet s,JSONObject json){
		int rowIndex = 0;
		Row row = null;
		Cell cell = null;
		
		//First Row
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Trip Number");
		cell = row.createCell(1);
		cell.setCellValue(json.get("{http://www.redwingaero.com/charter/1.0}QuoteNumber").toString());
		
		//Second Row
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Customer Name");
		cell = row.createCell(1);
		cell.setCellValue(json.get("{http://www.redwingaero.com/charter/1.0}CustomerNumber").toString());
		
		return rowIndex;		
	}
}
