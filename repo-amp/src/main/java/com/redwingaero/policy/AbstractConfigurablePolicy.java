package com.redwingaero.policy;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryListener;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.log4j.Logger;


public abstract class AbstractConfigurablePolicy implements DictionaryListener {

    protected PolicyComponent policyComponent;
    protected ServiceRegistry serviceRegistry;
    private DictionaryDAO dictionaryDAO;
    protected Repository repository;

    protected final Logger logger = Logger.getLogger(AbstractConfigurablePolicy.class);

    public abstract void bindBehavior();

    public void init() {
        logger.debug("init(), registering as dictionary listener.");
        dictionaryDAO.register(this);
    }

    public void setPolicyComponent(final PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setServiceRegistry(final ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public NodeService getNodeService() {
        return serviceRegistry.getNodeService();
    }

    public CategoryService getCategoryService() {
        return serviceRegistry.getCategoryService();
    }
    public SearchService getSearchService() {
        return serviceRegistry.getSearchService();
    }
    public FileFolderService getFileFolderService() {
    	return serviceRegistry.getFileFolderService();
    }
    public LockService getLockService() {
    	return serviceRegistry.getLockService();
    }
    public CheckOutCheckInService getCOCIService() {
    	return serviceRegistry.getCheckOutCheckInService();
    }
    
    public void setDictionaryDAO(final DictionaryDAO dictionaryDAO) {
        this.dictionaryDAO = dictionaryDAO;
    }

    public void setRepository(final Repository repository) {
        this.repository = repository;
    }

    @Override
    public void onDictionaryInit() {
    }

    @Override
    public void afterDictionaryDestroy() {
    }

    @Override
    public void afterDictionaryInit() {
        logger.debug("afterDictionaryInit()");
        bindBehavior();
    }

}
