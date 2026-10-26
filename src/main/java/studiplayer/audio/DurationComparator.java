package studiplayer.audio;
import java.util.Comparator;

public class DurationComparator implements Comparator<AudioFile> {
	
	public int compare(AudioFile o1, AudioFile o2) {
		if (o1 == null || o2 == null) {
		    throw new NullPointerException("AudioFiles cannot be null"); 
		}
		if (o1 instanceof SampledFile && o2 instanceof SampledFile)  {
			SampledFile s1 = (SampledFile) o1;
			SampledFile s2 = (SampledFile) o2;
			long duration1 = s1.getDuration();
			long duration2 = s2.getDuration();

			if (duration1 > duration2) {
				return 1;
			} else if (duration1 == duration2){
				return 0;
			} else {
				return -1;
			}
		} else if (!(o1 instanceof SampledFile) && o2 instanceof SampledFile) {
			return -1;
		} else if ((o1 instanceof SampledFile) && !(o2 instanceof SampledFile)) {
			return 1;
		} else {
			return 0;
		}
	}	
}
