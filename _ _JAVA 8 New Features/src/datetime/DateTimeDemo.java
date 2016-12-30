package datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class DateTimeDemo {

	public static void main(String[] args) {
		LocalDate d = LocalDate.of(1989, Month.FEBRUARY, 22);
		System.out.println(d);
		
		LocalTime t = LocalTime.now();
		System.out.println("current time = " + t);
		
		Set zoneIDs = ZoneId.getAvailableZoneIds();
		//need to sort the set after coping it to list, can use Collections.sort()
		
		List<String> zoneIDList = new ArrayList<>(zoneIDs);
		Collections.sort(zoneIDList);
		for(String s : zoneIDList){
			System.out.println(s);
		}

		LocalTime p = LocalTime.now(ZoneId.of("Europe/Rome"));
		System.out.println("current local time in Europe/Rome = " + p);
		
		LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Rome"));
		System.out.println("current local date time in Europe/Rome = " + ldt);
		
		System.out.println("current local datatime in GMT = " + Instant.now());
	}

}
