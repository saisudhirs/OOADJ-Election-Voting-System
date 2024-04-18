package com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Candidate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(unique = true)
	private String candidate;
	private int votes;
	public int getVotes() {
		// TODO Auto-generated method stub
		return votes;
	}
	public void setVotes(int i) {
		// TODO Auto-generated method stub
		this.votes=i;
		
	}
	public void setCandidate(String string) {
		// TODO Auto-generated method stub
		this.candidate=string;
		
	}
	public void setId(int i) {
		// TODO Auto-generated method stub
		this.id=i;
		
	}

	
	

}
