<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ page import="net.sourceforge.fenixedu.presentationTier.Action.resourceAllocationManager.utils.SessionConstants" %>
<tiles:insert page="/layout/teacherLayout_2col.jsp" flush="true">
  <tiles:put name="serviceName" value="Portal Docente" />
  <tiles:put name="institutionName" value="Instituto Superior T&eacute;cnico" />
  <tiles:put name="navGeral" value="/teacher/commonNavGeralTeacher.jsp" />
  <tiles:put name="executionCourseName" value="/teacher/executionCourseName.jsp" />
  <tiles:put name="body" value="/teacher/insertAnnouncement_bd.jsp" />
  <tiles:put name="navLocal" value="/teacher/commons/executionCourseAdministrationNavbar.jsp" type="page"/>
  <tiles:put name="footer" value="/copyright.jsp" />
</tiles:insert>