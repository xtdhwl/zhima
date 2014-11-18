package com.zhima.base.protocol.vo;

import java.util.ArrayList;

public class GoogleCell {
	private String version;
	private String host;
	private String radio_type;
	private ArrayList<CellIDInfo> cell_towers;
	private boolean request_address;

	public boolean isRequest_address() {
		return request_address;
	}

	public void setRequest_address(boolean request_address) {
		this.request_address = request_address;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getRadio_type() {
		return radio_type;
	}

	public void setRadio_type(String radio_type) {
		this.radio_type = radio_type;
	}

	public ArrayList<CellIDInfo> getCell_towers() {
		return cell_towers;
	}

	public void setCell_towers(ArrayList<CellIDInfo> cell_towers) {
		this.cell_towers = cell_towers;
	}

}
