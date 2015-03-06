package com.redwingaero.webscript;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.app.JSONConversionComponent;
import org.alfresco.repo.web.scripts.content.StreamContent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.FileCopyUtils;

import com.redwingaero.model.Model;
import com.redwingaero.model.CharterReport;


public class StreamCharterStatusInfo extends StreamContent {
	private static Log logger = LogFactory.getLog(StreamCharterStatusInfo.class);
	
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
	private NamespaceService namespaceService;
	private FileFolderService fileFolderService;
	private FileInfo fileInfo;
	private SearchService searchService;
	private SiteService siteService;
	private DictionaryService dictionaryService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {               
    	String folderPath = req.getParameter("path");	
	
    	 List<NodeRef> charterNode = getNodeRef(folderPath);

		
			//Map<QName,PropertyDefinition> aspectProperties = dictionaryService.getAspect(Model.ASPECT_CHARTERSTATUSINFO).getProperties();
			// init namespace prefix cache
            namespacePrefixCache.get().clear();
            //JSONConversionComponent jsonProp = new JSONConversionComponent();
            //System.out.println(jsonProp.toJSON(charterNode, true));
			prepareZip(charterNode,res);
		

	}

	private List<NodeRef> getNodeRef(String charterName){
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		String searchName = charterName.replace(" ", "_x0020_");
		String searchPath = "PATH:\"/app:company_home/st:sites/cm:charter/cm:documentLibrary/cm:" + searchName+ "//*\" AND +ASPECT:\"charter:CharterStatusInfo\"";
		ResultSet rs = searchService.query(storeRef,SearchService.LANGUAGE_LUCENE,searchPath);
		return rs.getNodeRefs();
	}
	
	private void prepareZip( List<NodeRef> nodeRefs, WebScriptResponse res){
		ZipOutputStream zout = null;
		PrintWriter tempFileOut = null;
		
		try{
            res.setContentType("application/zip");
            Cache cache = new Cache();
            cache.setNeverCache(true);
            cache.setMustRevalidate(true);
            cache.setMaxAge(0L);
            res.setCache(cache);
            
            Date date = new Date();
            String attachFileName = "TripBackup_" + (date.getYear()+1900) + '_' + (date.getMonth()+1) + '_' + (date.getDate()) + '_' + (date.getHours()) + '_' + (date.getMinutes());
            String headerValue = "attachment; filename=\"" + attachFileName + ".zip\"";
            
            // set header based on filename - will force a Save As from the browse if it doesn't recognize it
            // this is better than the default response of the browser trying to display the contents
            res.setHeader("Content-Disposition", headerValue);
            
            // Get node info
            //FileInfo nodeInfo = this.fileFolderService.getFileInfo(nodeRef);
            
            // 1. Get the whole aspect property in a JSON format
            // 2. Convert the above JSON to XLS format using POI library
            //final JSONObject json = new JSONObject();
            //json.put("properties", propertiesToJSON(nodeRef,nodeInfo.getProperties(),false));

            zout = new ZipOutputStream(res.getOutputStream());

            for(NodeRef nodeRef : nodeRefs ){
                File report = new File(nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString()+".xlsx");
                FileOutputStream fos = new FileOutputStream(nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString()+".xlsx");

	            Workbook wb = new XSSFWorkbook();
	            
	            createWorkbook(wb, nodeRef);
	            wb.write(fos);
	            ZipEntry zipEntry = new ZipEntry(nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString() + ".xlsx");
	            zout.putNextEntry(zipEntry);
	            fos.close();
	            FileInputStream fis = new FileInputStream(report);
	            byte[] byteBuffer = new byte[1024];
	            int bytesRead = -1;
	            while((bytesRead = fis.read(byteBuffer))!= -1){
	            	zout.write(byteBuffer, 0, bytesRead);
	            }
	            fis.close();
            } 

            // zip output
            
            
		}
        catch (IOException ioe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Could not output CharterStatusInfo : " + ioe.getMessage(), ioe);
        }
        finally
        {
        	if(tempFileOut!=null) tempFileOut.close();
        	
        	try{
        		if(zout!=null) zout.close();
        	}
        	catch(IOException ioe) {}
        }
	}
	
    private void createWorkbook(Workbook wb, NodeRef nodeRef){
    	Sheet sheet = wb.createSheet("Charter Status Info");
		setCellStyles(wb);
		FileInfo nodeInfo = fileFolderService.getFileInfo(nodeRef);
		final JSONObject jsonProperties = new JSONObject();
		jsonProperties.put("properties",propertiesToJSON(nodeRef, nodeInfo.getProperties(), false));
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) parser.parse(jsonProperties.toString());
			JSONObject properties = (JSONObject) jsonObject.get("properties");

	        insertData(sheet,properties,nodeRef);
	        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	//	Font font = wb.createFont();
	//	font.setFontHeight((short)15);
		
		// Set the cell style
	//	cs = wb.createCellStyle();
//		cs.setFont(font);
	}
	
	private int insertData(Sheet s,JSONObject json,NodeRef nodeRef){
		int rowIndex = 0;
		Row row = null;
		Cell cell = null;
		
		//First Row
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Trip Number");
		cell = row.createCell(1);
		//cell.setCellValue(json.get("{http://www.redwingaero.com/charter/1.0}QuoteNumber").toString());
		// Shouldn't we be using the QuoteNumber instead of PROP_NAME
		String propName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
		if(!propName.isEmpty()){
			cell.setCellValue(propName);
		}
		
		//Second Row
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Customer Name");
		cell = row.createCell(1);
		String propCustomerNumber = (nodeService.getProperty(nodeRef, Model.PROP_CUSTOMERNUMBER) == null) ? "No records found" :nodeService.getProperty(nodeRef, Model.PROP_CUSTOMERNUMBER).toString();
		cell.setCellValue(propCustomerNumber);
		
		rowIndex++;
		row = s.createRow(rowIndex);

		//Routing Information
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("ROUTING");
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("DATE");
		cell = row.createCell(1);
		cell.setCellValue("ROUTING");
		cell = row.createCell(2);
		cell.setCellValue("MODIFIED BY");
		
		String routingData =  (nodeService.getProperty(nodeRef, Model.PROP_ROUTING) == null) ? "No records found" :  json.get("{http://www.redwingaero.com/charter/1.0}Routing").toString();

		
		Object obj = JSONValue.parse(routingData);
		JSONArray routingArray = (JSONArray) obj;
		if(routingData.equalsIgnoreCase("No records found") || routingArray.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 2));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else{
			for(Object o : routingArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("date"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("routing"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}

		rowIndex++;
		row = s.createRow(rowIndex);
		
		//Broker Information
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("BROKER INFORMATION");
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("BROKER NAME");
		cell = row.createCell(1);
		cell.setCellValue("EMAIL");
		cell = row.createCell(2);
		cell.setCellValue("PHONE NO.");
		cell = row.createCell(3);
		cell.setCellValue("ALT PHONE NO.");
		cell = row.createCell(4);
		cell.setCellValue("MODIFIED BY");
		rowIndex++;
		String brokerInfData = (nodeService.getProperty(nodeRef, Model.PROP_BROKERINFORMATION) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}BrokerInformation").toString();
		
		Object objBroker = JSONValue.parse(brokerInfData);
		JSONArray brokerInfArray = (JSONArray) objBroker;
		if(brokerInfData.equalsIgnoreCase("No records found") || brokerInfArray.size() == 0){
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 4));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
			
		}else{
			for(Object o : brokerInfArray){
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("brokerName"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("email"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("phoneNumber"));
				cell = row.createCell(3);
				cell.setCellValue((String)data.get("altPhone"));
				cell = row.createCell(4);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//Legs  
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Legs");
	
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("DATE");
		cell = row.createCell(1);
		cell.setCellValue("IATA");
		cell = row.createCell(2);
		cell.setCellValue("CALLED");
		cell = row.createCell(3);
		cell.setCellValue("FBO");
		cell = row.createCell(4);
		cell.setCellValue("FUELER");
		cell = row.createCell(5);
		cell.setCellValue("PRICE");
		cell = row.createCell(6);
		cell.setCellValue("MIN UPLIFT");
		cell = row.createCell(7);
		cell.setCellValue("SAVE PRICE QUOTE");
		cell = row.createCell(8);
		cell.setCellValue("MODIFIED BY");
		
		String legsData = (nodeService.getProperty(nodeRef, Model.PROP_LEGS) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}Legs").toString();
		Object objLegs = JSONValue.parse(legsData);
		JSONArray legsArray = (JSONArray) objLegs;
		if(legsData.equalsIgnoreCase("No records found") || legsArray.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 8));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else
		{
			for(Object o : legsArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("date"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("iata"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("called"));
				cell = row.createCell(3);
				cell.setCellValue((String)data.get("fbo"));
				cell = row.createCell(4);
				cell.setCellValue((String)data.get("fueler"));
				cell = row.createCell(5);
				cell.setCellValue((String)data.get("price"));
				cell = row.createCell(6);
				cell.setCellValue((String)data.get("minUpLift"));
				cell = row.createCell(7);
				cell.setCellValue((String)data.get("savePriceQuote"));
				cell = row.createCell(8);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//Itinerary
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Itinerary");
		
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("DATE");
		cell = row.createCell(1);
		cell.setCellValue("ROUTING");
		cell = row.createCell(2);
		cell.setCellValue("SENT TO");
		cell = row.createCell(3);
		cell.setCellValue("SENT TO NAME");
		cell = row.createCell(4);
		cell.setCellValue("MODIFIED BY");
		
		String itineraryData = (nodeService.getProperty(nodeRef, Model.PROP_ITINERARY) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}Itinerary").toString();
		Object objItinerary = JSONValue.parse(itineraryData);
		JSONArray itineraryArray = (JSONArray) objItinerary;
		if(itineraryData.equalsIgnoreCase("No records found") || itineraryArray.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 4));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else{
			for(Object o : itineraryArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("date"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("routing"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("sentTo"));
				cell = row.createCell(3);
				cell.setCellValue((String)data.get("sentToName"));
				cell = row.createCell(4);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//Credit Card Authorization
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Credit Card Authorization");
		
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("AUTHORIZED AMT");
		cell = row.createCell(1);
		cell.setCellValue("AUTH DATE");
		cell = row.createCell(2);
		cell.setCellValue("LAST 4 CARD NO");
		cell = row.createCell(3);
		cell.setCellValue("MODIFIED BY");
		
		String ccAuthData = (nodeService.getProperty(nodeRef, Model.PROP_CREDITCARDAUTHORIZATION) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}CreditCardAuthorization").toString();
		Object objCCAuth= JSONValue.parse(ccAuthData);
		JSONArray ccAuthArray = (JSONArray) objCCAuth;
		if(ccAuthData.equalsIgnoreCase("No records found") || ccAuthArray.size() == 0){
			rowIndex++;
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));
			row = s.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else{
			for(Object o : ccAuthArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("authorizedAMT"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("authDate"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("last4CardNo"));
				cell = row.createCell(3);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//Catering
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Catering");
		
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("CATERING DATE");
		cell = row.createCell(1);
		cell.setCellValue("VENDOR NAME");
		cell = row.createCell(2);
		cell.setCellValue("CONTACT INFO");
		cell = row.createCell(3);
		cell.setCellValue("ORDER DETAILS");
		cell = row.createCell(4);
		cell.setCellValue("PAID");
		cell = row.createCell(5);
		cell.setCellValue("ORDERED");
		cell = row.createCell(6);
		cell.setCellValue("MODIFIED BY");
		
		String cateringData = (nodeService.getProperty(nodeRef, Model.PROP_CATERING) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}Catering").toString();
		Object objCatering = JSONValue.parse(cateringData);
		JSONArray cateringArray = (JSONArray) objCatering;
		if(cateringData.equalsIgnoreCase("No records found") || cateringArray.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 6));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else{
			for(Object o : cateringArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("date"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("vendorName"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("contactInfo"));
				cell = row.createCell(3);
				cell.setCellValue((String)data.get("orderDetails"));
				cell = row.createCell(4);
				cell.setCellValue((String)data.get("paid"));
				cell = row.createCell(5);
				cell.setCellValue((String)data.get("ordered"));
				cell = row.createCell(6);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}

		rowIndex++;
		row = s.createRow(rowIndex);
		
		//Additional Charges
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("Additional Charges");
		
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("TYPE");
		cell = row.createCell(1);
		cell.setCellValue("AMOUNT");
		cell = row.createCell(2);
		cell.setCellValue("NOTES/DESCRIPTION");
		cell = row.createCell(3);
		cell.setCellValue("MODIFIED BY");
		
		String addChargesData = (nodeService.getProperty(nodeRef, Model.PROP_ADDITIONALCHARGES) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}AdditionalCharges").toString();
		Object objAddCharges = JSONValue.parse(addChargesData);
		JSONArray addChargesArray = (JSONArray) objAddCharges;
		if(addChargesData.equalsIgnoreCase("No records found") || addChargesArray.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 4));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else{
			for(Object o : addChargesArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("type"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("amount"));
				cell = row.createCell(2);
				cell.setCellValue((String)data.get("notes"));
				cell = row.createCell(3);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}
		
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//SHRED CC FORM
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("SHRED CREDIT CARD FORM");
		cell = row.createCell(1);
		String shredCCForm = (nodeService.getProperty(nodeRef, Model.PROP_SHREDCREDITCARDFORM) == null) ? "No records found" : nodeService.getProperty(nodeRef, Model.PROP_SHREDCREDITCARDFORM).toString();
		cell.setCellValue(shredCCForm);
		
		//COMPILE FINAL INVOICE FOR ACCOUNTING
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("COMPILE FINAL INVOICE FOR ACCOUNTING");
		cell = row.createCell(1);
		String compileFinalInvoiceForAccouting = (nodeService.getProperty(nodeRef, Model.PROP_COMPILEFINALINVOICEFORAUDITING) == null) ? "No records found" : nodeService.getProperty(nodeRef, Model.PROP_COMPILEFINALINVOICEFORAUDITING).toString();
		cell.setCellValue(compileFinalInvoiceForAccouting);
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//NOTES
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("NOTES");
		
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("NOTES");
		cell = row.createCell(1);
		cell.setCellValue("MODIFIED BY");
		
		String notesData = (nodeService.getProperty(nodeRef, Model.PROP_NOTES) == null) ? "No records found" : json.get("{http://www.redwingaero.com/charter/1.0}Notes").toString();
		Object objNotes = JSONValue.parse(notesData);
		JSONArray notesArray = (JSONArray) objNotes;
		if(notesData.equalsIgnoreCase("No records found") || notesArray.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			s.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 2));
			cell = row.createCell(0);
			cell.setCellValue("No records found");
		}else{
			for(Object o : notesArray){
				rowIndex++;
				JSONObject data = (JSONObject) o;
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String) data.get("notes"));
				cell = row.createCell(1);
				cell.setCellValue((String)data.get("modifiedBy"));
			}
		}
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//CONTAINS
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("CONTAINS");
		
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		if(children.size() == 0){
			rowIndex++;
			row = s.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue("No record found");
		}else{
			for(ChildAssociationRef child : children){
				rowIndex++;
				NodeRef childNodeRef = child.getChildRef();
				row = s.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((String)nodeService.getProperty(childNodeRef, ContentModel.PROP_NAME));
			}
		}
		
		rowIndex++;
		row = s.createRow(rowIndex);
		
		//CHARTER STATUS
		rowIndex++;
		row = s.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("CHARTER STATUS");
		cell = row.createCell(1);
		String charterStatus = (nodeService.getProperty(nodeRef, Model.PROP_CHARTERSTATUS) == null) ? "No records found" : nodeService.getProperty(nodeRef, Model.PROP_CHARTERSTATUS).toString();
		cell.setCellValue(charterStatus);
		
		
		return rowIndex;		
	}
}

