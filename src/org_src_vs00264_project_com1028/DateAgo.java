package org_src_vs00264_project_com1028;

public enum DateAgo {
	One("One Year of Data"), Two("Two Years of Data"), Three("Three Years of Data"),
	Four("Four Years of Data"), Five("Five Years of Data"), Six("Six Years of Data"),
	Seven("Seven Years of Data"), Eight("Eight Years of Data"), Nine("Nine Years of Data"),
	Ten("Ten Years of Data");
	private final String display;
	DateAgo(String display){
		this.display = display;
	}
	@Override
	public String toString() {
		return this.display;
	}
}
