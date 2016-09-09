package com.sw.ncs.server.system.settings;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.sw.ncs.server.db.ISystemEntity;

@Entity
public class SystemSetting implements ISystemEntity{
	@Id
	private String settingKey;
	private String settingValue;
	
	SystemSetting(){
		
	}
	
	SystemSetting(String key,String value){
		this.settingKey = key;
		this.settingValue = value;
	}
	
	
	String getSettingKey() {
		return settingKey;
	}
	void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}
	String getSettingValue() {
		return settingValue;
	}
	void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}
}
