package com.redwingaero.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.rule.ruletrigger.RuleTriggerAbstractBase;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.redwingaero.model.Model;

public class InvoicePolicy extends RuleTriggerAbstractBase implements
		NodeServicePolicies.OnDeleteAssociationPolicy,
		NodeServicePolicies.OnCreateAssociationPolicy {
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		try {
			nodeService
					.removeAssociation(nodeAssocRef.getTargetRef(),
							nodeAssocRef.getSourceRef(),
							getReverseQnameAssociationName(nodeAssocRef
									.getTypeQName()));
		} catch (IllegalArgumentException exception) {

		}

	}

	public void onCreateAssociation(AssociationRef nodeAssocRef){	
		
		try {
			nodeService
			.createAssociation(nodeAssocRef.getTargetRef(),
					nodeAssocRef.getSourceRef(),
					Model.PROP_ASSO_DOCUMENT);	
		
		} catch (Exception exception) {
			System.out.println(exception);
		}
	}

	public QName getReverseQnameAssociationName(QName qName) {
		if (qName.isMatch(Model.PROP_ASSO_INVOICE)) {
			qName = Model.PROP_ASSO_PURCHASE;
		}
		else if (qName.isMatch(Model.PROP_ASSO_PURCHASE)) {
			qName = Model.PROP_ASSO_INVOICE;
		}
		return qName;
	}

	public void registerRuleTrigger() {
		this.policyComponent.bindAssociationBehaviour(QName.createQName(
				NamespaceService.ALFRESCO_URI, "onCreateAssociation"), this,
				new JavaBehaviour(this, "onCreateAssociation"));
		
		this.policyComponent.bindAssociationBehaviour(QName.createQName(
				NamespaceService.ALFRESCO_URI, "onDeleteAssociation"), this,
				new JavaBehaviour(this, "onDeleteAssociation"));

	}

}