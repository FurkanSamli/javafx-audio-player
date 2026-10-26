package studiplayer.audio;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PlayList implements Iterable<AudioFile> {
	private LinkedList<AudioFile> list;
	private String search;
	SortCriterion sortCriterion;
	
	private ControllablePlayListIterator iterator;
	private AudioFile currentFile; 
	
	public PlayList() {
		this.list = new LinkedList<>();
		this.sortCriterion = SortCriterion.DEFAULT;
		this.search = "";
		this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
		this.currentFile = null;
	}
	
	public PlayList(String path){
		this();
		loadFromM3U(path);
	}
	
	public void add(AudioFile file) {
		list.add(file);
		this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
		this.currentFile = null;
	}
	
	public void remove(AudioFile file) {
		list.remove(file);
		this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
		this.currentFile = null;
	}
	
	public int size() {
		return list.size();
	}
	
	public AudioFile currentAudioFile() {
		if (this.currentFile == null && this.iterator != null && this.iterator.hasNext()) {
			this.currentFile = this.iterator.next();
		}
		return this.currentFile;
	}
	
	public void nextSong() {
		if (this.currentFile == null && this.iterator != null && this.iterator.hasNext()) {
			this.currentFile = this.iterator.next();
		}
		
		if (this.iterator != null && this.iterator.hasNext()) {
			this.currentFile = this.iterator.next();
		} else {
			this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
			this.currentFile = null;
			
			if (this.iterator != null && this.iterator.hasNext()) {
				this.currentFile = this.iterator.next();
			} else {
				this.currentFile = null;
			}
		}
	}
	
	public void jumpToAudioFile(AudioFile file) {
		if (this.iterator != null) {
			AudioFile jumpedFile = this.iterator.jumpToAudioFile(file);
			if (jumpedFile != null) {
				this.currentFile = jumpedFile;
			}
		}
	}
	
	public void saveAsM3U(String path)  {
		FileWriter writer = null;
		String sep = System.getProperty("line.separator");
		
		try {
			writer = new FileWriter(path);
			for (AudioFile file : list) {
				writer.write(file.getPathname() + sep);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to write file " + path + "!");
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}
	
	public void loadFromM3U(String path){
		this.list.clear();
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(new File(path));

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				
				if (line.isEmpty() || line.startsWith("#")) {
					continue; 
				}
				
				try {
					AudioFile file = AudioFileFactory.createAudioFile(line);
					this.list.add(file);
				} catch (NotPlayableException e) {
					e.printStackTrace();
					continue;
				} 
			}
				
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				scanner.close();	
			} catch (Exception e) {
			}
		}
		this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
		this.currentFile = null;
	}
	
	@Override
	public java.util.Iterator<AudioFile> iterator() {
		return new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
	}
	
	public List<AudioFile> getList() {
		return list;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
		this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
		this.currentFile = null;
	}

	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}

	public void setSortCriterion(SortCriterion sortCriterion) {
		this.sortCriterion = sortCriterion;
		this.iterator = new ControllablePlayListIterator(this.list, this.search, this.sortCriterion);
		this.currentFile = null;
	}
}