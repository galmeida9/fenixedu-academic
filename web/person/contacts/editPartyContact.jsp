<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<html:xhtml />
<bean:define id="partyContactClass" scope="request" name="partyContactClass" />
<bean:define id="contactType" name="partyContact" property="type.name" />

<em><bean:message key="label.person.main.title" /></em>
<h2><bean:message key="<%= "label.partyContacts.edit" +  partyContactClass %>" /></h2>

<html:messages id="message" message="true" bundle="ACADEMIC_OFFICE_RESOURCES">
    <p><span class="error0"><!-- Error messages go here --><bean:write name="message" /></span>
    </p>
</html:messages>

<fr:edit id="edit-contact" name="partyContact" action="/partyContacts.do?method=editPartyContact"
    schema="<%= "contacts." + (contactType.equals("INSTITUTIONAL") ? "Institutional." : "") + partyContactClass + ".manage-student" %>">
    <fr:layout name="tabular-editable">
        <fr:property name="classes" value="tstyle5 thlight thright thmiddle" />
        <fr:property name="columnClasses" value=",,tdclear tderror1" />
    </fr:layout>
    <fr:destination name="postback-set-public"
        path="/partyContacts.do?method=postbackSetPublic&form=edit" />
    <fr:destination name="postback-set-elements"
        path="/partyContacts.do?method=postbackSetElements&form=edit" />
    <fr:destination name="invalid" path="/partyContacts.do?method=invalid&form=edit"/>
    <fr:destination name="cancel" path="/partyContacts.do?method=backToShowInformation" />
</fr:edit>
