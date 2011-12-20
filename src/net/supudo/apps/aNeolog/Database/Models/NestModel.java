package net.supudo.apps.aNeolog.Database.Models;

public class NestModel {
	public final Integer NestID;
	public final String Title;
	public final Integer OrderPos;
	
	public NestModel(Integer nestID, String name, Integer orderPos) {
		this.NestID = nestID;
		this.Title = name;
		this.OrderPos = orderPos;
	}
}
