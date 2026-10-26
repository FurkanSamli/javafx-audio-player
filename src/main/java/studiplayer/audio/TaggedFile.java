package studiplayer.audio;
import java.util.Map;

import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile{
	private String album;
	
	public TaggedFile() throws NotPlayableException{
		super();
		readAndStoreTags();
		
	}
	
	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		readAndStoreTags();
	}
	
	public String getAlbum(){
		return album;
	}
	
	public void readAndStoreTags() throws NotPlayableException {
		try {
			Map<String, Object> tagMap = TagReader.readTags(this.getPathname());
			Object authorO = tagMap.get("author");
			Object titleO = tagMap.get("title");
			Object albumO = tagMap.get("album");
			Object durationO = tagMap.get("duration");
			
			if (authorO != null) {
				this.author= ((String) authorO).trim();
			}
			
			if (titleO != null) {
				this.title = ((String) titleO).trim();
			}
			
			if (albumO != null) {
				this.album = ((String) albumO).trim();
			}
			
			if (durationO != null) {
				this.duration = (long) durationO;
			}
		
		} catch (Exception e) {
			throw new NotPlayableException(this.getPathname(), "Tags can't be read", e);
		}
		
	}
	
	@Override
	public String toString() {
		if (this.album != null) {
			return (super.toString() + " - " + this.album + " - " + this.formatDuration());
		}
		return super.toString() + " - " + this.formatDuration();
	}
}
