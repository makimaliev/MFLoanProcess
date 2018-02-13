package kg.gov.mf.loan.process.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Catalog extends GenericModel {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}