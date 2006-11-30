package net.sourceforge.fenixedu.domain.studentCurriculum;


public class ExtraCurriculumGroup extends ExtraCurriculumGroup_Base {
    
    public ExtraCurriculumGroup(final CurriculumGroup curriculumGroup) {
	super();
	init(curriculumGroup);
    }
    
    @Override
    public Integer getChildOrder() {
        return super.getChildOrder() - 2;
    }
    
    @Override
    public NoCourseGroupCurriculumGroupType getNoCourseGroupCurriculumGroupType() {
        return NoCourseGroupCurriculumGroupType.EXTRA_CURRICULAR;
    }
}
