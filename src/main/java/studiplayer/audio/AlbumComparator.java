package studiplayer.audio;
import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile> {
	
	public int compare(AudioFile o1, AudioFile o2) {
		if (o1 == null || o2 == null) {
		    throw new NullPointerException("AudioFiles cannot be null"); 
		}
		
		if (o1 instanceof TaggedFile && o2 instanceof TaggedFile)  {
			TaggedFile t1 = (TaggedFile) o1;
			TaggedFile t2 = (TaggedFile) o2;
			
			String album1;
			if (t1.getAlbum() != null) {
				album1 = t1.getAlbum();
			} else {
				album1 = "";
			}
			
			String album2;
			if (t2.getAlbum() != null) {
				album2 = t2.getAlbum();
			} else {
				album2 = "";
			}
			
			return album1.compareTo(album2);
			
		} else if (!(o1 instanceof TaggedFile) && o2 instanceof TaggedFile) {
			return -1;
		} else if ((o1 instanceof TaggedFile) && !(o2 instanceof TaggedFile)) {
			return 1;
		} else {
			return 0;
		}
	}
}