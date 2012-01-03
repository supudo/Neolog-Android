package net.supudo.apps.aNeolog.Database.Models;

public class WordCommentModel {
	public final Integer CommentID;
	public final Integer WordID;
	public final String Author;
	public final String Comment;
	public final String CommentDate;
	public Long CommentDatestamp;
	
	public WordCommentModel(Integer commentID, Integer wordID, String author, String comment, String commentDate, long commentDatestamp) {
		this.CommentID = commentID;
		this.WordID = wordID;
		this.Author = author;
		this.Comment = comment;
		this.CommentDate = commentDate;
		this.CommentDatestamp = commentDatestamp;
	}
}
