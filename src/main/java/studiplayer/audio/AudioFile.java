package studiplayer.audio;
import java.io.File;

abstract public class AudioFile {
	private String pathname; //Private is visible to only class
	private String filename; // ./ ../
	protected String author;
	protected String title;
	
	public AudioFile(String path) throws NotPlayableException {
		this.parsePathname(path);
		this.parseFilename(this.filename);
			
		File file = new File(this.getPathname());
		
		if (!file.canRead()) {
			throw new NotPlayableException(path, "File is not readable!");
		}
	}
	
	public AudioFile(){
		pathname = "";
		filename = "";
		author = "";
		title = "";
		
	}

	private boolean isWindows() {
		 return System.getProperty("os.name").toLowerCase()
		 .indexOf("win") >= 0;
		}
	
	public void parsePathname(String path) {
		// remove leading and trailing spaces		
		String p = path.trim(); 
		
		if (p.isEmpty()) {
			this.pathname = "";
			this.filename = "";
		}
		
		if (p.equals("-")) {
			this.pathname = "-";
			this.filename = "-";
		}
		
		//WINDOWS
		if (isWindows()) {
			p = p.replace("/", "\\");
			
			while (p.contains("\\\\")) {
				p = p.replace("\\\\", "\\");
			}
		} else {
			//LINUX
			p = p.replace("\\", "/");
			
			while (p.contains("//")) {
				p = p.replace("//", "/");
			}
			
			if (p.length() >= 2 && p.charAt(1) == ':') {
	            p = "/" + p.charAt(0) + p.substring(2);
	        }
		}
		
		this.pathname = p;
		
		String sep = "";
		if (isWindows()) {
			sep = "\\";
		} else {
			sep = "/";
		}
		
		if (p.endsWith(sep)) {
			this.filename = "";
		} else {
			int lastSlash = p.lastIndexOf(sep);
			this.filename = p.substring(lastSlash + 1).trim();
		}
		

			
		
	}
	
	public void parseFilename(String file) {
		String f = file.trim();
		
		if (f.isEmpty()) {
			this.author = "";
			this.title = "";
		}
		else if (file.equals("-")) {
			this.author = "";
			this.title = "-";
		} else if (f.equals("-")) {
			this.author = "";
			this.title = "";
		} else if (f.startsWith(".")) {
			this.author = "";
			this.title = "";
		} else if (!f.contains(" -")) {
		    this.author ="";
		    int indexOfDot = f.lastIndexOf("."); 
		    if (indexOfDot != -1) {
		        this.title = f.substring(0, indexOfDot).trim();
		    } else {
		        this.title = f;
		    }
		} else {
			int indexOfDash = f.indexOf(" -");
			int lastIndexOfDot = f.lastIndexOf(".");
			if (lastIndexOfDot!=-1) {
				this.author = f.substring(0,indexOfDash).trim();
				this.title = f.substring(indexOfDash+3,lastIndexOfDot).trim();
			}
		}
	}
	
	
	//Getters
	public String getFilename() {
		return filename;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public String getPathname() {
		return pathname;
	}
	

	public String toString() {
		String a = this.getAuthor();
		String t = this.getTitle();
		if (a.isEmpty()) {
			return t;
		} else {
			return a +  " - " + t;
		}
	}

	
	public abstract void play() throws NotPlayableException;
	public abstract void togglePause();
	public abstract void stop();
	public abstract String formatDuration();
	public abstract String formatPosition();
	
	
	
	
	public static void main(String[] args) {
		
	}
	

}
