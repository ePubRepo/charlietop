package com.charlietop.www.apiserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharlieCardNum {

	private int prefix;
	private long number;

	public CharlieCardNum(String charlieCardNum) throws NumberFormatException,
			IllegalArgumentException {
		Pattern p = Pattern.compile("(\\d+)-(\\d+)");
		Matcher m = p.matcher(charlieCardNum);
		if (m.find()) {
			this.prefix = Integer.parseInt(m.group(1));
			this.number = Long.parseLong(m.group(2));
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getPrefix() {
		return this.prefix;
	}

	public long getNumber() {
		return this.number;
	}
}
