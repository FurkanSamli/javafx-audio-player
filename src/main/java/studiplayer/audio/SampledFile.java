package studiplayer.audio;
import studiplayer.basic.BasicPlayer;

abstract public class SampledFile extends AudioFile {
	protected long duration;
	
	public SampledFile() {
		super();
	}
	
	public SampledFile(String path) throws NotPlayableException {
		super(path);
	}
	
	@Override
	public void play() throws NotPlayableException {
		try {
			BasicPlayer.play(getPathname());
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), "Error playing file", e);
		}
	}

	@Override
	public void togglePause() {
		BasicPlayer.togglePause();
	}

	@Override
	public void stop() {
		BasicPlayer.stop();		
	}

	@Override
	public String formatDuration() {
		return timeFormatter(this.duration);
	}

	@Override
	public String formatPosition() {
		return timeFormatter(BasicPlayer.getPosition());
	}
	
	public static String timeFormatter(long timelnMicroSeconds) {
		if (timelnMicroSeconds<0) {
			throw new RuntimeException("Negative Value Error");
		}
		long timeInSecond =  timelnMicroSeconds/1000000;
		long minute = timeInSecond/60;
		long second = timeInSecond%60;
		
		if (minute > 99) {
			throw new RuntimeException("Time exceeds mm:ss format");
		}
		return String.format("%02d:%02d", minute,second);
	}
	
	public long getDuration() {
		return duration;
	}
}