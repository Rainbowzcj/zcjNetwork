package com.zcj.bean;

public class Book {


	/** 书名 */
	private String name;

	/** 评分 */
	private Double score;

	/** 评价人数 */
	private Integer pl;

	/** 作者 */
	private String author;

	/** 出版社 */
	private String pubCompany;

	/** 出版日期 */
	private String pubDate;

	/** 价格 */
	private String price;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getPl() {
		return pl;
	}

	public void setPl(Integer pl) {
		this.pl = pl;
	}

	public String getPubCompany() {
		return pubCompany;
	}

	public void setPubCompany(String pubCompany) {
		this.pubCompany = pubCompany;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Book [name=" + name + ", score=" + score + ", pl=" + pl + ", author=" + author
				+ ", pubCompany=" + pubCompany + ", pubDate=" + pubDate + ", price=" + price + "]";
	}

}
