package net.supudo.apps.aNeolog.Database.Models;

public final class SettingModel {
	public final boolean EditableYn;
	public final String SName;
	public final String SValue;
	
	public SettingModel(boolean editableYn, String sname, String svalue) {
		this.EditableYn = editableYn;
		this.SName = sname;
		this.SValue = svalue;
	}
}
