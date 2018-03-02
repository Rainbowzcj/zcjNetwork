package com.zcj.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.zcj.bean.Book;

/**
 * 起名无力
 * 
 * @author zhengchengjiao
 *
 */
public class Demo {

	private static Logger logger = LoggerFactory.getLogger(Demo.class);

	private static List<String> pageUrlList = null;
	private static Map<Integer, Integer> pageFlag = new HashMap<>();
	private static List<Book> bookList = new ArrayList<>();

	private static Boolean doneFlag = false;

	private static String fileName = "F:/test/demo.xls";

	public static void main(String[] args) {

		String url = "https://book.douban.com";

		// 1. 从主页获取编程标签的访问地址
		String programmingLabel = getProgrammingLabelUrl(url);
		if (StringUtils.isEmpty(programmingLabel)) {
			logger.info("获取编程标签的URL地址失败");
		} else {
			logger.info("编程标签的URL地址：" + programmingLabel);

			// 2. 从编程标签页获取分页访问地址
			String lastPageUrl = getLastPageInfo(programmingLabel); // 获取最后一页的访问信息
			if (StringUtils.isEmpty(lastPageUrl)) {
				logger.info("获取最后一页的访问信息失败");
			} else {
				logger.info("最后一页的访问信息: " + lastPageUrl);

				// 3. 计算每一页的访问地址
				getAllPageUrl(url, lastPageUrl);
				if (pageUrlList == null || pageUrlList.size() == 0) {
					logger.info("无分页访问地址或计算分页访问地址失败");
				} else {
					// 4. 多线程读取每一页的书籍信息
					getBookUrlForEveryPage();

					// 5.监控线程是否处理完成
					Long start = System.currentTimeMillis();
					while (System.currentTimeMillis() - start < 30 * 1000) {// 30秒
						// 6. 排序筛选书籍，并写入excel中
						if (doneFlag) { 
							writeExcel();
						} else {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				logger.info("编程标签的URL地址：" + programmingLabel);
			}
		}
	}

	/**
	 * 排序处理book，取前40本书，并写入文件中
	 */
	private static void writeExcel() {
		if (bookList.isEmpty()) {
			logger.info("书籍信息不存在");
		}
		quickSort(0,bookList.size()-1);
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet1");
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("序号");
		row.createCell(1).setCellValue("书名");
		row.createCell(2).setCellValue("评分");
		row.createCell(3).setCellValue("评价人数");
		row.createCell(4).setCellValue("作者");
		row.createCell(5).setCellValue("出版社");
		row.createCell(6).setCellValue("出版日期");
		row.createCell(7).setCellValue("价格");
		for (int i = 0; i < bookList.size()&&i<40; i++) {
			 row = sheet.createRow(i+1);
			if (bookList.get(i) != null) {
				row.createCell(0).setCellValue(i+1);
				row.createCell(1).setCellValue(bookList.get(i).getName());
				row.createCell(2).setCellValue(bookList.get(i).getScore());
				row.createCell(3).setCellValue(bookList.get(i).getPl());
				row.createCell(4).setCellValue(bookList.get(i).getAuthor());
				row.createCell(5).setCellValue(bookList.get(i).getPubCompany());
				row.createCell(6).setCellValue(bookList.get(i).getPubDate());
				row.createCell(7).setCellValue(bookList.get(i).getPrice());
			}
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = os.toByteArray();
		File file = new File(fileName);// Excel文件生成后存储的位置。
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content);
			os.close();
			fos.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 快速排序
	 * @param array
	 * @param low
	 * @param high
	 */
	private static void quickSort(int low, int high) {// 传入low=0，high=array.length-1;
        int  p_pos, i;// p_pos->位索引;pivot->轴值。
        Book pivot=null;
        Book t=null;
        if (low < high) {
            p_pos = low;
            pivot = bookList.get(low);
            for (i = low + 1; i <= high; i++)
                if (bookList.get(i).getScore() > pivot.getScore()) {
                    p_pos++;
                    t = bookList.get(p_pos);
                    bookList.set(p_pos, bookList.get(i));
                    bookList.set(i, t);
                }
            t = bookList.get(low);
            bookList.set(low, bookList.get(p_pos));
            bookList.set(p_pos, t);
            // 分而治之
            quickSort( low, p_pos - 1);// 排序左半部分
            quickSort( p_pos + 1, high);// 排序右半部分
        }
	}

	/**
	 * 多线程获取每一页的书籍信息
	 */
	private static void getBookUrlForEveryPage() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for (String url : pageUrlList) {
			// 多线程处理
			threadPool.submit(new Runnable() {
				@Override
				public void run() {
					Demo.getBookInfo(url, pageUrlList.indexOf(url));
				}
			});
		}
		threadPool.shutdown(); // 线程池不会立刻退出，直到添加到线程池中的任务都已经处理完成，才会退出。
	}

	/**
	 * 获取书籍信息
	 * 
	 * @param url
	 * @param index
	 */
	private static void getBookInfo(String url, int num) {
		logger.info("获取分页：" + url + "的书籍信息");
		BufferedReader br = null;
		InputStream is = null;
		try {
			if ((is = getUrlInfo(url)) != null) {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				String line;
				StringBuilder sb = new StringBuilder();
				// 正则表达式的匹配规则提取该网页的链接
				Integer index = 0;

				Integer beg = null;
				// 读取书单
				while ((line = br.readLine()) != null) {
					// 去除空行
					if (line.replaceAll(" ", "").length() >= 0) {

						if (line.contains("<ul class=\"subject-list\">")) {
							beg = index;
						}
						// 获取<ul class="subject-list"></ul>标签内的书籍清单
						if (beg != null && index > beg) {
							if (line.contains("</ul>")) {
								break;
							}
							sb.append(line);
						}
						index++;
					}
				}
				// 读取出每一本书籍的信息
				getEachBookInfo(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 书籍信息获取完成
			if (num == pageUrlList.size() - 1) {
				done();
			}
		}
	}

	/**
	 * 多线程处理完成
	 */
	private static void done() {
		doneFlag = true;
	}

	/**
	 * 读取每一本书的信息
	 * 
	 * @param string
	 */
	static	Pattern pattern = Pattern.compile(
			"<h2.*?>(.*?)</h2>[\\s\\S]*?<div class=\"pub\">(.*?)</div>[\\s\\S]*?<span class=\"rating_nums\">(.*?)</span>[\\s\\S]*?<span class=\"pl\">(.*?)</span>",
			Pattern.DOTALL);
	static Pattern namePattern = Pattern.compile("<a.*?>.*?</a>", Pattern.DOTALL);
	private static void getEachBookInfo(String bookInfo) {
		if(StringUtils.isEmpty(bookInfo)){
			return;
		}
		// 现在运用正则表达式对数据进行抽取提取
		Matcher m = pattern.matcher(bookInfo);
		while (m.find()) {
			Book book = new Book();

			if (m.groupCount() > 0) {
				// 设置书籍名
				Matcher m1 = namePattern.matcher(m.group(1));
				if (m1.find()) {
					book.setName(m1.group().replaceAll("</?[a|span][^>]*>", "").trim().replaceAll("\\s+", " "));
				}
				// 设置书籍的出版信息
				String pub = m.group(2);
				String[] p = pub.split("/");
				if (p != null && p.length > 2) {
					book.setAuthor(p[0].trim().replaceAll("\\s+", " "));
					book.setPrice(p[p.length - 1].trim().replaceAll("\\s+", " "));
					book.setPubDate(p[p.length - 2].trim().replaceAll("\\s+", " "));
					book.setPubCompany(p.length < 4 ? "" : p[p.length - 3].trim().replaceAll("\\s+", " "));
				}

				// 设置评价分数
				String score = m.group(3).replaceAll("</?span[^>]*>", "").trim().replaceAll("\\s+", " ");
				book.setScore(Double.valueOf(score));
				// 设置评价人数
				String pl = m.group(4).replaceAll("</?span[^>]*>", "").replaceAll("[^0-9]", "");
				book.setPl(Integer.valueOf(pl));
				System.out.println(book.toString());
				if (book.getPl() > 1000) {
					synchronized (bookList) {
						bookList.add(book);
					}
				}
			}
		}

	}

	


	/**
	 * 获取所有页面的访问地址
	 * 
	 * @param url
	 * @param lastPageUrl
	 */
	private static void getAllPageUrl(String url, String lastPageUrl) {

		try {
			Integer totalPage = Integer.valueOf(lastPageUrl.replaceAll("</?a[^>]*>", "").replaceAll(" ", ""));

			lastPageUrl = lastPageUrl.substring(lastPageUrl.indexOf("href=\"") + "href=\"".length());
			lastPageUrl = lastPageUrl.substring(0, lastPageUrl.indexOf("\""));
			System.out.println(lastPageUrl);

			String prefix = lastPageUrl.substring(0, lastPageUrl.indexOf("start=") + "start=".length());
			String suffix = lastPageUrl.substring(lastPageUrl.indexOf("&amp"));

			Integer curNum = Integer.valueOf(
					lastPageUrl.substring(lastPageUrl.indexOf(prefix) + prefix.length(), lastPageUrl.indexOf(suffix)));

			System.out.println(curNum);

			Integer pageSize = curNum / (totalPage - 1);
			if (totalPage > 0 && pageSize > 0) {
				pageUrlList = new ArrayList<>(totalPage);
				for (int i = 0; i < totalPage; i++) {
					pageFlag.put(i, 0);
					pageUrlList.add(url + prefix + i * pageSize + suffix);
				}
			}

		} catch (Exception e) {
			logger.info("计算每一页的访问地址失败：", e);
		}
	}

	/**
	 * 建立连接信息
	 * 
	 * @param urlInfo
	 * @return
	 */
	public static InputStream getUrlInfo(String urlInfo) throws Exception {
		// 读取目的网页URL地址，获取网页源码
		try {
			URL url = new URL(urlInfo);
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			InputStream is = httpUrl.getInputStream();
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取编程标签的URL地址
	 * 
	 * @param urlInfo
	 * @return
	 */
	public static String getProgrammingLabelUrl(String urlInfo) {
		BufferedReader br = null;
		InputStream is = null;
		try {
			if ((is = getUrlInfo(urlInfo)) == null) {
				return null;
			}
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			// 正则表达式的匹配规则提取该网页的链接
			while ((line = br.readLine()) != null) {
				// 去除空行
				if (line.replaceAll(" ", "").length() == 0) {
					continue;
				}

				if (line.contains("<a href") && line.contains(">编程</a>")) {
					System.out.println(line);
					line = line.substring(line.indexOf("href=\"") + "href=\"".length());
					return urlInfo + line.substring(0, line.indexOf("\""));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getLastPageInfo(String urlInfo) {

		BufferedReader br = null;
		InputStream is = null;
		try {
			if ((is = getUrlInfo(urlInfo)) != null) {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				String line;
				String lastPageLine = "";
				// 正则表达式的匹配规则提取该网页的链接
				while ((line = br.readLine()) != null) {
					// 去除空行
					if (line.replaceAll(" ", "").length() > 0) {
						if (line.contains("<span class=\"next\"")) {
							System.out.println(line);
							// lastPageLine =
							// lastPageLine.substring(lastPageLine.indexOf("href=\"")
							// + "href=\"".length());
							// return lastPageLine.substring(0,
							// lastPageLine.indexOf("\""));
							return lastPageLine;
						}
						lastPageLine = line;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
