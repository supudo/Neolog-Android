package net.supudo.apps.aNeolog.Database.Models;

public final class TextContentModel {
	public final Integer CID;
	public final String Title;
	public final String Content;
	
	public TextContentModel(Integer cid, String title, String content) {
		this.CID = cid;
		this.Title = title;
		this.Content = content;
	}
}
