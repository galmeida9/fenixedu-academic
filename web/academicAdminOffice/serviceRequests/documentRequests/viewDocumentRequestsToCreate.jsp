<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<%@ page import="net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequestType" %>

<html:xhtml/>

<em><bean:message key="label.academicAdminOffice" bundle="ACADEMIC_OFFICE_RESOURCES"/></em>
<h2><bean:message key="documentRequests" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>
<h3 class="mtop15"><bean:message key="label.documentRequests.confirmation" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>

<logic:messagesPresent message="true">
	<span class="error"><!-- Error messages go here -->
		<html:messages id="message" message="true" bundle="STUDENT_RESOURCES">
			<bean:write name="message"/>
		</html:messages>
	</span>
</logic:messagesPresent>

<logic:notEmpty name="warningsToReport">
	<p class="warning0"><bean:message key="document.request.warnings.title"/></p>
	<ul>
		<logic:iterate id="warningToReport" name="warningsToReport">
			<li><bean:message name="warningToReport"/></li>
		</logic:iterate>
	</ul>
</logic:notEmpty>


<fr:form action="/documentRequestsManagement.do?method=create">

	<fr:edit 
		visible="false"
		name="documentRequestCreateBean" 
		type="net.sourceforge.fenixedu.dataTransferObject.degreeAdministrativeOffice.serviceRequest.documentRequest.DocumentRequestCreateBean"
		schema="DocumentRequestCreateBean.chooseDocumentRequestType"/>

	<fr:view schema="DocumentRequestCreateBean.chooseDocumentRequestType" name="documentRequestCreateBean">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle4 thright thlight mvert0"/>
			<fr:property name="columnClasses" value="width14em,width20em,tdclear tderror1"/>
		</fr:layout>	
	</fr:view>
	
	<logic:present name="additionalInformationSchemaName">
		<bean:define id="additionalInformationSchemaName" name="additionalInformationSchemaName" type="java.lang.String"/>	
		<fr:view name="documentRequestCreateBean" schema="<%= additionalInformationSchemaName + ".view" %>" 
			type="net.sourceforge.fenixedu.dataTransferObject.degreeAdministrativeOffice.serviceRequest.documentRequest.DocumentRequestCreateBean" >
			<fr:layout name="tabular">
				<fr:property name="classes" value="tstyle4 thright thlight mvert0"/>
				<fr:property name="columnClasses" value="width14em,width20em,tdclear tderror1"/>
			</fr:layout>	
		</fr:view>		
	</logic:present>
	
	<logic:notEqual name="documentRequestCreateBean" property="chosenDocumentRequestType" value="<%=DocumentRequestType.DIPLOMA_REQUEST.name()%>">	
	
		<fr:view name="documentRequestCreateBean" schema="DocumentRequestCreateBean.purposes" 
			type="net.sourceforge.fenixedu.dataTransferObject.degreeAdministrativeOffice.serviceRequest.documentRequest.DocumentRequestCreateBean">
			<fr:layout name="tabular">
				<fr:property name="classes" value="tstyle4 thright thlight mvert0"/>
				<fr:property name="columnClasses" value="width14em,width20em,tdclear tderror1"/>
			</fr:layout>	
		</fr:view>

	</logic:notEqual>
	
	<logic:equal name="documentRequestCreateBean" property="chosenDocumentRequestType.canBeFreeProcessed" value="true">
		<fr:view name="documentRequestCreateBean" schema="DocumentRequestCreateBean.freeProcessed" 
			type="net.sourceforge.fenixedu.dataTransferObject.degreeAdministrativeOffice.serviceRequest.documentRequest.DocumentRequestCreateBean">
			<fr:layout name="tabular">
				<fr:property name="classes" value="tstyle4 thright thlight mvert0"/>
				<fr:property name="columnClasses" value="width14em,width20em,tdclear tderror1"/>
			</fr:layout>	
		</fr:view>	
	</logic:equal>
	
	<p class="mtop15">
		<html:submit><bean:message key="button.confirm" bundle="ACADEMIC_OFFICE_RESOURCES"/></html:submit>
	</p>
	
</fr:form>
