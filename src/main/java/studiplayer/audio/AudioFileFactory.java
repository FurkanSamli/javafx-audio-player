package studiplayer.audio;

public class AudioFileFactory {
	public static AudioFile createAudioFile(String path) throws NotPlayableException {
		if (path == null) {
			throw new NotPlayableException(path, "Path cannot be null");
		}
		
		String lowerPath = path.toLowerCase();
		
		if (lowerPath.endsWith(".wav")) {
			return new WavFile(path);
		} else if (lowerPath.endsWith(".ogg") || lowerPath.endsWith(".mp3")) {
	        return new TaggedFile(path);
	    } 
	    else {
	        // throw new RuntimeException("Unknown suffix for AudioFile \"" + path + "\"");
	    	throw new NotPlayableException(path, "Unknown suffix for AudioFile \"" + path + "\""); 
	    }
		
	}
}
