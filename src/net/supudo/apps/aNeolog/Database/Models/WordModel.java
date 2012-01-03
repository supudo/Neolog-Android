package net.supudo.apps.aNeolog.Database.Models;

public class WordModel {
	public Integer WordID;
	public String Word;
	public String WordLetter;
	public Integer OrderPos;
	public Integer NestID;
	public String Example;
	public String Ethimology;
	public String Description;
	public String Derivatives;
	public Integer CommentCount;
	public String AddedBy;
	public String AddedBy_URL;
	public String AddedBy_Email;
	public String AddedAt_Date;
	public Long AddedAt_Datestamp;
	
	public WordModel(Integer wordID, String word, String wordLetter, Integer orderPos, Integer nestID, String example, String ethimology, String description, String derivatives, Integer commentCount, String addedBy, String addedBy_URL, String addedBy_Email, String addedAt_Date, long addedAt_Datestamp) {
		this.WordID = wordID;
		this.Word = word;
		this.WordLetter = wordLetter;
		this.OrderPos = orderPos;
		this.NestID = nestID;
		this.Example = example;
		this.Ethimology = ethimology;
		this.Description = description;
		this.Derivatives = derivatives;
		this.CommentCount = commentCount;
		this.AddedBy = addedBy;
		this.AddedBy_URL = addedBy_URL;
		this.AddedBy_Email = addedBy_Email;
		this.AddedAt_Date = addedAt_Date;
		this.AddedAt_Datestamp = addedAt_Datestamp;
	}
}
