package studiplayer.audio;

public class NotPlayableException extends Exception {
	private String pathname;
	
	public NotPlayableException(String path, String msg) {
		super(msg);
		this.pathname = path;
		
	}
	
	public NotPlayableException(String path, Throwable t) {
		super(t);
		this.pathname = path;
	}

	public NotPlayableException(String path, String msg, Throwable t ) {
		super(msg,t);
		this.pathname = path;
	}
	
	@Override
	public String toString() {
		return String.format("studiplayer.audio.NotPlayableException for <%s>: <%s>",getPathname(),getMessage());
	}
	
	public String getPathname() {
		return this.pathname;
	}
		
}
