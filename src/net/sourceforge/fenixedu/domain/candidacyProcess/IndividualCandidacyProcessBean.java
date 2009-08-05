package net.sourceforge.fenixedu.domain.candidacyProcess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.dataTransferObject.person.ChoosePersonBean;
import net.sourceforge.fenixedu.dataTransferObject.person.PersonBean;
import net.sourceforge.fenixedu.domain.DomainReference;
import net.sourceforge.fenixedu.domain.ExecutionInterval;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.candidacy.CandidacyInformationBean;
import net.sourceforge.fenixedu.domain.person.RoleType;

import org.joda.time.LocalDate;

abstract public class IndividualCandidacyProcessBean implements Serializable {

    private static final long serialVersionUID = 2860833709120576930L;

    // this must be set to false if you want to use external persons
    private Boolean internalPersonCandidacy = Boolean.TRUE;

    private DomainReference<CandidacyProcess> candidacyProcess;

    private DomainReference<DegreeOfficePublicCandidacyHashCode> publicCandidacyHashCode;

    private DomainReference<IndividualCandidacyProcess> individualCandidacyProcess;

    private ChoosePersonBean choosePersonBean;

    private PersonBean personBean;

    private LocalDate candidacyDate;

    private CandidacyInformationBean candidacyInformationBean;

    private String observations;

    private Boolean processChecked;

    private String personNumber;

    private Boolean publicCandidacyCreationOrEdition;

    private CandidacyProcessDocumentUploadBean documentIdentificationDocument;
    private CandidacyProcessDocumentUploadBean paymentDocument;
    private CandidacyProcessDocumentUploadBean habilitationCertificationDocument;
    private CandidacyProcessDocumentUploadBean firstCycleAccessHabilitationDocument;
    private CandidacyProcessDocumentUploadBean vatCatCopyDocument;
    private CandidacyProcessDocumentUploadBean photoDocument;

    private List<FormationBean> formationConcludedBeanList;
    private List<FormationBean> formationNonConcludedBeanList;

    protected Boolean honorAgreement;

    protected Boolean utlStudent;

    public IndividualCandidacyProcessBean() {
	setFormationConcludedBeanList(new ArrayList<FormationBean>());
	setFormationNonConcludedBeanList(new ArrayList<FormationBean>());
	setPublicCandidacy(Boolean.TRUE);
    }

    public Boolean getInternalPersonCandidacy() {
	return internalPersonCandidacy;
    }

    public void setInternalPersonCandidacy(Boolean internalPersonCandidacy) {
	this.internalPersonCandidacy = internalPersonCandidacy;
    }

    public CandidacyProcess getCandidacyProcess() {
	return (this.candidacyProcess != null) ? this.candidacyProcess.getObject() : null;
    }

    public void setCandidacyProcess(CandidacyProcess candidacyProcess) {
	this.candidacyProcess = (candidacyProcess != null) ? new DomainReference<CandidacyProcess>(candidacyProcess) : null;
    }

    public boolean hasCandidacyProcess() {
	return getCandidacyProcess() != null;
    }

    public ChoosePersonBean getChoosePersonBean() {
	return choosePersonBean;
    }

    public void setChoosePersonBean(ChoosePersonBean choosePersonBean) {
	this.choosePersonBean = choosePersonBean;
    }

    public boolean hasChoosenPerson() {
	return getChoosePersonBean().hasPerson();
    }

    public void removeChoosePersonBean() {
	setChoosePersonBean(null);
    }

    public PersonBean getPersonBean() {
	return personBean;
    }

    public void setPersonBean(PersonBean personBean) {
	this.personBean = personBean;
    }

    public LocalDate getCandidacyDate() {
	return candidacyDate;
    }

    public void setCandidacyDate(final LocalDate candidacyDate) {
	this.candidacyDate = candidacyDate;
    }

    public Person getOrCreatePersonFromBean() {
	if (!getPersonBean().hasPerson()) {
	    Person person = new Person(getPersonBean());
	    return person;
	}

	if (getPersonBean().getPerson().hasRole(RoleType.EMPLOYEE)) {
	    return getPersonBean().getPerson();
	} else if (!getPersonBean().getPerson().getPersonRoles().isEmpty() && this.isPublicCandidacy()) {
	    return getPersonBean().getPerson();
	} else if (getPersonBean().getPerson().getPersonRoles().isEmpty() && this.isPublicCandidacy()) {
	    return getPersonBean().getPerson().editByPublicCandidate(personBean);
	} else {
	    return getPersonBean().getPerson().edit(getPersonBean());
	}
    }

    public ExecutionInterval getCandidacyExecutionInterval() {
	return hasCandidacyProcess() ? getCandidacyProcess().getCandidacyExecutionInterval() : null;
    }

    public CandidacyInformationBean getCandidacyInformationBean() {
	return candidacyInformationBean;
    }

    public void setCandidacyInformationBean(CandidacyInformationBean candidacyInformationBean) {
	this.candidacyInformationBean = candidacyInformationBean;
    }

    public void copyInformationToCandidacyBean() {
	getCandidacyInformationBean().setMaritalStatus(getPersonBean().getMaritalStatus());
	getCandidacyInformationBean().setCountryOfResidence(getPersonBean().getCountryOfResidence());
    }

    public CandidacyProcessDocumentUploadBean getDocumentIdentificationDocument() {
	return documentIdentificationDocument;
    }

    public void setDocumentIdentificationDocument(CandidacyProcessDocumentUploadBean documentIdentificationDocument) {
	this.documentIdentificationDocument = documentIdentificationDocument;
    }

    public CandidacyProcessDocumentUploadBean getPaymentDocument() {
	return paymentDocument;
    }

    public void setPaymentDocument(CandidacyProcessDocumentUploadBean paymentDocument) {
	this.paymentDocument = paymentDocument;
    }

    public CandidacyProcessDocumentUploadBean getHabilitationCertificationDocument() {
	return habilitationCertificationDocument;
    }

    public void setHabilitationCertificationDocument(CandidacyProcessDocumentUploadBean habilitationCertificationDocument) {
	this.habilitationCertificationDocument = habilitationCertificationDocument;
    }

    public CandidacyProcessDocumentUploadBean getFirstCycleAccessHabilitationDocument() {
	return firstCycleAccessHabilitationDocument;
    }

    public void setFirstCycleAccessHabilitationDocument(CandidacyProcessDocumentUploadBean firstCycleAccessHabilitationDocument) {
	this.firstCycleAccessHabilitationDocument = firstCycleAccessHabilitationDocument;
    }

    public CandidacyProcessDocumentUploadBean getVatCatCopyDocument() {
	return vatCatCopyDocument;
    }

    public void setVatCatCopyDocument(CandidacyProcessDocumentUploadBean vatCatCopyDocument) {
	this.vatCatCopyDocument = vatCatCopyDocument;
    }

    public IndividualCandidacyProcess getIndividualCandidacyProcess() {
	return this.individualCandidacyProcess != null ? this.individualCandidacyProcess.getObject() : null;
    }

    public void setIndividualCandidacyProcess(IndividualCandidacyProcess individualCandidacyProcess) {
	this.individualCandidacyProcess = individualCandidacyProcess != null ? new DomainReference<IndividualCandidacyProcess>(
		individualCandidacyProcess) : null;
    }

    public DegreeOfficePublicCandidacyHashCode getPublicCandidacyHashCode() {
	return (this.publicCandidacyHashCode != null) ? this.publicCandidacyHashCode.getObject() : null;
    }

    public void setPublicCandidacyHashCode(DegreeOfficePublicCandidacyHashCode publicCandidacyHashCode) {
	this.publicCandidacyHashCode = (publicCandidacyHashCode != null) ? new DomainReference<DegreeOfficePublicCandidacyHashCode>(
		publicCandidacyHashCode)
		: null;
    }

    public String getObservations() {
	return this.observations;
    }

    public void setObservations(String value) {
	this.observations = value;
    }

    public CandidacyProcessDocumentUploadBean getPhotoDocument() {
	return this.photoDocument;
    }

    public void setPhotoDocument(CandidacyProcessDocumentUploadBean bean) {
	this.photoDocument = bean;
    }

    public List<FormationBean> getFormationConcludedBeanList() {
	return this.formationConcludedBeanList;
    }

    public void setFormationConcludedBeanList(List<FormationBean> formationConcludedBeanList) {
	this.formationConcludedBeanList = formationConcludedBeanList;
    }

    public List<FormationBean> getFormationNonConcludedBeanList() {
	return this.formationNonConcludedBeanList;
    }

    public void setFormationNonConcludedBeanList(List<FormationBean> formationNonConcludedBeanList) {
	this.formationNonConcludedBeanList = formationNonConcludedBeanList;
    }

    public void addConcludedFormationBean() {
	this.formationConcludedBeanList.add(new FormationBean(Boolean.TRUE));
    }

    public void addNonConcludedFormationBean() {
	this.formationNonConcludedBeanList.add(new FormationBean(Boolean.FALSE));
    }

    public void removeFormationConcludedBean(final int index) {
	this.formationConcludedBeanList.remove(index);
    }

    public void removeFormationNonConcludedBean(final int index) {
	this.formationNonConcludedBeanList.remove(index);
    }

    protected void initializeFormation(List<Formation> formations) {
	this.formationConcludedBeanList = new ArrayList<FormationBean>();
	this.formationNonConcludedBeanList = new ArrayList<FormationBean>();

	for (Formation formation : formations) {
	    if (formation.getConcluded()) {
		this.formationConcludedBeanList.add(new FormationBean(formation));
	    } else {
		this.formationNonConcludedBeanList.add(new FormationBean(formation));
	    }
	}
    }

    protected void initializeDocumentUploadBeans() {
	this.documentIdentificationDocument = new CandidacyProcessDocumentUploadBean(
		IndividualCandidacyDocumentFileType.DOCUMENT_IDENTIFICATION);
	this.paymentDocument = new CandidacyProcessDocumentUploadBean(IndividualCandidacyDocumentFileType.PAYMENT_DOCUMENT);
	this.habilitationCertificationDocument = new CandidacyProcessDocumentUploadBean(
		IndividualCandidacyDocumentFileType.HABILITATION_CERTIFICATE_DOCUMENT);
	this.firstCycleAccessHabilitationDocument = new CandidacyProcessDocumentUploadBean(
		IndividualCandidacyDocumentFileType.FIRST_CYCLE_ACCESS_HABILITATION_DOCUMENT);
	this.vatCatCopyDocument = new CandidacyProcessDocumentUploadBean(IndividualCandidacyDocumentFileType.VAT_CARD_DOCUMENT);
	setPhotoDocument(new CandidacyProcessDocumentUploadBean(IndividualCandidacyDocumentFileType.PHOTO));
    }

    public Boolean getProcessChecked() {
	return this.processChecked;
    }

    public void setProcessChecked(Boolean value) {
	this.processChecked = value;
    }

    public String getPersonNumber() {
	return this.personNumber;
    }

    public void setPersonNumber(String personNumber) {
	this.personNumber = personNumber;
    }

    public Boolean isPublicCandidacy() {
	return this.publicCandidacyCreationOrEdition;
    }

    public void setPublicCandidacy(Boolean value) {
	this.publicCandidacyCreationOrEdition = value;
    }

    public Boolean getHonorAgreement() {
	return this.honorAgreement;
    }

    public void setHonorAgreement(Boolean value) {
	this.honorAgreement = value;
    }

    public Boolean getUtlStudent() {
	return utlStudent;
    }

    public void setUtlStudent(Boolean utlStudent) {
	this.utlStudent = utlStudent;
    }
}
