package cc.flydev.launcher.entity;

public class Music {
	private int id;
	private String name;
	private String path;
	private String artist;
	
	public Music() {
		super();
	}
	public Music(int id,String name, String path, String artist) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
		this.artist = artist;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
}
