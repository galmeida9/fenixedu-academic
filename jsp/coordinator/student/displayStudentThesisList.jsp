<%@ taglib uri="/WEB-INF/jsf_core.tld" prefix="f"%>
<%@ taglib uri="/WEB-INF/jsf_tiles.tld" prefix="ft"%>
<%@ taglib uri="/WEB-INF/html_basic.tld" prefix="h"%>

<style type="text/css">
.solidBorderClass {
	border-style: solid;
	border-width: 1px
}
.boldFontClass {
	font-weight: bold
}
</style>

<ft:tilesView definition="definition.coordinator.two-column" attributeName="body-inline">
<f:loadBundle basename="ServidorApresentacao/ApplicationResources" var="bundle"/>
<f:loadBundle basename="ServidorApresentacao/EnumerationResources" var="bundleEnumeration"/>

	<h:dataTable value="#{listStudentThesis.masterDegreeThesisDataVersions}" var="masterDegreeThesisDataVersion" cellpadding="0">
		<h:column>
			<h:panelGrid columns="1" styleClass="solidBorderClass" width="100%">
				<h:panelGrid columns="3" width="100%">
					<h:outputText value="#{bundle['label.coordinator.studentNumber']}" styleClass="boldFontClass" />
					<h:outputText value="#{bundle['label.coordinator.studentName']}" styleClass="boldFontClass" />
					<h:outputText value="#{bundle['label.coordinator.planState']}" styleClass="boldFontClass" />
					<h:outputText value="#{masterDegreeThesisDataVersion.infoMasterDegreeThesis.infoStudentCurricularPlan.infoStudent.number}" />
					<h:outputText value="#{masterDegreeThesisDataVersion.infoMasterDegreeThesis.infoStudentCurricularPlan.infoStudent.infoPerson.nome}" />
					<h:outputText value="#{masterDegreeThesisDataVersion.infoMasterDegreeThesis.infoStudentCurricularPlan.currentState.name}" />				
				</h:panelGrid>
				<h:outputText value=" " />
				<h:panelGrid columns="1" >
					<h:outputText value="#{bundle['label.coordinator.title']}" styleClass="boldFontClass"/>
					<h:outputText value="#{masterDegreeThesisDataVersion.dissertationTitle}" />
				</h:panelGrid>
				<h:outputText value=" " />
				<h:dataTable value="#{masterDegreeThesisDataVersion.allGuiders}" var="guider" columnClasses="solidBorderClass" headerClass="solidBorderClass" cellspacing="0" width="100%">
					<h:column>
						<h:outputFormat value="#{bundleEnumeration[guider.guiderType]}" styleClass="boldFontClass"/>
					</h:column>			
					<h:column >
						<f:facet name="header">
							<h:outputText value="#{bundle['label.coordinator.guiderNumber']}" />		
						</f:facet>
						<h:outputText value="#{(!empty guider.guiderNumber) ? guider.guiderNumber : '-'}" />
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="#{bundle['label.coordinator.name']}" />		
						</f:facet>
						<h:outputText value="#{guider.guiderName}" />				
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="#{bundle['label.coordinator.institution']}" />		
						</f:facet>
						<h:outputText value="#{guider.institutionName}" />				
					</h:column>
				</h:dataTable>					
			</h:panelGrid>
		</h:column>
	</h:dataTable>

</ft:tilesView>