package com.fletch22.orb;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.util.NowFactory;

@Component
public class TranDateGenerator {
	
	Logger logger = LoggerFactory.getLogger(TranDateGenerator.class);
	
	public static final BigDecimal TRAN_DATE_UNSET = new BigDecimal(0);
	private BigDecimal lastTranDateRaw = new BigDecimal(0);
	private static final int NUMBER_DECIMAL_PLACES = 10;
	private BigDecimal increment = (new BigDecimal(1)).scaleByPowerOfTen(-1 * NUMBER_DECIMAL_PLACES); // NOTE:01-20-2015:chris.flesche: decimal followed by 9 zeros. Then a 1. :)
	
	// NOTE:01-24-2015:chris.flesche: When the system resets its clock, this could screw up this class' tranDate. 
	// This would cause bad ordering of items in the log. So this class is designed to pause if it detects that the current date time is *BEFORE* the last calculated tranDate.
	// To reiterate, this would happen only if the system time clock was rewound to correct a slightly fast clock. In practice, the system would be a few milliseconds off. If we simply pause
	// the thread until the current time exceeds the last calculate tranDate, our next calculate tranDate will safely be after the last calculated tranDate. This number represents
	// the maximum time we should wait to "catch up" before we throw up our hands and throw an exception.
	private static final int MAX_MILLIS_TO_PAUSE = 3000;
	
	@Autowired
	public NowFactory nowFactory;

	// NOTE:01-20-2015:chris.flesche: A complex design that will perhaps not be necessary. Basically this ensures that the tran date 
	// never repeats. If there is a repeat, the method increments the value by a small amount.
	// NOTE:01-20-2015:Turning back the system clock may very well mess up record ordering - hence this precautionary approach. Syncing time services will also mess ordering if the time syncs at the 
	// right moment and/or with a big enough reset. 
	public BigDecimal getTranDate() {
		BigDecimal currentTranDateRaw = getCurrentTranDate();

		BigDecimal tranDate;
		
		currentTranDateRaw = ensureGoodLogDate(currentTranDateRaw);
		
		if (currentTranDateRaw.equals(this.lastTranDateRaw)) {
			tranDate = currentTranDateRaw.add(increment);
		} else {
			tranDate = currentTranDateRaw;
		}
		this.lastTranDateRaw = currentTranDateRaw;		
		
		return tranDate.setScale(NUMBER_DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
	}

	private BigDecimal getCurrentTranDate() {
		BigDecimal currentTranDateRaw = new BigDecimal(nowFactory.getNow().getMillis());
		return currentTranDateRaw;
	}

	private BigDecimal ensureGoodLogDate(BigDecimal currentTranDateRaw) {

		BigDecimal difference = currentTranDateRaw.subtract(this.lastTranDateRaw);
		if (difference.compareTo(BigDecimal.ZERO) < 0) {
			if (difference.abs().compareTo(new BigDecimal(MAX_MILLIS_TO_PAUSE)) > 0) {
				throw new RuntimeException("Encountered problem with tran date generation possibly related to system clock issues. The last tran date was '" + String.valueOf(this.lastTranDateRaw) + "' millis. But the current tran date is '" + String.valueOf(currentTranDateRaw) + "'. That's a difference of '" + String.valueOf(difference.abs()) + "' millis. This exceeds the maximum allowed '" + MAX_MILLIS_TO_PAUSE + "' millis. The cause may have been a system clock rewind due to clock drift. Someone (or the system) corrected the time and as a result created this gap. Restart after the current time has 'caught up' with the last tran date."); 
			} else {
				difference = difference.setScale(0, BigDecimal.ROUND_CEILING);
				
				sleep(difference.abs());
				currentTranDateRaw = getCurrentTranDate();
			}
		}
		
		return currentTranDateRaw;
	}

	private void sleep(BigDecimal difference) {
		try {
			Thread.sleep(difference.longValue());
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem when trying to pause thread.");
		}
	}
}
