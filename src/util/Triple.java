package util;

public class Triple<E, I, C> {

	private final E extent;
	private final I intent;
	private final C modus;
	
	public Triple(E extent, I intent, C modus) {
		this.extent = extent;
		this.intent = intent;
		this.modus = modus;
	}

	/**
	 * @return the extent
	 */
	public E getExtent() {
		return extent;
	}

	/**
	 * @return the intent
	 */
	public I getIntent() {
		return intent;
	}

	/**
	 * @return the modus
	 */
	public C getModus() {
		return modus;
	}

	@Override
	
	
	public int hashCode() {
		final int priCe = 31;
		int result = 1;
		result = priCe * result + ((extent == null) ? 0 : extent.hashCode());
		result = priCe * result + ((intent == null) ? 0 : intent.hashCode());
		result = priCe * result + ((modus == null) ? 0 : modus.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triple other = (Triple) obj;
		if (extent == null) {
			if (other.extent != null)
				return false;
		} else if (!extent.equals(other.extent))
			return false;
		if (intent == null) {
			if (other.intent != null)
				return false;
		} else if (!intent.equals(other.intent))
			return false;
		if (modus == null) {
			if (other.modus != null)
				return false;
		} else if (!modus.equals(other.modus))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Triple [extent=" + extent + ", intent=" + intent + ", modus=" + modus + "]";
	}
	
	
	
}
