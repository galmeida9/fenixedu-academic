package net.sourceforge.fenixedu.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.fenixedu.commons.CollectionUtils;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOfficeType;
import net.sourceforge.fenixedu.domain.curriculum.CurricularCourseType;
import net.sourceforge.fenixedu.domain.curriculum.EnrollmentCondition;
import net.sourceforge.fenixedu.domain.curriculum.EnrollmentState;
import net.sourceforge.fenixedu.domain.curriculum.EnrolmentEvaluationType;
import net.sourceforge.fenixedu.domain.degreeStructure.DegreeModule;
import net.sourceforge.fenixedu.domain.enrolment.CurriculumModuleEnroledWrapper;
import net.sourceforge.fenixedu.domain.enrolment.IDegreeModuleToEvaluate;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.finalDegreeWork.Proposal;
import net.sourceforge.fenixedu.domain.log.EnrolmentLog;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.studentCurriculum.CurriculumGroup;
import net.sourceforge.fenixedu.domain.thesis.Thesis;
import net.sourceforge.fenixedu.domain.util.FactoryExecutor;
import net.sourceforge.fenixedu.util.EnrolmentAction;
import net.sourceforge.fenixedu.util.EnrolmentEvaluationState;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

/**
 * @author dcs-rjao
 * 
 * 24/Mar/2003
 */

public class Enrolment extends Enrolment_Base implements IEnrolment {

    static final public Comparator<Enrolment> COMPARATOR_BY_EXECUTION_PERIOD_AND_ID = new Comparator<Enrolment>() {
        public int compare(Enrolment o1, Enrolment o2) {
            final ComparatorChain comparatorChain = new ComparatorChain();
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_EXECUTION_PERIOD);
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_ID);
            
            return comparatorChain.compare(o1, o2);
        }
    };

    static final public Comparator<Enrolment> REVERSE_COMPARATOR_BY_EXECUTION_PERIOD_AND_ID = new Comparator<Enrolment>() {
	public int compare(Enrolment o1, Enrolment o2) {
	    return -COMPARATOR_BY_EXECUTION_PERIOD_AND_ID.compare(o1, o2);
	}
    };
    
    static final public Comparator<Enrolment> COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID = new Comparator<Enrolment>() {
        public int compare(Enrolment o1, Enrolment o2) {
            final ComparatorChain comparatorChain = new ComparatorChain();
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME);
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_ID);
            
            return comparatorChain.compare(o1, o2);
        }
    };
    
    static final public Comparator<Enrolment> COMPARATOR_BY_EXECUTION_YEAR_AND_NAME_AND_ID = new Comparator<Enrolment>() {
        public int compare(Enrolment o1, Enrolment o2) {
            final ComparatorChain comparatorChain = new ComparatorChain();
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_EXECUTION_YEAR_AND_NAME);
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_ID);
            
            return comparatorChain.compare(o1, o2);
        }
    };
    
    static final private Comparator<Enrolment> COMPARATOR_BY_LATEST_ENROLMENT_EVALUATION = new Comparator<Enrolment>() {
        public int compare(Enrolment o1, Enrolment o2) {
            return EnrolmentEvaluation.COMPARATOR_BY_EXAM_DATE.compare(o1.getLatestEnrolmentEvaluation(), o2.getLatestEnrolmentEvaluation());
        }
    };
    
    static final public Comparator<Enrolment> COMPARATOR_BY_LATEST_ENROLMENT_EVALUATION_AND_ID = new Comparator<Enrolment>() {
        public int compare(Enrolment o1, Enrolment o2) {
            final ComparatorChain comparatorChain = new ComparatorChain();
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_LATEST_ENROLMENT_EVALUATION);
            comparatorChain.addComparator(Enrolment.COMPARATOR_BY_ID);
            
            return comparatorChain.compare(o1, o2);
        }
    };
    
    private Integer accumulatedWeight;

    private Double accumulatedEctsCredits;

    public Enrolment() {
	super();
	setRootDomainObject(RootDomainObject.getInstance());
    }

    public Enrolment(StudentCurricularPlan studentCurricularPlan, CurricularCourse curricularCourse,
	    ExecutionPeriod executionPeriod, EnrollmentCondition enrolmentCondition, String createdBy) {
	this();
	initializeAsNew(studentCurricularPlan, curricularCourse, executionPeriod, enrolmentCondition,
		createdBy);
	createEnrolmentLog(studentCurricularPlan.getRegistration(), EnrolmentAction.ENROL);
    }

    public Enrolment(StudentCurricularPlan studentCurricularPlan, CurricularCourse curricularCourse,
	    ExecutionPeriod executionPeriod, EnrollmentCondition enrolmentCondition, String createdBy,
	    Boolean isExtraCurricular) {
	this(studentCurricularPlan, curricularCourse, executionPeriod, enrolmentCondition, createdBy);
	this.setIsExtraCurricular(isExtraCurricular);
    }

    @Override
    public boolean isEnrolment() {
	return true;
    }
    
    public boolean isBolonhaDegree() {
	return getStudentCurricularPlan().isBolonhaDegree();
    }
    
    /**
     * Temporary method, after all degrees migration this is no longer necessary
     * @return
     */
    public boolean isBoxStructure() {
	return getStudentCurricularPlan().isBoxStructure();
    }
   
    @Deprecated
    public EnrollmentCondition getCondition() {
	return getEnrolmentCondition();
    }

    @Deprecated
    public void setCondition(EnrollmentCondition enrollmentCondition) {
	setEnrolmentCondition(enrollmentCondition);
    }

    public boolean isFinal() {
	return getEnrolmentCondition() == EnrollmentCondition.FINAL;
    }

    public boolean isInvisible() {
	return getEnrolmentCondition() == EnrollmentCondition.INVISIBLE;
    }

    public boolean isTemporary() {
	return getEnrolmentCondition() == EnrollmentCondition.TEMPORARY;
    }
    
    public boolean isImpossible() {
	return getEnrolmentCondition() == EnrollmentCondition.IMPOSSIBLE;
    }

    public boolean isSpecialSeason() {
	return hasSpecialSeason();
    }

    // new student structure methods
    public Enrolment(StudentCurricularPlan studentCurricularPlan, CurriculumGroup curriculumGroup,
	    CurricularCourse curricularCourse, ExecutionPeriod executionPeriod,
	    EnrollmentCondition enrolmentCondition, String createdBy) {
	this();
	if (studentCurricularPlan == null || curriculumGroup == null || curricularCourse == null
		|| executionPeriod == null || enrolmentCondition == null || createdBy == null) {
	    throw new DomainException("invalid arguments");
	}
	checkInitConstraints(studentCurricularPlan, curricularCourse, executionPeriod);
	// TODO: check this
	// validateDegreeModuleLink(curriculumGroup, curricularCourse);
	initializeAsNew(studentCurricularPlan, curriculumGroup, curricularCourse, executionPeriod,
		enrolmentCondition, createdBy);
    }

    public Enrolment(StudentCurricularPlan studentCurricularPlan, CurriculumGroup curriculumGroup,
	    CurricularCourse curricularCourse, ExecutionPeriod executionPeriod,
	    EnrollmentCondition enrolmentCondition, String createdBy, Boolean isExtraCurricular) {

	this(studentCurricularPlan, curriculumGroup, curricularCourse, executionPeriod,
		enrolmentCondition, createdBy);
	setIsExtraCurricular(isExtraCurricular);
    }

    protected void checkInitConstraints(StudentCurricularPlan studentCurricularPlan,
	    CurricularCourse curricularCourse, ExecutionPeriod executionPeriod) {
	if (studentCurricularPlan.getEnrolmentByCurricularCourseAndExecutionPeriod(curricularCourse,
		executionPeriod) != null) {
	    throw new DomainException("error.Enrolment.duplicate.enrolment", curricularCourse.getName());
	}
    }

    protected void initializeAsNew(StudentCurricularPlan studentCurricularPlan,
	    CurriculumGroup curriculumGroup, CurricularCourse curricularCourse,
	    ExecutionPeriod executionPeriod, EnrollmentCondition enrolmentCondition, String createdBy) {
	initializeAsNewWithoutEnrolmentEvaluation(studentCurricularPlan, curriculumGroup,
		curricularCourse, executionPeriod, enrolmentCondition, createdBy);
	createEnrolmentEvaluationWithoutGrade();
    }

    protected void initializeAsNewWithoutEnrolmentEvaluation(
	    StudentCurricularPlan studentCurricularPlan, CurriculumGroup curriculumGroup,
	    CurricularCourse curricularCourse, ExecutionPeriod executionPeriod,
	    EnrollmentCondition enrolmentCondition, String createdBy) {
	initializeCommon(studentCurricularPlan, curricularCourse, executionPeriod, enrolmentCondition,
		createdBy);
	setCurriculumGroup(curriculumGroup);
    }

    // end

    public Integer getAccumulatedWeight() {
	return accumulatedWeight;
    }

    public void setAccumulatedWeight(Integer accumulatedWeight) {
	this.accumulatedWeight = accumulatedWeight;
    }

    protected void initializeAsNew(StudentCurricularPlan studentCurricularPlan,
	    CurricularCourse curricularCourse, ExecutionPeriod executionPeriod,
	    EnrollmentCondition enrolmentCondition, String createdBy) {
	initializeAsNewWithoutEnrolmentEvaluation(studentCurricularPlan, curricularCourse,
		executionPeriod, enrolmentCondition, createdBy);
	createEnrolmentEvaluationWithoutGrade();
    }

    private void initializeCommon(StudentCurricularPlan studentCurricularPlan,
	    CurricularCourse curricularCourse, ExecutionPeriod executionPeriod,
	    EnrollmentCondition enrolmentCondition, String createdBy) {
	setCurricularCourse(curricularCourse);
	setWeigth(studentCurricularPlan.isBolonhaDegree() ? curricularCourse.getEctsCredits(executionPeriod) : curricularCourse.getWeigth());
	setEnrollmentState(EnrollmentState.ENROLLED);
	setExecutionPeriod(executionPeriod);
	setEnrolmentEvaluationType(EnrolmentEvaluationType.NORMAL);
	setCreatedBy(createdBy);
	setCreationDateDateTime(new DateTime());
	setEnrolmentCondition(enrolmentCondition);
	createAttend(studentCurricularPlan.getRegistration(), curricularCourse, executionPeriod);
	setIsExtraCurricular(Boolean.FALSE);
    }

    protected void initializeAsNewWithoutEnrolmentEvaluation(
	    StudentCurricularPlan studentCurricularPlan, CurricularCourse curricularCourse,
	    ExecutionPeriod executionPeriod, EnrollmentCondition enrolmentCondition, String createdBy) {
	initializeCommon(studentCurricularPlan, curricularCourse, executionPeriod, enrolmentCondition,
		createdBy);
	setStudentCurricularPlan(studentCurricularPlan);
    }

    public void unEnroll() throws DomainException {

	for (EnrolmentEvaluation eval : getEvaluationsSet()) {

	    if (eval.getEnrolmentEvaluationType().equals(EnrolmentEvaluationType.NORMAL)
		    && eval.getEnrolmentEvaluationState().equals(EnrolmentEvaluationState.TEMPORARY_OBJ)
		    && (eval.getGradeValue() == null || eval.getGradeValue().equals(""))) {
		continue;
	    }
	    else {
		throw new DomainException("error.enrolment.cant.unenroll");
	    }
	}

	delete();
    }

    public void delete() {
	createEnrolmentLog(EnrolmentAction.UNENROL);
	// TODO: falta ver se � dos antigos enrolments ou dos novos
	final Registration registration = getRegistration();

	removeExecutionPeriod();
	removeStudentCurricularPlan();
	removeDegreeModule();
	removeCurriculumGroup();
	getNotNeedToEnrollCurricularCourses().clear();

	Iterator<Attends> attendsIter = getAttendsIterator();
	while (attendsIter.hasNext()) {
	    Attends attends = attendsIter.next();

	    attendsIter.remove();
	    attends.removeEnrolment();

	    if (!attends.hasAnyAssociatedMarks() && !attends.hasAnyStudentGroups()) {
		boolean hasShiftEnrolment = false;
		for (Shift shift : attends.getExecutionCourse().getAssociatedShifts()) {
		    if (shift.hasStudents(registration)) {
			hasShiftEnrolment = true;
			break;
		    }
		}

		if (!hasShiftEnrolment) {
		    attends.delete();
		}
	    }
	}

	Iterator<EnrolmentEvaluation> evalsIter = getEvaluationsIterator();
	while (evalsIter.hasNext()) {
	    EnrolmentEvaluation eval = evalsIter.next();
	    evalsIter.remove();
	    eval.delete();
	}

	Iterator<CreditsInAnySecundaryArea> creditsInAnysecundaryAreaIterator = getCreditsInAnySecundaryAreasIterator();

	while (creditsInAnysecundaryAreaIterator.hasNext()) {
	    CreditsInAnySecundaryArea credits = creditsInAnysecundaryAreaIterator.next();
	    creditsInAnysecundaryAreaIterator.remove();
	    credits.delete();
	}

	Iterator<CreditsInScientificArea> creditsInScientificAreaIterator = getCreditsInScientificAreasIterator();

	while (creditsInScientificAreaIterator.hasNext()) {
	    CreditsInScientificArea credits = creditsInScientificAreaIterator.next();
	    creditsInScientificAreaIterator.remove();
	    credits.delete();
	}

	Iterator<EquivalentEnrolmentForEnrolmentEquivalence> equivalentEnrolmentIterator = getEquivalentEnrolmentForEnrolmentEquivalencesIterator();

	while (equivalentEnrolmentIterator.hasNext()) {
	    EquivalentEnrolmentForEnrolmentEquivalence equivalentEnrolment = equivalentEnrolmentIterator
		    .next();
	    equivalentEnrolmentIterator.remove();
	    equivalentEnrolment.removeEquivalentEnrolment();

	    EnrolmentEquivalence equivalence = equivalentEnrolment.getEnrolmentEquivalence();
	    Enrolment enrolment = equivalence.getEnrolment();

	    equivalence.removeEnrolment();
	    enrolment.delete();
	    equivalentEnrolment.removeEnrolmentEquivalence();

	    equivalentEnrolment.delete();
	    equivalence.delete();
	}

	Iterator<EnrolmentEquivalence> equivalenceIterator = getEnrolmentEquivalencesIterator();

	while (equivalenceIterator.hasNext()) {
	    EnrolmentEquivalence equivalence = equivalenceIterator.next();
	    equivalenceIterator.remove();
	    equivalence.removeEnrolment();

	    Iterator<EquivalentEnrolmentForEnrolmentEquivalence> equivalentRestrictionIterator = equivalence
		    .getEquivalenceRestrictionsIterator();

	    while (equivalentRestrictionIterator.hasNext()) {
		EquivalentEnrolmentForEnrolmentEquivalence equivalentRestriction = equivalentRestrictionIterator
			.next();
		equivalentRestriction.removeEquivalentEnrolment();
		equivalentRestrictionIterator.remove();
		equivalentRestriction.removeEnrolmentEquivalence();

		equivalentRestriction.delete();
	    }
	    equivalence.delete();
	}

	Iterator<Thesis> theses = getThesesIterator();
	while (theses.hasNext()) {
		theses.next().delete();
	}
	
	super.delete();

    }

    public Collection<Enrolment> getBrothers() {
	final Collection<Enrolment> result = new HashSet<Enrolment>();
	
	result.addAll(getStudentCurricularPlan().getEnrolments(getCurricularCourse()));
	result.remove(this);
	
	return result;
    }

    public EnrolmentEvaluation getEnrolmentEvaluationByEnrolmentEvaluationTypeAndGrade(
	    final EnrolmentEvaluationType evaluationType, final String grade) {

	return (EnrolmentEvaluation) CollectionUtils.find(getEvaluationsSet(), new Predicate() {

	    public boolean evaluate(Object o) {
		EnrolmentEvaluation enrolmentEvaluation = (EnrolmentEvaluation) o;
		String evaluationGrade = enrolmentEvaluation.getGradeValue();

		return enrolmentEvaluation.getEnrolmentEvaluationType().equals(evaluationType)
			&& ((grade == null && evaluationGrade == null) || (evaluationGrade != null && evaluationGrade
				.equals(grade)));
	    }

	});
    }

    public EnrolmentEvaluation getEnrolmentEvaluationByEnrolmentEvaluationStateAndType(
	    final EnrolmentEvaluationState state, final EnrolmentEvaluationType type) {
	return (EnrolmentEvaluation) CollectionUtils.find(getEvaluationsSet(), new Predicate() {

	    public boolean evaluate(Object o) {
		EnrolmentEvaluation enrolmentEvaluation = (EnrolmentEvaluation) o;
		return (enrolmentEvaluation.getEnrolmentEvaluationState().equals(state) && enrolmentEvaluation
			.getEnrolmentEvaluationType().equals(type));
	    }

	});
    }

    public List<EnrolmentEvaluation> getEnrolmentEvaluationsByEnrolmentEvaluationState(
	    final EnrolmentEvaluationState evaluationState) {
	List<EnrolmentEvaluation> result = new ArrayList<EnrolmentEvaluation>();
	for (EnrolmentEvaluation evaluation : getEvaluationsSet()) {
	    if (evaluation.getEnrolmentEvaluationState().equals(evaluationState)) {
		result.add(evaluation);
	    }
	}
	return result;
    }

    public List<EnrolmentEvaluation> getEnrolmentEvaluationsByEnrolmentEvaluationType(
	    final EnrolmentEvaluationType evaluationType) {
	List<EnrolmentEvaluation> result = new ArrayList<EnrolmentEvaluation>();
	for (EnrolmentEvaluation evaluation : getEvaluationsSet()) {
	    if (evaluation.getEnrolmentEvaluationType().equals(evaluationType)) {
		result.add(evaluation);
	    }
	}
	return result;
    }

    public EnrolmentEvaluation submitEnrolmentEvaluation(
	    EnrolmentEvaluationType enrolmentEvaluationType, Mark publishedMark, Employee employee,
	    Person personResponsibleForGrade, Date evaluationDate, String observation) {

	EnrolmentEvaluation enrolmentEvaluation = getEnrolmentEvaluationByEnrolmentEvaluationStateAndType(
		EnrolmentEvaluationState.TEMPORARY_OBJ, enrolmentEvaluationType);

	// There can be only one enrolmentEvaluation with Temporary State
	if (enrolmentEvaluation == null) {
	    enrolmentEvaluation = new EnrolmentEvaluation();
	    enrolmentEvaluation.setEnrolment(this);
	}

	// teacher responsible for execution course
	String grade = null;
	if ((publishedMark == null) || (publishedMark.getMark().length() == 0))
	    grade = GradeScale.NA;
	else
	    grade = publishedMark.getMark().toUpperCase();

	enrolmentEvaluation.setGradeValue(grade);

	enrolmentEvaluation.setEnrolmentEvaluationType(enrolmentEvaluationType);
	enrolmentEvaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
	enrolmentEvaluation.setObservation(observation);
	enrolmentEvaluation.setPersonResponsibleForGrade(personResponsibleForGrade);

	enrolmentEvaluation.setEmployee(employee);

	final YearMonthDay yearMonthDay = new YearMonthDay();
	enrolmentEvaluation.setGradeAvailableDateYearMonthDay(yearMonthDay);
	if (evaluationDate != null) {
	    enrolmentEvaluation.setExamDateYearMonthDay(new YearMonthDay(evaluationDate));
	} else {
	    enrolmentEvaluation.setExamDateYearMonthDay(yearMonthDay);
	}

	enrolmentEvaluation.setCheckSum("");

	return enrolmentEvaluation;
    }

    protected void createEnrolmentEvaluationWithoutGrade() {

	EnrolmentEvaluation enrolmentEvaluation = getEnrolmentEvaluationByEnrolmentEvaluationTypeAndGrade(
		EnrolmentEvaluationType.NORMAL, null);

	if (enrolmentEvaluation == null) {
	    enrolmentEvaluation = new EnrolmentEvaluation();
	    enrolmentEvaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
	    enrolmentEvaluation.setEnrolmentEvaluationType(EnrolmentEvaluationType.NORMAL);
	    enrolmentEvaluation.setWhenDateTime(new DateTime());

	    addEvaluations(enrolmentEvaluation);
	}
    }

    private void createAttend(Registration registration, CurricularCourse curricularCourse,
	    ExecutionPeriod executionPeriod) {

	List executionCourses = curricularCourse.getExecutionCoursesByExecutionPeriod(executionPeriod);

	ExecutionCourse executionCourse = null;
	if (executionCourses.size() > 1) {
	    Iterator iterator = executionCourses.iterator();
	    while (iterator.hasNext()) {
		ExecutionCourse executionCourse2 = (ExecutionCourse) iterator.next();
		if (executionCourse2.getExecutionCourseProperties() == null
			|| executionCourse2.getExecutionCourseProperties().isEmpty()) {
		    executionCourse = executionCourse2;
		}
	    }
	} else if (executionCourses.size() == 1) {
	    executionCourse = (ExecutionCourse) executionCourses.get(0);
	}

	if (executionCourse != null) {
	    Attends attend = executionCourse.getAttendsByStudent(registration);

	    if (attend != null) {
		addAttends(attend);
	    } else {
		Attends attendToWrite = new Attends(registration, executionCourse);
		addAttends(attendToWrite);
	    }
	}
    }

    public void createAttends(final Registration registration, final ExecutionCourse executionCourse) {
	final Attends attendsFor = this.getAttendsFor(executionCourse.getExecutionPeriod());
	if (attendsFor != null) {
	    try {
		attendsFor.delete();
	    } catch (DomainException e) {
		throw new DomainException("error.attends.cant.change.attends");
	    }
	}

	Attends attends = new Attends(registration, executionCourse);
	this.addAttends(attends);
    }

    public EnrolmentEvaluation createEnrolmentEvaluationForImprovement(final Employee employee, final ExecutionPeriod executionPeriod) {
	final EnrolmentEvaluation enrolmentEvaluation = new EnrolmentEvaluation(this, EnrolmentEvaluationType.IMPROVEMENT, EnrolmentEvaluationState.TEMPORARY_OBJ, employee, executionPeriod);
	createAttendForImprovement(executionPeriod);
	return enrolmentEvaluation;
    }
    
    private void createAttendForImprovement(final ExecutionPeriod executionPeriod) {
	final Registration registration = getRegistration();
	
	// FIXME this is completly wrong, there may be more than one execution course for this curricular course
	ExecutionCourse currentExecutionCourse = (ExecutionCourse) CollectionUtils.find(
		getCurricularCourse().getAssociatedExecutionCourses(), new Predicate() {

		    public boolean evaluate(Object arg0) {
			ExecutionCourse executionCourse = (ExecutionCourse) arg0;
			if (executionCourse.getExecutionPeriod().equals(executionPeriod))
			    return true;
			return false;
		    }

		});

	if (currentExecutionCourse != null) {
	    List attends = currentExecutionCourse.getAttends();
	    Attends attend = (Attends) CollectionUtils.find(attends, new Predicate() {

		public boolean evaluate(Object arg0) {
		    Attends attend = (Attends) arg0;
		    if (attend.getRegistration().equals(registration))
			return true;
		    return false;
		}

	    });

	    if (attend != null) {
		attend.setEnrolment(this);
	    } else {
		attend = new Attends(registration, currentExecutionCourse);
		attend.setEnrolment(this);
	    }
	}
    }

    public void unEnrollImprovement(final ExecutionPeriod executionPeriod) throws DomainException {
	final EnrolmentEvaluation temporaryImprovement = getImprovementEvaluation();
	if (temporaryImprovement == null) {
	    throw new DomainException("error.enrolment.cant.unenroll.improvement");
	}

	temporaryImprovement.delete();
	Attends attends = getAttendsFor(executionPeriod); 
	if(attends != null) {
	    attends.delete();
	}
    }
    
    public boolean isImprovementForExecutionCourse(ExecutionCourse executionCourse) {
	return getCurricularCourse().hasAssociatedExecutionCourses(executionCourse)
		&& getExecutionPeriod() != executionCourse.getExecutionPeriod();
    }

    public boolean isImprovingInExecutionPeriodFollowingApproval(final ExecutionPeriod improvementExecutionPeriod) {
	final DegreeModule degreeModule = getDegreeModule();
	if (hasImprovement() || !isApproved() || !degreeModule.hasAnyParentContexts(improvementExecutionPeriod)) {
	    throw new DomainException("Enrolment.is.not.in.improvement.conditions");
	}
	
	final ExecutionPeriod enrolmentExecutionPeriod = getExecutionPeriod();
	if (improvementExecutionPeriod.isBeforeOrEquals(enrolmentExecutionPeriod)) {
	    throw new DomainException("Enrolment.cannot.improve.enrolment.prior.to.its.execution.period");
	}
	
	if (improvementExecutionPeriod == enrolmentExecutionPeriod.getNextExecutionPeriod()) {
	    return true;
	}
	
	for (ExecutionPeriod executionPeriod = enrolmentExecutionPeriod.getNextExecutionPeriod(); executionPeriod != improvementExecutionPeriod && executionPeriod != null; executionPeriod = executionPeriod.getNextExecutionPeriod()) {
	    if (degreeModule.hasAnyParentContexts(executionPeriod)) {
		return false;
	    }
	}
	
	return true;
    }
    
    public EnrolmentEvaluation createSpecialSeasonEvaluation(final Employee employee) {
	if (getEnrolmentEvaluationType() != EnrolmentEvaluationType.SPECIAL_SEASON && !isApproved()) {
	    setEnrolmentEvaluationType(EnrolmentEvaluationType.SPECIAL_SEASON);
	    setEnrollmentState(EnrollmentState.ENROLLED);

	    return new EnrolmentEvaluation(this, EnrolmentEvaluationType.SPECIAL_SEASON,
		    EnrolmentEvaluationState.TEMPORARY_OBJ, employee);
	} else {
	    throw new DomainException("error.invalid.enrolment.state");
	}
    }

    public void deleteSpecialSeasonEvaluation() {
	if (getEnrolmentEvaluationType() == EnrolmentEvaluationType.SPECIAL_SEASON && hasSpecialSeason()) {
	    setEnrolmentCondition(EnrollmentCondition.FINAL);
	    setEnrolmentEvaluationType(EnrolmentEvaluationType.NORMAL);
	    EnrolmentEvaluation enrolmentEvaluation = getEnrolmentEvaluationByEnrolmentEvaluationStateAndType(
		    EnrolmentEvaluationState.TEMPORARY_OBJ, EnrolmentEvaluationType.SPECIAL_SEASON);
	    if (enrolmentEvaluation != null) {
		enrolmentEvaluation.delete();
	    }

	    EnrolmentEvaluation normalEnrolmentEvaluation = getEnrolmentEvaluationByEnrolmentEvaluationStateAndType(
		    EnrolmentEvaluationState.FINAL_OBJ, EnrolmentEvaluationType.NORMAL);
	    if (normalEnrolmentEvaluation != null) {
		setEnrollmentState(normalEnrolmentEvaluation.getEnrollmentStateByGrade());
	    }
	} else {
	    throw new DomainException("error.invalid.enrolment.state");
	}
    }

    public List<EnrolmentEvaluation> getAllFinalEnrolmentEvaluations() {
	final List<EnrolmentEvaluation> result = new ArrayList<EnrolmentEvaluation>();

	for (final EnrolmentEvaluation enrolmentEvaluation : getEvaluationsSet()) {
	    if (enrolmentEvaluation.isFinal()) {
		result.add(enrolmentEvaluation);
	    }
	}

	return result;
    }

    private boolean hasEnrolmentEvaluationByType(final EnrolmentEvaluationType enrolmentEvaluationType) {
	for (final EnrolmentEvaluation enrolmentEvaluation : getEvaluationsSet()) {
	    if (enrolmentEvaluation.getEnrolmentEvaluationType().equals(enrolmentEvaluationType)) {
		return true;
	    }
	}
	return false;
    }

    public boolean hasImprovement() {
	return hasEnrolmentEvaluationByType(EnrolmentEvaluationType.IMPROVEMENT);
    }
    
    public boolean hasImprovementFor(ExecutionPeriod executionPeriod) {
	for (EnrolmentEvaluation enrolmentEvaluation : this.getEvaluationsSet()) {
	    if(enrolmentEvaluation.isImprovment() && enrolmentEvaluation.getExecutionPeriod() != null &&
		    enrolmentEvaluation.getExecutionPeriod().equals(executionPeriod)) {
		return true;
	    }
	}
	return false;
    }

    public boolean hasSpecialSeason() {
	return hasEnrolmentEvaluationByType(EnrolmentEvaluationType.SPECIAL_SEASON);
    }

    public boolean hasSpecialSeasonInExecutionYear() {
	for (final Enrolment enrolment : getBrothers()) {
	    if (enrolment.getExecutionYear() == getExecutionYear() && enrolment.hasSpecialSeason()) {
		return true;
	    }
	}
	
	return hasSpecialSeason();
    }

    public boolean isNotEvaluated() {
	final EnrolmentEvaluation latestEnrolmentEvaluation = getLatestEnrolmentEvaluation();
	return latestEnrolmentEvaluation == null || latestEnrolmentEvaluation.isNotEvaluated();
    }

    public boolean isFlunked() {
	final EnrolmentEvaluation latestEnrolmentEvaluation = getLatestEnrolmentEvaluation();
	return latestEnrolmentEvaluation != null && latestEnrolmentEvaluation.isFlunked();
    }

    public boolean isApproved() {
	final EnrolmentEvaluation latestEnrolmentEvaluation = getLatestEnrolmentEvaluation();
	return latestEnrolmentEvaluation != null && latestEnrolmentEvaluation.isApproved();
    }

    public boolean isEnroled() {
	return this.getEnrollmentState() == EnrollmentState.ENROLLED;
    }

    @Deprecated
    public boolean isEnrolmentStateApproved() {
	return this.getEnrollmentState() == EnrollmentState.APROVED;
    }

    @Deprecated
    public boolean isEnrolmentStateNotApproved() {
	return this.getEnrollmentState() == EnrollmentState.NOT_APROVED;
    }

    @Deprecated
    public boolean isEnrolmentStateNotEvaluated() {
	return this.getEnrollmentState() == EnrollmentState.NOT_EVALUATED;
    }

    public boolean isAnnulled() {
	return this.getEnrollmentState() == EnrollmentState.ANNULED;
    }

    public boolean isTemporarilyEnroled() {
	return this.getEnrollmentState() == EnrollmentState.TEMPORARILY_ENROLLED;
    }

    public boolean isEvaluated() {
	return isEnrolmentStateApproved() || isEnrolmentStateNotApproved();
    }

    public Boolean isFirstTime() {
	if (getIsFirstTime() == null) {
	    resetIsFirstTimeEnrolment();
	}
	return getIsFirstTime();
    }

    public void resetIsFirstTimeEnrolment() {
	if (getStudentCurricularPlan() != null && getCurricularCourse() != null
		&& getExecutionPeriod() != null && getEnrollmentState() != null) {
	    getStudentCurricularPlan().resetIsFirstTimeEnrolmentForCurricularCourse(getCurricularCourse());
	} else {
	    setIsFirstTime(Boolean.FALSE);
	}
    }

    @Override
    public void setDegreeModule(DegreeModule degreeModule) {
	super.setDegreeModule(degreeModule);
	resetIsFirstTimeEnrolment();
    }

    @Override
    public void setEnrollmentState(EnrollmentState enrollmentState) {
	super.setEnrollmentState(enrollmentState);
	resetIsFirstTimeEnrolment();
    }

    @Override
    public void setExecutionPeriod(ExecutionPeriod executionPeriod) {
	super.setExecutionPeriod(executionPeriod);
	resetIsFirstTimeEnrolment();
    }

    @Override
    public void setStudentCurricularPlan(StudentCurricularPlan studentCurricularPlan) {
	super.setStudentCurricularPlan(studentCurricularPlan);
	resetIsFirstTimeEnrolment();
    }

    public boolean isActive() {
	return !isAnnulled() && !isTemporarilyEnroled();
    }

    public int getNumberOfTotalEnrolmentsInThisCourse() {
	return this.getStudentCurricularPlan().countEnrolmentsByCurricularCourse(
		this.getCurricularCourse());
    }

    public int getNumberOfTotalEnrolmentsInThisCourse(ExecutionPeriod untilExecutionPeriod) {
	return this.getStudentCurricularPlan().countEnrolmentsByCurricularCourse(
		this.getCurricularCourse(), untilExecutionPeriod);
    }
    
    protected void createEnrolmentLog(Registration registration, EnrolmentAction action) {
	new EnrolmentLog(action, registration, this.getCurricularCourse(), this.getExecutionPeriod(),
		getCurrentUser());
    }

    protected void createEnrolmentLog(EnrolmentAction action) {
	new EnrolmentLog(action, this.getRegistration(), this
		.getCurricularCourse(), this.getExecutionPeriod(), getCurrentUser());
    }

    @Override
    public StringBuilder print(String tabs) {
	final StringBuilder builder = new StringBuilder();
	builder.append(tabs);
	builder.append("[E ").append(getDegreeModule().getName()).append(" ]\n");

	return builder;
    }

    public EnrolmentEvaluation addNewEnrolmentEvaluation(
	    EnrolmentEvaluationState enrolmentEvaluationState,
	    EnrolmentEvaluationType enrolmentEvaluationType, Person responsibleFor, String grade,
	    Date availableDate, Date examDate, ExecutionPeriod executionPeriod) {
	EnrolmentEvaluation enrolmentEvaluation = new EnrolmentEvaluation(this, enrolmentEvaluationState, enrolmentEvaluationType,
		responsibleFor, grade, availableDate, examDate, new DateTime());
	if(enrolmentEvaluationType == EnrolmentEvaluationType.IMPROVEMENT) {
	    enrolmentEvaluation.setExecutionPeriod(executionPeriod);
	}
	return enrolmentEvaluation;
    }

    public boolean hasAssociatedMarkSheet(MarkSheetType markSheetType) {
	for (final EnrolmentEvaluation enrolmentEvaluation : this.getEvaluationsSet()) {
	    if (enrolmentEvaluation.hasMarkSheet()
		    && enrolmentEvaluation.getEnrolmentEvaluationType() == markSheetType
			    .getEnrolmentEvaluationType()) {
		return true;
	    }
	}
	return false;
    }

    public boolean hasAssociatedMarkSheetOrFinalGrade(MarkSheetType markSheetType) {
	for (final EnrolmentEvaluation enrolmentEvaluation : this.getEvaluationsSet()) {
	    if (enrolmentEvaluation.getEnrolmentEvaluationType() == markSheetType
		    .getEnrolmentEvaluationType() && (enrolmentEvaluation.hasMarkSheet() || enrolmentEvaluation.isFinal())) {
		return true;
	    }
	}
	return false;
    }
    

    public List<EnrolmentEvaluation> getConfirmedEvaluations(MarkSheetType markSheetType) {
	List<EnrolmentEvaluation> evaluations = new ArrayList<EnrolmentEvaluation>();
	for (EnrolmentEvaluation evaluation : this.getEvaluationsSet()) {
	    if (evaluation.hasMarkSheet()
		    && evaluation.getMarkSheet().getMarkSheetType() == markSheetType
		    && evaluation.getMarkSheet().isConfirmed()) {

		evaluations.add(evaluation);
	    }
	}
	Collections.sort(evaluations, new BeanComparator("when"));
	return evaluations;
    }

    public Attends getAttendsByExecutionCourse(ExecutionCourse executionCourse) {
	for (Attends attends : this.getAttendsSet()) {
	    if (attends.getExecutionCourse() == executionCourse) {
		return attends;
	    }
	}
	return null;
    }

    public boolean hasAttendsFor(ExecutionPeriod executionPeriod) {
	for (final Attends attends : this.getAttendsSet()) {
	    if (attends.getExecutionCourse().getExecutionPeriod() == executionPeriod) {
		return true;
	    }
	}
	return false;
    }

    public Attends getAttendsFor(final ExecutionPeriod executionPeriod) {
	Attends result = null;
	
	for (final Attends attends : getAttendsSet()) {
	    if (attends.isFor(executionPeriod)) {
		if (result == null) {
		    result = attends;
		} else {
		    throw new DomainException("Enrolment.found.two.attends.for.same.execution.period");
		}
	    }
	}

	return result;
    }

    public ExecutionCourse getExecutionCourseFor(final ExecutionPeriod executionPeriod) {
	for (final Attends attend : getAttends()) {
	    if (attend.getExecutionCourse().getExecutionPeriod() == executionPeriod) {
		return attend.getExecutionCourse();
	    }
	}

	return null;
    }

    @Deprecated
    public EnrolmentEvaluation getFinalEnrolmentEvaluation() {
	return getLatestEnrolmentEvaluation();
    }

    public EnrolmentEvaluation getLatestEnrolmentEvaluation() {
	return (getStudentCurricularPlan().getDegreeType().getAdministrativeOfficeType() == AdministrativeOfficeType.DEGREE) ? 
		getLatestEnrolmentEvalution(getAllFinalEnrolmentEvaluations()) : getLatestEnrolmentEvalution(getEvaluationsSet());
    }

    private EnrolmentEvaluation getLatestEnrolmentEvalution(
	    Collection<EnrolmentEvaluation> enrolmentEvaluations) {
	return (enrolmentEvaluations == null || enrolmentEvaluations.isEmpty()) ? null : Collections
		.max(enrolmentEvaluations);
    }

    public EnrolmentEvaluation getLatestEnrolmentEvaluationBy(EnrolmentEvaluationType evaluationType) {
	return getLatestEnrolmentEvalution(getEnrolmentEvaluationsByEnrolmentEvaluationType(evaluationType));
    }

    public EnrolmentEvaluation getLatestNormalEnrolmentEvaluation() {
	return getLatestEnrolmentEvaluationBy(EnrolmentEvaluationType.NORMAL);
    }

    public EnrolmentEvaluation getLatestSpecialSeasonEnrolmentEvaluation() {
	return getLatestEnrolmentEvaluationBy(EnrolmentEvaluationType.SPECIAL_SEASON);
    }

    public EnrolmentEvaluation getLatestImprovementEnrolmentEvaluation() {
	return getLatestEnrolmentEvaluationBy(EnrolmentEvaluationType.IMPROVEMENT);
    }

    public EnrolmentEvaluation getImprovementEvaluation() {
	final EnrolmentEvaluation latestImprovementEnrolmentEvaluation = getLatestImprovementEnrolmentEvaluation();
	
	if (latestImprovementEnrolmentEvaluation != null && latestImprovementEnrolmentEvaluation.getEnrolmentEvaluationState().equals(EnrolmentEvaluationState.TEMPORARY_OBJ)) {
	    return latestImprovementEnrolmentEvaluation;
	}

	return null;
    }

    public EnrolmentEvaluation getLatestEquivalenceEnrolmentEvaluation() {
	return getLatestEnrolmentEvaluationBy(EnrolmentEvaluationType.EQUIVALENCE);
    }

    public String getGradeValue() {
	final EnrolmentEvaluation enrolmentEvaluation = getLatestEnrolmentEvaluation();
	return (enrolmentEvaluation == null) ? null : enrolmentEvaluation.getGradeValue();
    }

    public Grade getGrade() {
	final String gradeValue = getGradeValue();
	if (gradeValue == null || gradeValue.length() == 0) {
	    return Grade.createEmptyGrade();
	}
	
	return Grade.createGrade(gradeValue, getCurricularCourse().getGradeScaleChain());
    }

    public Integer getFinalGrade() {
	final String grade = getGradeValue();
	return (grade == null || StringUtils.isEmpty(grade) || !StringUtils.isNumeric(grade)) ? null
		: Integer.valueOf(grade);
    }

    public Double getAccumulatedEctsCredits() {
	return accumulatedEctsCredits;
    }

    public void setAccumulatedEctsCredits(Double ectsCredits) {
	this.accumulatedEctsCredits = ectsCredits;
    }

    @Override
    public List<Enrolment> getEnrolments() {
	return Collections.singletonList(this);
    }

    @Override
    public StudentCurricularPlan getStudentCurricularPlan() {
	return hasCurriculumGroup() ? getCurriculumGroup().getStudentCurricularPlan() : super
		.getStudentCurricularPlan();
    }

    public Registration getRegistration() {
	return getStudentCurricularPlan().getRegistration();
    }
    
    public ExecutionYear getExecutionYear() {
	return getExecutionPeriod().getExecutionYear();
    }
    
    @Override
    public boolean isApproved(final CurricularCourse curricularCourse, final ExecutionPeriod executionPeriod) {
	if (executionPeriod == null || getExecutionPeriod().isBeforeOrEquals(executionPeriod)) {
	    return isApproved()
		    && (getCurricularCourse().isEquivalent(curricularCourse) || hasCurricularCourseEquivalence(
			    getCurricularCourse(), curricularCourse, executionPeriod));
	} else {
	    return false;
	}
    }
    
    protected boolean hasCurricularCourseEquivalence(final CurricularCourse sourceCurricularCourse, final CurricularCourse equivalentCurricularCourse, final ExecutionPeriod executionPeriod) {
	for (final CurricularCourseEquivalence curricularCourseEquivalence : sourceCurricularCourse.getCurricularCourseEquivalencesFor(equivalentCurricularCourse)) {
	    if (oldCurricularCoursesAreApproved(curricularCourseEquivalence, executionPeriod)) {
		return true;
	    }
	}
	return false;
    }
    
    private boolean oldCurricularCoursesAreApproved(final CurricularCourseEquivalence curricularCourseEquivalence, final ExecutionPeriod executionPeriod) {
	boolean result = true;
        for (final CurricularCourse curricularCourse : curricularCourseEquivalence.getOldCurricularCourses()) {
            result &= getStudentCurricularPlan().isApproved(curricularCourse, executionPeriod);
            if (!result) {
        	return result;
            }
        }
        return result;
    }

    @Override
    public boolean isEnroledInExecutionPeriod(final CurricularCourse curricularCourse, final ExecutionPeriod executionPeriod) {
	return isValid(executionPeriod) && this.getCurricularCourse().equals(curricularCourse);
    }

    public boolean isValid(final ExecutionPeriod executionPeriod) {
	return getExecutionPeriod() == executionPeriod
		|| (getCurricularCourse().isAnual() && getExecutionPeriod().getExecutionYear() == executionPeriod.getExecutionYear());
    }
    
    @Override
    public boolean hasEnrolmentWithEnroledState(final CurricularCourse curricularCourse, final ExecutionPeriod executionPeriod) {
        return isEnroledInExecutionPeriod(curricularCourse, executionPeriod) && isEnroled();
    }
    
    public List<ExecutionCourse> getExecutionCourses() {
	return this.getCurricularCourse().getAssociatedExecutionCourses();
    }

    public boolean isEnrolmentTypeNormal() {
	return getCurricularCourse().getType() == CurricularCourseType.NORMAL_COURSE
		&& !isExtraCurricular() && !isOptional();
    }

    public String getEnrolmentTypeName() {
	if (isExtraCurricular()) {
	    return "EXTRA_CURRICULAR_ENROLMENT";
	} else if (isOptional()) {
	    return "ENROLMENT_IN_OPTIONAL_DEGREE_MODULE";
	} else {
	    return getCurricularCourse().getType().name();
	}
    }

    public boolean isOptional() {
	return false;
    }

    public static class DeleteEnrolmentExecutor implements FactoryExecutor {

	private DomainReference<Enrolment> enrolment;

	public DeleteEnrolmentExecutor(Enrolment enrolment) {
	    super();
	    this.enrolment = new DomainReference<Enrolment>(enrolment);
	}

	public Object execute() {
	    enrolment.getObject().delete();
	    return null;
	}

    }

    static public int countEvaluated(final List<Enrolment> enrolments) {
        int result = 0;
        
        for (final Enrolment enrolment : enrolments) {
            if (enrolment.isEvaluated()) {
                result++;
            }
        }
        
        return result;
    }

    static public int countApproved(final List<Enrolment> enrolments) {
        int result = 0;
        
        for (final Enrolment enrolment : enrolments) {
            if (enrolment.isEnrolmentStateApproved()) {
                result++;
            }
        }
        
        return result;
    }

    static final double LMAC_AND_LCI_WEIGHT_FACTOR = 0.25d;
    
    @Override
    final public Double getWeigth() {
	if (isExtraCurricular()) {
	    return Double.valueOf(0);
	}
	
	final Double baseWeigth = (super.getWeigth() == null || super.getWeigth() == 0) ? getCurricularCourse().getWeigth() : super.getWeigth();
	
	if (!isBolonhaDegree()) {
	    
	    if (isExecutionYearEnrolmentAfterOrEqualsExecutionYear0607()) {
		return getEctsCredits();
	    }

	    if (isFromLMAC() || isFromLCI()) {
		return baseWeigth * LMAC_AND_LCI_WEIGHT_FACTOR;
	    }
	}
	
	return baseWeigth;
    }
    
    private boolean isExecutionYearEnrolmentAfterOrEqualsExecutionYear0607() {
	final ExecutionYear executionYear = getExecutionPeriod().getExecutionYear();
	final ExecutionYear executionYear0607 = ExecutionYear.readExecutionYearByName("2006/2007");
	return executionYear.isAfterOrEquals(executionYear0607);
    }
    
    private boolean isFromLCI() {
	return Degree.readBySigla("LCI-pB").hasDegreeCurricularPlans(getDegreeCurricularPlanOfDegreeModule());
    }

    private boolean isFromLMAC() {
	return Degree.readBySigla("LMAC-pB").hasDegreeCurricularPlans(getDegreeCurricularPlanOfDegreeModule());
    }
    
    @Override
    final public Double getEctsCredits() {
	return isExtraCurricular() ? Double.valueOf(0d) : getCurricularCourse().getEctsCredits(getExecutionPeriod());
    }

    @Override
    final public Double getAprovedEctsCredits() {
	return isApproved() ? getEctsCredits() : Double.valueOf(0d);
    }

    public boolean isExtraCurricular() {
	return getIsExtraCurricular() != null && getIsExtraCurricular();
    }

    final public boolean isExternalEnrolment() {
	return false;
    }

    @Override
    public Double getEnroledEctsCredits(final ExecutionPeriod executionPeriod) {
	return isValid(executionPeriod) && isEnroled() ? getEctsCredits() : Double.valueOf(0d);
    }
    
    @Override
    public Enrolment findEnrolmentFor(final CurricularCourse curricularCourse, final ExecutionPeriod executionPeriod) {
        return isEnroledInExecutionPeriod(curricularCourse, executionPeriod) ? this : null;
    }
    
    @Override
    public Enrolment getApprovedEnrolment(final CurricularCourse curricularCourse) {
        return isAproved(curricularCourse) ? this : null;
    }

    @Override
    public Set<IDegreeModuleToEvaluate> getDegreeModulesToEvaluate(final ExecutionPeriod executionPeriod) {
	if (!isValid(executionPeriod)) {
	    return Collections.EMPTY_SET;
	}
	final Set<IDegreeModuleToEvaluate> result = new HashSet<IDegreeModuleToEvaluate>(1);
	result.add(new CurriculumModuleEnroledWrapper(this, executionPeriod));
	return result;
    }
    
    public double getAccumulatedEctsCredits(final ExecutionPeriod executionPeriod) {
	if(!isBolonhaDegree()) {
	    return accumulatedEctsCredits;
	}	
	if(isExtraCurricular() || parentCurriculumGroupIsNoCourseGroupCurriculumGroup()) {
	    return 0d;
	}
	
	return getStudentCurricularPlan().getAccumulatedEctsCredits(executionPeriod, getCurricularCourse());
    }
    
    public boolean isImprovementEnroled() {
	return isEnrolmentStateApproved() && getImprovementEvaluation() != null;
    }
    
    public boolean canBeImproved() {
	return isEnrolmentStateApproved() && !hasImprovement();
    }

    public boolean isSpecialSeasonEnroled(final ExecutionYear executionYear) {
	return getEnrolmentEvaluationType() == EnrolmentEvaluationType.SPECIAL_SEASON && 
		getExecutionPeriod().getExecutionYear() == executionYear && getTempSpecialSeasonEvaluation() != null;
    }
    
    private EnrolmentEvaluation getTempSpecialSeasonEvaluation() {
	final EnrolmentEvaluation latestSpecialSeasonEnrolmentEvaluation = getLatestSpecialSeasonEnrolmentEvaluation();
	
	if (latestSpecialSeasonEnrolmentEvaluation != null && latestSpecialSeasonEnrolmentEvaluation.getEnrolmentEvaluationState().equals(EnrolmentEvaluationState.TEMPORARY_OBJ)) {
	    return latestSpecialSeasonEnrolmentEvaluation;
	}

	return null;
    }

    public boolean canBeSpecialSeasonEnroled(ExecutionYear executionYear) {
	return getEnrolmentEvaluationType() != EnrolmentEvaluationType.SPECIAL_SEASON
	    && getExecutionPeriod().getExecutionYear() == executionYear
	    && !isApproved();
    }
    
    @Override
    public Collection<Enrolment> getSpecialSeasonEnrolments(ExecutionYear executionYear) {
	if (getEnrolmentEvaluationType() == EnrolmentEvaluationType.SPECIAL_SEASON
		    && getExecutionPeriod().getExecutionYear().equals(executionYear)) {
	    return Collections.singleton(this);
	}
	return Collections.emptySet();
    }
    
    public String getDescription() {
	return getStudentCurricularPlan().getDegree().getPresentationName() + " > " + getName().getContent();
    }

    /**
     * Obtains the first non evaluated thesis associated with this Enrolment or
     * the Thesis with the final evaluation. This method assumes that each
     * Student has at most one non evaluated Thesis and no more that two Thesis.
     * 
     * @return the first non evaluated Thesis for this Enrolment
     */
    public Thesis getThesis() {
	List<Thesis> theses = getTheses();

	switch (theses.size()) {
	case 0:
	    return null;
	case 1:
	    return theses.iterator().next();
	default:
	    SortedSet<Thesis> sortedTheses = new TreeSet<Thesis>(new Comparator<Thesis>() {
		public int compare(Thesis o1, Thesis o2) {
		    return o2.getCreation().compareTo(o1.getCreation());
		}
	    });

	    sortedTheses.addAll(theses);
	    return sortedTheses.iterator().next();
	}
    }

    public boolean isBefore(final Enrolment enrolment) {
	return getExecutionPeriod().isBefore(enrolment.getExecutionPeriod());
    }

    public Proposal getDissertationProposal() {
	final ExecutionYear previousExecutionYear = getExecutionYear().getPreviousExecutionYear();
	if (previousExecutionYear == null) {
	    return null;
	}
	return getRegistration().getDissertationProposal(previousExecutionYear);
    }

    public Unit getAcademicUnit() {
	return RootDomainObject.getInstance().getInstitutionUnit();
    }

    public String getCode() {
	if(hasDegreeModule()) {
	    return getDegreeModule().getCode();
	}
	return null;
    }

}
