package net.supudo.apps.aNeolog.Database.Models;

public class AccountDataModel {
	public final String Name;
	public final String Email;
	public final String URL;
	public final Integer NestID;
	
	public AccountDataModel(String name, String email, String url, Integer nestID) {
		this.Name = name;
		this.Email = email;
		this.URL = url;
		this.NestID = nestID;
	}
}
