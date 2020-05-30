package com.edgar.paintshop;

public class Paint {
	private Integer color;
	private Boolean type;

	public Paint() {

	}

	public Paint(Integer color, Boolean type) {
		setColor(color);
		setType(type);
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	@Override
	public String toString() {
		if(getColor()==null || getType()==null) {
			return null;
		}else {
			return new StringBuilder("(").append(color).append(",").append(type).append(")").toString();
		}
	}
}
