package com.heidigi.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.heidigi.model.JollyData;
import com.heidigi.model.JollyFno;
import com.heidigi.model.JollyFutures;
import com.heidigi.model.JollyMapData;
import com.heidigi.model.JollyStock;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "MYTRADE")
public class MyTradingSetupController {

	static RestTemplate template = new RestTemplate();
	static HttpHeaders headers = new HttpHeaders();
	static HttpEntity<String> entity = null;

	{
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		headers.set("Referer", "https://www.samco.in/bhavcopy-nse-bse-mcx");

		entity = new HttpEntity<String>(headers);

	}

	List<JollyFno> futures = new ArrayList<>();
	List<JollyStock> Delivery = new ArrayList<>();

	@RequestMapping(value = "getLastPrice")
	public List<JollyData> getLastPrice() throws Exception {

		List<JollyFno> futures = new ArrayList<>();
		List<JollyMapData> chartData = new ArrayList<>();
		URL url = new URL("https://www.samco.in/");
		URLConnection con = url.openConnection();

		String setCookie = "";

		for (int i = 0;; i++) {
			String headerName = con.getHeaderFieldKey(i);
			String headerValue = con.getHeaderField(i);

			if (null != headerName && headerName.equals("Set-Cookie"))
				setCookie = headerValue;

			if (headerName == null && headerValue == null) {
				break;
			}

		}

		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		localDate = localDate.minusDays(1);
		for (int d = 0; d < 10; d++) {

			// System.out.println(localDate.format(formatter1));

			String urlParameters = "start_date=" + localDate.format(formatter1) + "&end_date="
					+ localDate.format(formatter1)
					+ "&show_or_down=1&bhavcopy_data%5B%5D=NSE&bhavcopy_data%5B%5D=NSEFO";
			url = new URL("https://www.samco.in/bse_nse_mcx/getBhavcopy");
			con = url.openConnection();

			System.out.println(urlParameters);

			con.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

			writer.write(urlParameters);
			writer.flush();
			String line1;
			String data = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

			while ((line1 = reader.readLine()) != null) {
				Boolean loop = false;
				int index1 = 0;
				do {
					index1 = line1.indexOf("https://www.samco.in/bse_nse_mcx/datacopy/", index1);
					if (index1 != -1) {
						data += line1.substring(index1, line1.indexOf("\"", index1)) + " - ";
						index1 = line1.indexOf("\"", index1);
						loop = true;
					} else
						loop = false;
				} while (loop);

			}
			System.out.println(data);
			writer.close();
			reader.close();

			String urls[] = data.split(" - ");

			try {
				url = new URL(urls[0].trim());

				con = url.openConnection();
			} catch (Exception ex) {
				System.out.println(ex);
				localDate = localDate.minusDays(1);
				continue;
			}
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.0; pl; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2");
			con.addRequestProperty("Referer", "https://www.samco.in/bhavcopy-nse-bse-mcx");
			con.setRequestProperty("Cookie", setCookie);
			con.setRequestProperty("Expires", " Thu, 19 Nov 1981 08:52:00 GMT");
			con.setRequestProperty("Cache-Control", "no-store, no-cache, must-revalidate");
			con.setRequestProperty("Pragma", "no-cache");
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = "";
			List<JollyStock> stocks = new ArrayList<>();
			List<JollyFno> fnos = new ArrayList<>();

			while ((line = br.readLine()) != null)
				if (line.indexOf(",EQ,") != -1)
					stocks.add(new JollyStock(line));

			url = new URL(urls[1].trim());

			con = url.openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.0; pl; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2");
			con.addRequestProperty("Referer", "https://www.samco.in/bhavcopy-nse-bse-mcx");
			con.setRequestProperty("Cookie", setCookie);
			con.setRequestProperty("Expires", " Thu, 19 Nov 1981 08:52:00 GMT");
			con.setRequestProperty("Cache-Control", "no-store, no-cache, must-revalidate");
			con.setRequestProperty("Pragma", "no-cache");
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));

			line = "";

			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
					// case insensitive to parse JAN and FEB
					.parseCaseInsensitive()
					// add pattern
					.appendPattern("dd-MMM-yyyy")
					// create formatter (use English Locale to parse month names)
					.toFormatter(Locale.ENGLISH);
			while ((line = br.readLine()) != null) {
				String cols[] = line.split(",");

				if (cols[0].equals("OPTSTK") && ChronoUnit.DAYS.between(
						getExpiryDate(LocalDate.parse(cols[cols.length - 1].trim(), formatter)),
						LocalDate.parse(cols[2].trim(), formatter)) < 6) {
//					System.out.println(line+" "+LocalDate.parse(cols[cols.length - 1].trim(), formatter) +" "+
//							LocalDate.parse(cols[2].trim(), formatter) +" "+ChronoUnit.DAYS.between(LocalDate.parse(cols[cols.length - 1].trim(), formatter),
//									LocalDate.parse(cols[2].trim(), formatter)));
					fnos.add(new JollyFno(line));
				}

				if (cols[0].equals("FUTSTK") && ChronoUnit.DAYS.between(
						getExpiryDate(LocalDate.parse(cols[cols.length - 1].trim(), formatter)),
						LocalDate.parse(cols[2].trim(), formatter)) < 6) {
//					System.out.println(line+" "+LocalDate.parse(cols[cols.length - 1].trim(), formatter) +" "+
//							LocalDate.parse(cols[2].trim(), formatter) +" "+ChronoUnit.DAYS.between(LocalDate.parse(cols[cols.length - 1].trim(), formatter),
//									LocalDate.parse(cols[2].trim(), formatter)));
					futures.add(new JollyFno(line));
				}
			}

			this.futures = futures;

			Map<String, List<JollyFno>> gdata = fnos.stream()
					.collect(Collectors.groupingBy(o -> o.getSymbol() + "-" + o.getOption_typ()));

			List<String> keys = gdata.keySet().stream().sorted().collect(Collectors.toList());

			for (int i = 0; i < keys.size(); i++) {
				JollyMapData value = gdata.get(keys.get(i)).stream()
						.sorted(Comparator.comparingDouble(JollyFno::getOpen_int).reversed()).limit(1)
						.map(o -> new JollyMapData(o,
								stocks.stream().filter(o1 -> o1.getSymbol().equals(o.getSymbol()))
										.collect(Collectors.toList()).get(0).getClose()))

						.collect(Collectors.toList()).get(0);

				chartData.add(value);

			}
			localDate = localDate.minusDays(1);
		}

		Map<String, List<JollyMapData>> mdata = chartData.stream().collect(Collectors.groupingBy(o -> o.getSymbol()));

		List<String> mkeys = mdata.keySet().stream().collect(Collectors.toList());

		List<JollyData> sdata = new ArrayList<>();

		for (int i = 0; i < mkeys.size(); i++) {
			JollyData data = new JollyData(mdata.get(mkeys.get(i)));
			sdata.add(data);
		}

		return sdata.stream().sorted(Comparator.comparing(JollyData::sort).reversed()).collect(Collectors.toList());
	}

	public LocalDate getExpiryDate(LocalDate date) {
		LocalDate lastThrusday = date.with(TemporalAdjusters.lastInMonth(DayOfWeek.THURSDAY));

		if (lastThrusday.compareTo(date) < 0)
			lastThrusday = date.plusDays(20).with(TemporalAdjusters.lastInMonth(DayOfWeek.THURSDAY));
		// System.out.println(date+" "+lastThrusday);
		return lastThrusday;
	}

	@RequestMapping(value = "getFutureOIs")
	public Collection<List<JollyFutures>> getFutureOIs() throws Exception {

		return (futures.stream().sorted(Comparator.comparing(JollyFno::getTimestamp)).map(o -> new JollyFutures(o))
				.collect(Collectors.groupingBy(o -> o.getSymbol()))).values();

	}

	@RequestMapping(value = "getDelivery")
	public void getDelivery() throws Exception {

		URL url = new URL("https://nsearchives.nseindia.com/archives/equities/mto/MTO_31072023.DAT");
		getFinalURL(url);

	}

	public URL getFinalURL(URL url) throws Exception {

		URLConnection con = url.openConnection();
		System.out.println("orignal url: " + con.getURL());
		con.connect();
		System.out.println("connected url: " + con.getURL());
		InputStream is = con.getInputStream();
		System.out.println("redirected url: " + con.getURL());
		is.close();

		return url;

	}

}
