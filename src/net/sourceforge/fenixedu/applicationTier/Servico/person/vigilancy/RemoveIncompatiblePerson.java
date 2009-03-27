package net.sourceforge.fenixedu.applicationTier.Servico.person.vigilancy;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.domain.vigilancy.Vigilant;
import net.sourceforge.fenixedu.domain.vigilancy.VigilantWrapper;
import pt.ist.fenixWebFramework.services.Service;

public class RemoveIncompatiblePerson extends FenixService {

    @Service
    public static void run(VigilantWrapper vigilantWrapper) {
	vigilantWrapper.getPerson().removeIncompatibleVigilant();
    }

}