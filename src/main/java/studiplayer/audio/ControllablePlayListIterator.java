package studiplayer.audio;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class ControllablePlayListIterator implements Iterator<AudioFile> {
	List<AudioFile> list;
	int index = 0;
	
	public ControllablePlayListIterator(List<AudioFile> list) {
		this.list = list;
	}
	
	public ControllablePlayListIterator(List<AudioFile> list, String search, SortCriterion sortCriterion) {
		List<AudioFile> list2 = new ArrayList<>();
		
		if (search == null || search.isEmpty()) {
			list2.addAll(list);
		} else {
			for (AudioFile file : list) {	
				if ((file.getAuthor() != null && file.getAuthor().contains(search)) || 
						(file.getTitle() != null && file.getTitle().contains(search))) {
					list2.add(file);
					continue;
				} 
				if (file instanceof TaggedFile) {
					TaggedFile taggedFile = (TaggedFile) file;
					if (taggedFile.getAlbum() != null && taggedFile.getAlbum().contains(search)) {
						list2.add(file);
					}
				}
			}
		}
		
		switch (sortCriterion) {
			case AUTHOR:
				Collections.sort(list2, new AuthorComparator());
				break;
			case TITLE:
				Collections.sort(list2, new TitleComparator());
				break;
			case ALBUM:
				Collections.sort(list2, new AlbumComparator());
				break;
			case DURATION:
				Collections.sort(list2, new DurationComparator());
				break;
			case DEFAULT:
				break;
				
		}
		
		this.list = list2;
		this.index = 0;
		
	}
	
	@Override
	public boolean hasNext() {
		if (index < list.size() && index >= 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public AudioFile next(){
		AudioFile file = list.get(index);
		index++;
		return file;
	}
	
	public AudioFile jumpToAudioFile(AudioFile file) {
		if (file==null || !list.contains(file)) {
			return null;
		}
		
		int indexFile = list.indexOf(file);
		
		this.index = indexFile + 1;
		
		return file;
	}
} 