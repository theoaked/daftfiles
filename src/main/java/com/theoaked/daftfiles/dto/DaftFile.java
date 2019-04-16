package com.theoaked.daftfiles.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DaftFile {
	// just a file but cooler
	@Id
	int id;
	String nome;
	String description;
	String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
