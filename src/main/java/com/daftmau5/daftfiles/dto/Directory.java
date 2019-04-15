package com.daftmau5.daftfiles.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Directory {
	@Id
	int id;
	String nome;
	String description;

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
