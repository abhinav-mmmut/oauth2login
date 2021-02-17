package com.example.oauth2impl;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Article {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String author;
	private Date date;
	private String title;
	private String content;

	public Article() {
	}

	public Article(String author,String title, String content) {
		super();
		long millis=System.currentTimeMillis();
		this.id =0;
		this.author = author;
		this.date =new java.sql.Date(millis);
		this.title = title;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", author=" + author + ", date=" + date + ", title=" + title + ", content="
				+ content + "]";
	}

}
