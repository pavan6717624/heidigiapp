package com.heidigi.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heidigi.domain.AmazonProduct;
import com.heidigi.model.ImageAndCategory;
import com.heidigi.model.ProductAmazon;
import com.heidigi.repository.AmazonProductRepository;

@Service
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AmazonService {

	String chatgptkey = System.getenv("chatgptkey");

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	AmazonProductRepository amazonRepository;

	public ProductAmazon createPageContent(String url) throws Exception {

		ProductAmazon chatGPTData = getChatGPTContent(url);
		
		ImageAndCategory imagecat=getImageAndCategory(url);

		chatGPTData.setImageUrl(imagecat.getImage());
		
		chatGPTData.setCategory(imagecat.getCategory());

		System.out.println(chatGPTData);

		return chatGPTData;
		

	}

	public String getPageContent(String product) {

		return "";

	}

	public Boolean savePageContent(String data) throws Exception {

		AmazonProduct product = mapper.readValue(data, AmazonProduct.class);

		product.setProductUrl(product.getProduct().trim().replaceAll(" ", "_").replaceAll("\\\\n", ""));
		
		product.setFullData(data);

		System.out.print(product);
		
		amazonRepository.save(product);

		return true;

	}

	public String getImageLink(String siteUrl) throws Exception {

		URL url = new URL(siteUrl);
		URLConnection con = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer output = new StringBuffer("");
		String line = "";
		String search = "data-old-hires=\"";
		String imageUrl = "";

		while ((line = br.readLine()) != null) {
			int index = line.indexOf(search);
			if (index != -1) {
				int index1 = line.indexOf("https", index);
				int index2 = line.indexOf("\"", index1);
				imageUrl = line.substring(index1, index2);
				break;
			}

		}

		System.out.println(imageUrl.substring(0, imageUrl.indexOf("_") + 1) + "SX569_.jpg");

		return imageUrl.substring(0, imageUrl.indexOf("_") + 1) + "SX569_.jpg";
	}
	
	public ImageAndCategory getImageAndCategory(String siteUrl) throws Exception {

		URL url = new URL(siteUrl);
		ImageAndCategory imagecat=new ImageAndCategory();
		URLConnection con = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer output = new StringBuffer("");
		String line = "";
		String search = "data-old-hires=\"";
		String search1 = "data-category=";
		String imageUrl = "";
		String categoryUrl = "";
		Boolean image=false, cat=false;

		while ((line = br.readLine()) != null) {
			int index = line.indexOf(search);
			if (index != -1 && !image) {
				int index1 = line.indexOf("https", index);
				int index2 = line.indexOf("\"", index1);
				imageUrl = line.substring(index1, index2);
				image=true;
				if(image && cat)
				break;
			}
			
			index = line.indexOf(search1);
			if (index != -1 && !cat) {
				int index1 = line.indexOf("\"", index);
				int index2 = line.indexOf("\"", index1+1);
				categoryUrl = line.substring(index1+1, index2);
				cat=true;
				if(image && cat)
				break;
			}

		}

		System.out.println(imageUrl.substring(0, imageUrl.indexOf("_") + 1) + "SX569_.jpg");
		
		imagecat.setImage(imageUrl.substring(0, imageUrl.indexOf("_") + 1) + "SX569_.jpg");
		imagecat.setCategory(categoryUrl);

		return imagecat;
	}
	
	public String getCategory(String siteUrl) throws Exception {

		URL url = new URL(siteUrl);
		URLConnection con = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer output = new StringBuffer("");
		String line = "";
		String search = "data-category=";
		String categoryUrl = "";

		while ((line = br.readLine()) != null) {
			int index = line.indexOf(search);
			if (index != -1) {
				int index1 = line.indexOf("\"", index);
				
				int index2 = line.indexOf("\"", index1+1);
				
				categoryUrl = line.substring(index1+1, index2);
				break;
			}

		}

		

		return categoryUrl;
	}

	public ProductAmazon getChatGPTContent(String siteUrl) {
		String chatGPTData = "";
		try {// API endpoint
			String url = "https://api.openai.com/v1/chat/completions";
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + chatgptkey);

			String model = "gpt-4-turbo";
			String prompt = "[{\"role\": \"user\", \"content\": \"Create an article on " + siteUrl
					+ " and it should contain Product Name (MAXIMUM 7 WORDS), Tag Line (MAXIMUM 5 WORDS), Description (MAXIMUM 20 WORDS), Key Features (6 Bullet points with subtitles, with . at end of every point), Product Specifications (6 Bullet points with subtitles, with . at end of every point), Benefits (3 Bullet points with subtitles, with . at end of every point), Why Choose (3 Bullet points with subtitles, with . at end of every point), Conclusion (MAXIMUM 25 WORDS)\"}]";
			int maxTokens = 4000;

			con.setDoOutput(true);
			String body = "{\"model\": \"" + model + "\", \"messages\": " + prompt + ", \"max_tokens\": " + maxTokens
					+ "}";

			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(body);
			writer.flush();

			System.out.println(body);

			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			int index1 = response.indexOf("\"content\":");
			int index2 = response.indexOf("\"refusal\"", index1);

			chatGPTData = (response.toString().substring(index1, index2));

//			.replaceAll("\\\\n", "")

			chatGPTData = chatGPTData.replaceAll("\\*", "").replaceAll("\\#", "").replaceAll("- ", "")
					.replaceAll("\"content\": \"", "").replaceAll("\"", "").trim();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ProductAmazon(chatGPTData);
	}

}