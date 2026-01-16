package healthcareab.project.healthcare_booking_app.helpers.period;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.repository.PeriodRepository;
import org.springframework.stereotype.Component;

@Component
public class PeriodHelper {
    private final PeriodRepository periodRepository;

    public PeriodHelper(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }


    public String updatePeriods(UpdateAvailabilityRequest request) {


        Period newPeriod = request.getNewPeriod();


        // 1 timme period - 10 min break. 60 min lunch 12-13


        Period period = periodRepository.save(newPeriod);
        System.out.println("------- " + period.getId());


        return period.getId();
    }


    // create new period (return to avail and gets added to list)
    // validate period cant exist more than 4 weeks ahead of time max
    // validate period exist already
    // patch existing period (no need to return)
    // delete period (remove id from avail)
    // 10 or 15 min break

    // getMy avail -> get myPeriods
    // upDate avail -> patchPeriods
    // getAvail -> get periods by id


    // ifall 26/01/10 period 08:00 - 12:00
    // 13:00-17:00
    // ifall vi introducerar bokar en appointment 10:00-11:00 måste vi in i period och uppdatera
    // 08:00-10:00 och skapa en ny period 11:00-12:00
}
