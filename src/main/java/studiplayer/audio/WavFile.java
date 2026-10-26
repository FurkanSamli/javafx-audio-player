package studiplayer.audio;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile{
	public WavFile() throws NotPlayableException {
		super();
		readAndSetDurationFromFile();
	}
	
	public WavFile(String path) throws NotPlayableException {
		super(path);
		readAndSetDurationFromFile();
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException {
		try {
			WavParamReader.readParams(this.getPathname());
			long numberOfFrames = WavParamReader.getNumberOfFrames();
			float frameRate = WavParamReader.getFrameRate();
			
			this.duration = computeDuration(numberOfFrames,frameRate);
		} catch (Exception e) {
			throw new NotPlayableException(this.getPathname(),"Paramaters cannot be read",e);
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + this.formatDuration();
	}
	
	public static long computeDuration(long numberOfFrames, float frameRate) {
		float result = (numberOfFrames/frameRate)*1000000;
		
		return (long) result;
	}
}
