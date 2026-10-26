package studiplayer.audio;
import java.util.Comparator;

public class TitleComparator implements Comparator<AudioFile> {
	public int compare(AudioFile o1, AudioFile o2) {
		if (o1 == null || o2 == null) {
		    throw new NullPointerException("AudioFiles cannot be null"); 
		}
		String title1 = o1.getTitle();
		String title2 = o2.getTitle();
		
		return title1.compareTo(title2);
	}
}
