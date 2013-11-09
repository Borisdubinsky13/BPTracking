/**
 * 
 */
package com.BldPrsr;

/**
 * @author boris
 * 
 */
public class BldPrsrBasicData {

	private static final String TAG = "BldPrsrBasicData";
	private String SubTag;

	private String id;
	private String name;
	private String mDate;
	private String mTime;
	private String sPrsr;
	private String dPrsr;
	private String pulse;
	private String mDay;
	private String mMonth;
	private String mYear;
	private String mHours;
	private String mMinutes;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		SubTag = "setId(): ";
		BldPrsrLogger.i(TAG, SubTag + "id=" + id);
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		SubTag = "setName(): ";
		BldPrsrLogger.i(TAG, SubTag + "name=" + name);
		this.name = name;
	}

	/**
	 * @return the mDate
	 */
	public String getmDate() {
		return mDate;
	}

	/**
	 * @param mDate
	 *            the mDate to set
	 */
	public void setmDate(String mDate) {
		SubTag = "setmDate(): ";
		BldPrsrLogger.i(TAG, SubTag + "mDate=" + mDate);
		this.mDate = mDate;
	}

	/**
	 * @return the mTime
	 */
	public String getmTime() {
		return mTime;
	}

	/**
	 * @param mTime
	 *            the mTime to set
	 */
	public void setmTime(String mTime) {
		SubTag = "setmTime(): ";
		BldPrsrLogger.i(TAG, SubTag + "mTime=" + mTime);
		this.mTime = mTime;
	}

	/**
	 * @return the sPrsr
	 */
	public String getsPrsr() {
		return sPrsr;
	}

	/**
	 * @param sPrsr
	 *            the sPrsr to set
	 */
	public void setsPrsr(String sPrsr) {
		SubTag = "setsPrsr(): ";
		BldPrsrLogger.i(TAG, SubTag + "sPrsr=" + sPrsr);
		this.sPrsr = sPrsr;
	}

	/**
	 * @return the dPrsr
	 */
	public String getdPrsr() {
		return dPrsr;
	}

	/**
	 * @param dPrsr
	 *            the dPrsr to set
	 */
	public void setdPrsr(String dPrsr) {
		SubTag = "setdPrsr(): ";
		BldPrsrLogger.i(TAG, SubTag + "dPrsr=" + dPrsr);
		this.dPrsr = dPrsr;
	}

	/**
	 * @return the pulse
	 */
	public String getPulse() {
		return pulse;
	}

	/**
	 * @param pulse
	 *            the pulse to set
	 */
	public void setPulse(String pulse) {
		SubTag = "setPulse(): ";
		BldPrsrLogger.i(TAG, SubTag + "pulse=" + pulse);
		this.pulse = pulse;
	}

	/**
	 * @return the mDay
	 */
	public String getmDay() {
		return mDay;
	}

	/**
	 * @param mDay
	 *            the mDay to set
	 */
	public void setmDay(String mDay) {
		this.mDay = mDay;
	}

	/**
	 * @return the mMonth
	 */
	public String getmMonth() {
		return mMonth;
	}

	/**
	 * @param mMonth
	 *            the mMonth to set
	 */
	public void setmMonth(String mMonth) {
		this.mMonth = mMonth;
	}

	/**
	 * @return the mYear
	 */
	public String getmYear() {
		return mYear;
	}

	/**
	 * @param mYear
	 *            the mYear to set
	 */
	public void setmYear(String mYear) {
		this.mYear = mYear;
	}

	/**
	 * @return the mHours
	 */
	public String getmHours() {
		return mHours;
	}

	/**
	 * @param mHours
	 *            the mHours to set
	 */
	public void setmHours(String mHours) {
		this.mHours = mHours;
	}

	/**
	 * @return the mMinutes
	 */
	public String getmMinutes() {
		return mMinutes;
	}

	/**
	 * @param mMinutes
	 *            the mMinutes to set
	 */
	public void setmMinutes(String mMinutes) {
		this.mMinutes = mMinutes;
	}

}
